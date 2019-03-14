package li.naska.spring.ejb;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import li.naska.spring.ejb.test.FakeConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AbstractSpringSingletonBeanTest {

  @Nested
  class given_a_singleton_bean {

    private AbstractSpringSingletonBean underTest = new AbstractSpringSingletonBean() {};

    private AnnotationConfigApplicationContext anApplicationContext =
        new AnnotationConfigApplicationContext(FakeConfiguration.class);

    private AnnotationConfigApplicationContext anotherApplicationContext =
        new AnnotationConfigApplicationContext(FakeConfiguration.class);

    {
      underTest.setApplicationContext("aKey", anApplicationContext);
      underTest.setApplicationContext("anotherKey", anotherApplicationContext);
    }

    @Nested
    class when_asked_for_an_application_context {

      private ApplicationContext result = underTest.getApplicationContext("aKey");

      @Test
      public void then_it_should_be_initialized() {
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AnnotationConfigApplicationContext.class);
        assertThat(((AnnotationConfigApplicationContext) result).isActive()).isTrue();
        assertThat(((AnnotationConfigApplicationContext) result).getBean(LocalDateTime.class))
            .isNotNull();
      }

      @Nested
      class when_later_it_is_released {

        @Test
        public void then_it_should_be_closed() {
          underTest.doReleaseBean();
          assertThat(anApplicationContext.isActive()).isFalse();
        }
      }
    }

    @Nested
    class when_asked_for_an_application_context_using_the_same_key_twice {

      private ApplicationContext first = underTest.getApplicationContext("aKey");

      private ApplicationContext second = underTest.getApplicationContext("aKey");

      @Test
      public void then_both_should_be_the_same() {
        assertThat(first).isSameAs(second);
      }
    }

    @Nested
    class when_asked_for_an_application_context_using_different_keys {

      private ApplicationContext first = underTest.getApplicationContext("aKey");

      private ApplicationContext second = underTest.getApplicationContext("anotherKey");

      @Test
      public void then_both_should_be_different() {
        assertThat(first).isNotSameAs(second);
      }
    }
  }
}

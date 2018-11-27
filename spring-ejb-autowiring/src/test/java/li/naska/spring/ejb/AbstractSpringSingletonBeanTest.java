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

    @Nested
    class when_asked_for_an_application_context {

      private ApplicationContext result =
          underTest.getApplicationContext("aKey", FakeConfiguration.class);

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
          ApplicationContext result = underTest.getApplicationContext("aKey", Object.class);
          underTest.doReleaseBean();
          assertThat(((AnnotationConfigApplicationContext) result).isActive()).isFalse();
        }
      }
    }

    @Nested
    class when_asked_for_an_application_context_using_the_same_key_twice {

      private ApplicationContext first =
          underTest.getApplicationContext("aKey", FakeConfiguration.class);

      private ApplicationContext second =
          underTest.getApplicationContext("aKey", FakeConfiguration.class);

      @Test
      public void then_both_should_be_the_same() {
        assertThat(first).isSameAs(second);
      }
    }

    @Nested
    class when_asked_for_an_application_context_using_different_keys {

      private ApplicationContext first =
          underTest.getApplicationContext("aKey", FakeConfiguration.class);

      private ApplicationContext second =
          underTest.getApplicationContext("anotherKey", FakeConfiguration.class);

      @Test
      public void then_both_should_be_different() {
        assertThat(first).isNotSameAs(second);
      }
    }
  }
}

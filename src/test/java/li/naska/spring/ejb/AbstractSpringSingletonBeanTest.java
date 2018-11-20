package li.naska.spring.ejb;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import li.naska.spring.ejb.test.FakeConfiguration;

public class AbstractSpringSingletonBeanTest {

  private AbstractSpringSingletonBean underTest = new AbstractSpringSingletonBean() {
  };

  @Test
  public void shouldReturnInitializedApplicationContext() {
    // WHEN
    ApplicationContext result = underTest.getApplicationContext(Object.class, FakeConfiguration.class);
    // THEN
    assertThat(result).isNotNull();
    assertThat(result).isInstanceOf(AnnotationConfigApplicationContext.class);
    assertThat(((AnnotationConfigApplicationContext) result).isActive()).isTrue();
    assertThat(((AnnotationConfigApplicationContext) result).getBean(LocalDateTime.class)).isNotNull();
  }

  @Test
  public void shouldReturnSameReferenceIfSameKey() {
    // WHEN
    ApplicationContext first = underTest.getApplicationContext(Object.class, FakeConfiguration.class);
    ApplicationContext second = underTest.getApplicationContext(Object.class, FakeConfiguration.class);
    // THEN
    assertThat(first).isSameAs(second);
  }

  @Test
  public void shouldReturnDifferentReferenceIfDifferentKeys() {
    // WHEN
    ApplicationContext first = underTest.getApplicationContext(Integer.class, FakeConfiguration.class);
    ApplicationContext second = underTest.getApplicationContext(String.class, FakeConfiguration.class);
    // THEN
    assertThat(first).isNotSameAs(second);
  }

  @Test
  public void shouldCloseApplicationContextOnRelease() {
    // GIVEN
    ApplicationContext result = underTest.getApplicationContext(Object.class, Object.class);
    // WHEN
    underTest.doReleaseBean();
    // THEN
    assertThat(((AnnotationConfigApplicationContext) result).isActive()).isFalse();
  }

}

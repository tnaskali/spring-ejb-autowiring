package li.naska.spring.ejb.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import javax.ejb.EJBException;
import javax.interceptor.InvocationContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
public class AbstractSpringAutowiringInterceptorTest {

  @Nested
  class given_a_spring_interceptor {

    @Mock
    private ConfigurableListableBeanFactory beanFactory;

    @Mock
    private ApplicationContext applicationContext;

    private AbstractSpringAutowiringInterceptor underTest =
        new AbstractSpringAutowiringInterceptor() {
          @Override
          protected ApplicationContext getApplicationContext(String key) {
            return applicationContext;
          }
        };

    @Nested
    class when_autowiring {

      private Consumer<InvocationContext> method = ic -> underTest.autowireBean(ic);

      @Test
      public void given_invocation_succeeds_then_proceed() throws Exception {
        // GIVEN
        InvocationContext invocationContext = mock(InvocationContext.class);
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        when(invocationContext.getTarget()).thenReturn(underTest);
        // WHEN
        method.accept(invocationContext);
        // THEN
        verify(invocationContext).proceed();
      }

      @Test
      public void given_checked_exception_is_thrown_then_wrap_it_in_an_ejb_exception()
          throws Exception {
        InvocationContext invocationContext = mock(InvocationContext.class);
        Exception exception = new Exception();
        // GIVEN
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        when(invocationContext.getTarget()).thenReturn(underTest);
        when(invocationContext.proceed()).thenThrow(exception);
        // WHEN
        try {
          method.accept(invocationContext);
          fail("exception expected but not thrown");
        } catch (EJBException e) {
          // THEN
          assertThat(e.getCause()).isEqualTo(exception);
        }
      }

      @Test
      public void given_runtime_exception_is_thrown_then_propagate() throws Exception {
        InvocationContext invocationContext = mock(InvocationContext.class);
        RuntimeException exception = new RuntimeException();
        // GIVEN
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        when(invocationContext.getTarget()).thenReturn(underTest);
        when(invocationContext.proceed()).thenThrow(exception);
        // WHEN
        try {
          method.accept(invocationContext);
          fail("exception expected but not thrown");
        } catch (RuntimeException e) {
          // THEN
          assertThat(e).isEqualTo(exception);
        }
      }
    }
  }
}

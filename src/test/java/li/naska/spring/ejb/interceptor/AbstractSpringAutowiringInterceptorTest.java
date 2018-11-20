package li.naska.spring.ejb.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ejb.EJBException;
import javax.interceptor.InvocationContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
public class AbstractSpringAutowiringInterceptorTest {

  @Mock
  private ConfigurableListableBeanFactory beanFactory;

  @Mock
  private ApplicationContext applicationContext;

  private AbstractSpringAutowiringInterceptor underTest = new AbstractSpringAutowiringInterceptor() {
    @Override
    protected ApplicationContext getApplicationContext(Object target) {
      return applicationContext;
    }
  };

  @Test
  public void autowireBean_GivenInvocationProceeds() throws Exception {
    InvocationContext invocationContext = mock(InvocationContext.class);
    // GIVEN
    when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
    when(invocationContext.getTarget()).thenReturn(underTest);
    // WHEN
    underTest.autowireBean(invocationContext);
    // THEN
    verify(invocationContext).proceed();
  }

  @Test
  public void autowireBean_GivenCheckedExceptionIsThrown() throws Exception {
    InvocationContext invocationContext = mock(InvocationContext.class);
    Exception exception = new Exception();
    // GIVEN
    when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
    when(invocationContext.getTarget()).thenReturn(underTest);
    when(invocationContext.proceed()).thenThrow(exception);
    // WHEN
    try {
      underTest.autowireBean(invocationContext);
      fail("exception expected but not thrown");
    } catch (EJBException e) {
      // THEN
      assertThat(e.getCause()).isEqualTo(exception);
    }
  }

  @Test
  public void autowireBean_GivenRuntimeExceptionIsThrown() throws Exception {
    InvocationContext invocationContext = mock(InvocationContext.class);
    RuntimeException exception = new RuntimeException();
    // GIVEN
    when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
    when(invocationContext.getTarget()).thenReturn(underTest);
    when(invocationContext.proceed()).thenThrow(exception);
    // WHEN
    try {
      underTest.autowireBean(invocationContext);
      fail("exception expected but not thrown");
    } catch (RuntimeException e) {
      // THEN
      assertThat(e).isEqualTo(exception);
    }
  }

}

package li.naska.spring.ejb.test;

import javax.ejb.EJB;

import org.springframework.context.ApplicationContext;

import li.naska.spring.ejb.interceptor.AbstractSpringAutowiringInterceptor;

public class FakeSpringAutowiringInterceptor extends AbstractSpringAutowiringInterceptor {

  @EJB
  private FakeSpringSingletonBean singletonBean;

  @Override
  protected ApplicationContext getApplicationContext(Object target) {
    return singletonBean.getApplicationContext(target.getClass(), FakeConfiguration.class);
  }

}

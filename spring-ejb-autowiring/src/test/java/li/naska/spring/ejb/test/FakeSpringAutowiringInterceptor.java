package li.naska.spring.ejb.test;

import javax.ejb.EJB;
import li.naska.spring.ejb.AbstractSpringSingletonBean;
import li.naska.spring.ejb.interceptor.AbstractSpringAutowiringInterceptor;

public class FakeSpringAutowiringInterceptor extends AbstractSpringAutowiringInterceptor {

  @EJB
  private FakeSpringSingletonBean singletonBean;

  @Override
  protected AbstractSpringSingletonBean getSpringSingletonBean() {
    return singletonBean;
  }

  @Override
  protected Class<?>[] getAnnotatedClasses() {
    return new Class<?>[] { FakeConfiguration.class };
  }

}

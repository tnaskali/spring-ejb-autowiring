package li.naska.spring.ejb.test;

import javax.ejb.EJB;
import li.naska.spring.ejb.interceptor.AbstractSpringAutowiringInterceptor;
import org.springframework.context.ApplicationContext;

public class FakeSpringAutowiringInterceptor extends AbstractSpringAutowiringInterceptor {

  @EJB
  private FakeSpringSingletonBean singletonBean;

  @Override
  protected ApplicationContext getApplicationContext(String key) {
    return singletonBean.getApplicationContext(key, FakeConfiguration.class);
  }

}

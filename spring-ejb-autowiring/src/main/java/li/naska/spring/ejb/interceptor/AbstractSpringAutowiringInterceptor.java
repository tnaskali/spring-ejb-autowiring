package li.naska.spring.ejb.interceptor;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.PostActivate;
import javax.interceptor.InvocationContext;
import li.naska.spring.ejb.AbstractSpringSingletonBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;

/**
 * Adapted from {@code org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor}, which
 * has been removed in Spring 5.x.
 * 
 * @see <a href=
 *      "https://jira.spring.io/browse/SPR-16821">https://jira.spring.io/browse/SPR-16821</a>
 */
public abstract class AbstractSpringAutowiringInterceptor {

  /**
   * Autowire the target bean after construction as well as after passivation.
   * 
   * @param invocationContext the EJB invocation context
   */
  @PostConstruct
  @PostActivate
  public void autowireBean(InvocationContext invocationContext) {
    doAutowireBean(invocationContext.getTarget());
    try {
      invocationContext.proceed();
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      // Cannot declare a checked exception on WebSphere here - so we need to wrap.
      throw new EJBException(ex);
    }
  }

  /**
   * Actually autowire the target bean after construction/passivation.
   * 
   * @param target the target bean to autowire
   */
  protected void doAutowireBean(Object target) {
    AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
    configureBeanPostProcessor(bpp, target);
    bpp.setBeanFactory(getBeanFactory());
    bpp.processInjection(target);
  }

  /**
   * Template method for configuring the {@link AutowiredAnnotationBeanPostProcessor} used for
   * autowiring the target bean.
   * 
   * @param processor the AutowiredAnnotationBeanPostProcessor to configure
   * @param target the target bean to autowire with this processor
   */
  protected void configureBeanPostProcessor(AutowiredAnnotationBeanPostProcessor processor,
      Object target) {}

  /**
   * Provides the key for accessing the Spring context. The default implementation is the
   * interceptor's class name, which is the logical choice when the interceptor always provides the
   * same configuration classes.
   * 
   * @return the key
   */
  protected String getKey() {
    return getClass().getName();
  }

  /**
   * Determine the BeanFactory for autowiring the given target bean.
   * 
   * @param target the target bean to autowire
   * @return the BeanFactory to use for autowiring
   */
  protected BeanFactory getBeanFactory() {
    return getApplicationContext(getKey()).getAutowireCapableBeanFactory();
  }

  /**
   * Retreives the ApplicationContext to use for autowiring. The implementor is expected to return
   * equivalent ApplicationContext instances every time it's being passed the same key.
   * 
   * @param key the key identifying the ApplicationContext
   * @return the ApplicationContext corresponding to the given key
   */
  protected ApplicationContext getApplicationContext(String key) {
    return getSpringSingletonBean().getApplicationContext(key, getAnnotatedClasses());
  }

  /**
   * Template method to provide a reference to the singleton Bean holding the Spring
   * ApplicationContexts.
   * 
   * @return the Spring singleton bean
   */
  protected abstract AbstractSpringSingletonBean getSpringSingletonBean();

  /**
   * Template method to provide the Spring configuration classes used to build the
   * ApplicationContext.
   * 
   * @return the Spring configuration classes
   */
  protected abstract Class<?>[] getAnnotatedClasses();

}

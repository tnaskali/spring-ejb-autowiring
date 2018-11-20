package li.naska.spring.ejb.interceptor;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.PostActivate;
import javax.interceptor.InvocationContext;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;

/**
 * Adapted from {@link org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor}, which has been removed in
 * Spring 5.x.
 * 
 * @see <a href="https://jira.spring.io/browse/SPR-16821">https://jira.spring.io/browse/SPR-16821</a>
 */
public abstract class AbstractSpringAutowiringInterceptor {

  /**
   * Autowire the target bean after construction as well as after passivation.
   * 
   * @param invocationContext the EJB3 invocation context
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
    bpp.setBeanFactory(getBeanFactory(target));
    bpp.processInjection(target);
  }

  /**
   * Template method for configuring the {@link AutowiredAnnotationBeanPostProcessor} used for autowiring.
   * 
   * @param processor the AutowiredAnnotationBeanPostProcessor to configure
   * @param target the target bean to autowire with this processor
   */
  protected void configureBeanPostProcessor(AutowiredAnnotationBeanPostProcessor processor, Object target) {
  }

  /**
   * Determine the BeanFactory for autowiring the given target bean.
   * 
   * @param target the target bean to autowire
   * @return the BeanFactory to use (never {@code null})
   * @see #getBeanFactoryReference
   */
  protected BeanFactory getBeanFactory(Object target) {
    return getApplicationContext(target).getAutowireCapableBeanFactory();
  }

  /**
   * Determine the BeanFactoryReference for the given target bean.
   * <p>
   * The default implementation delegates to {@link #getBeanFactoryLocator} and {@link #getBeanFactoryLocatorKey}.
   * 
   * @param target the target bean to autowire
   * @return the BeanFactoryReference to use (never {@code null})
   * @see #getBeanFactoryLocator
   * @see #getBeanFactoryLocatorKey
   * @see org.springframework.beans.factory.access.BeanFactoryLocator#useBeanFactory(String)
   */
  protected abstract ApplicationContext getApplicationContext(Object target);

}

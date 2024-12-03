/*
 * Copyright (c) 2018 Thomas Naskali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package li.naska.spring.ejb.interceptor;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJBException;
import jakarta.ejb.PostActivate;
import jakarta.interceptor.InvocationContext;
import li.naska.spring.ejb.AbstractSpringSingletonBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
   * @return the BeanFactory to use for autowiring
   */
  protected BeanFactory getBeanFactory() {
    return getApplicationContext(getKey()).getAutowireCapableBeanFactory();
  }

  /**
   * Retrieves the ApplicationContext to use for autowiring. The implementor is expected to return
   * equivalent ApplicationContext instances every time it's being passed the same key.
   * 
   * @param key the key identifying the ApplicationContext
   * @return the ApplicationContext corresponding to the given key
   */
  protected ApplicationContext getApplicationContext(String key) {
    AbstractSpringSingletonBean springSingletonBean = getSpringSingletonBean();
    synchronized (key.intern()) {
      ConfigurableApplicationContext applicationContext = springSingletonBean.getApplicationContext(key);
      if (applicationContext == null) {
        applicationContext = new AnnotationConfigApplicationContext(getAnnotatedClasses());
        springSingletonBean.setApplicationContext(key, applicationContext);
      }
      return applicationContext;
    }
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

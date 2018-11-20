package li.naska.spring.ejb;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import javax.annotation.PreDestroy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import li.naska.spring.ejb.interceptor.AbstractSpringAutowiringInterceptor;

/**
 * Abstract class to extend when implementing a {@link javax.ejb.Singleton} EJB responsible for holding the shared
 * Spring ConfigurableApplicationContext instances (one instance per bean type). </br>
 * Access it from an implementation of {@link AbstractSpringAutowiringInterceptor} after injecting it using
 * {@link javax.ejb.EJB}
 */
public abstract class AbstractSpringSingletonBean {

  /**
   * Holder for the ConfigurableApplicationContext instances
   */
  private final Map<Class<?>, ConfigurableApplicationContext> applicationContextReferences = new WeakHashMap<>();

  /**
   * Retrieves an ApplicationContext, creating it beforehand if it not already present.
   * 
   * @param key the key under which the ApplicationContext will be stored
   * @param configuration the spring configuration classes from which the ApplicationContext should be created
   * @return the corresponding ApplicationContext
   */
  public synchronized ApplicationContext getApplicationContext(Class<?> key, Class<?>... configuration) {
    if (!applicationContextReferences.containsKey(key)) {
      this.applicationContextReferences.put(key, new AnnotationConfigApplicationContext(configuration));
    }
    return applicationContextReferences.get(key);
  }

  @PreDestroy
  public void doReleaseBean() {
    for (Iterator<Entry<Class<?>, ConfigurableApplicationContext>> iterator = applicationContextReferences.entrySet()
      .iterator(); iterator.hasNext();) {
      iterator.next().getValue().close();
      iterator.remove();
    }
  }

}

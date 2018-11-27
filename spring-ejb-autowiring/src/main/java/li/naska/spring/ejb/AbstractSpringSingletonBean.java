package li.naska.spring.ejb;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import javax.annotation.PreDestroy;
import li.naska.spring.ejb.interceptor.AbstractSpringAutowiringInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Abstract class to extend when implementing a {@link javax.ejb.Singleton} EJB responsible for
 * holding the shared Spring ConfigurableApplicationContext instances (one instance per bean type).
 * <br>
 * Access it from an implementation of {@link AbstractSpringAutowiringInterceptor} after injecting
 * it using {@link javax.ejb.EJB}.
 */
public abstract class AbstractSpringSingletonBean {

  /**
   * Holder for the ConfigurableApplicationContext instances.
   */
  private final Map<String, ConfigurableApplicationContext> applicationContextReferences =
      new WeakHashMap<>();

  /**
   * Retrieves an ApplicationContext, creating it beforehand if it not already present.
   * 
   * @param key the key identifying the ApplicationContext
   * @param annotedClasses the spring configuration classes from which the ApplicationContext should
   *        be created
   * @return the ApplicationContext stored under the given key
   */
  public synchronized ApplicationContext getApplicationContext(String key,
      Class<?>... annotedClasses) {
    if (!applicationContextReferences.containsKey(key)) {
      this.applicationContextReferences.put(key,
          new AnnotationConfigApplicationContext(annotedClasses));
    }
    return applicationContextReferences.get(key);
  }

  @PreDestroy
  void doReleaseBean() {
    for (Iterator<Entry<String, ConfigurableApplicationContext>> iterator =
        applicationContextReferences.entrySet().iterator(); iterator.hasNext();) {
      iterator.next().getValue().close();
      iterator.remove();
    }
  }

}

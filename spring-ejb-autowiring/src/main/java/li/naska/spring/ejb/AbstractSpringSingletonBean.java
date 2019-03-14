package li.naska.spring.ejb;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import javax.annotation.PreDestroy;
import li.naska.spring.ejb.interceptor.AbstractSpringAutowiringInterceptor;
import org.springframework.context.ConfigurableApplicationContext;

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
   * Retrieves the ApplicationContext stored under the given key.
   * 
   * @param key the key identifying the ApplicationContext
   * @return the ApplicationContext stored under the given key
   */
  public ConfigurableApplicationContext getApplicationContext(String key) {
    return applicationContextReferences.get(key);
  }

  /**
   * Stores an ApplicationContext under the provided key.
   * 
   * @param key the key identifying the ApplicationContext
   * @param applicationContext the ApplicationContext to store
   */
  public void setApplicationContext(String key, ConfigurableApplicationContext applicationContext) {
    this.applicationContextReferences.put(key, applicationContext);
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

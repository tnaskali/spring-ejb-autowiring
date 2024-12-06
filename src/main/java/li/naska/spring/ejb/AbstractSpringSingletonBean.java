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
package li.naska.spring.ejb;

import jakarta.annotation.PreDestroy;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import li.naska.spring.ejb.interceptor.AbstractSpringAutowiringInterceptor;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Abstract class to extend when implementing a {@link jakarta.ejb.Singleton} EJB responsible for
 * holding the shared Spring ConfigurableApplicationContext instances (one instance per bean type).
 * <br>
 * Access it from an implementation of {@link AbstractSpringAutowiringInterceptor} after injecting
 * it using {@link jakarta.ejb.EJB}.
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
            applicationContextReferences.entrySet().iterator();
        iterator.hasNext(); ) {
      iterator.next().getValue().close();
      iterator.remove();
    }
  }
}

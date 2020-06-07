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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import li.naska.spring.ejb.test.FakeConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AbstractSpringSingletonBeanTest {

  @Nested
  class given_a_singleton_bean {

    private AbstractSpringSingletonBean underTest = new AbstractSpringSingletonBean() {};

    private AnnotationConfigApplicationContext anApplicationContext =
        new AnnotationConfigApplicationContext(FakeConfiguration.class);

    private AnnotationConfigApplicationContext anotherApplicationContext =
        new AnnotationConfigApplicationContext(FakeConfiguration.class);

    {
      underTest.setApplicationContext("aKey", anApplicationContext);
      underTest.setApplicationContext("anotherKey", anotherApplicationContext);
    }

    @Nested
    class when_asked_for_an_application_context {

      private ApplicationContext result = underTest.getApplicationContext("aKey");

      @Test
      public void then_it_should_be_initialized() {
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AnnotationConfigApplicationContext.class);
        assertThat(((AnnotationConfigApplicationContext) result).isActive()).isTrue();
        assertThat(((AnnotationConfigApplicationContext) result).getBean(LocalDateTime.class))
            .isNotNull();
      }

      @Nested
      class when_later_it_is_released {

        @Test
        public void then_it_should_be_closed() {
          underTest.doReleaseBean();
          assertThat(anApplicationContext.isActive()).isFalse();
        }
      }
    }

    @Nested
    class when_asked_for_an_application_context_using_the_same_key_twice {

      private ApplicationContext first = underTest.getApplicationContext("aKey");

      private ApplicationContext second = underTest.getApplicationContext("aKey");

      @Test
      public void then_both_should_be_the_same() {
        assertThat(first).isSameAs(second);
      }
    }

    @Nested
    class when_asked_for_an_application_context_using_different_keys {

      private ApplicationContext first = underTest.getApplicationContext("aKey");

      private ApplicationContext second = underTest.getApplicationContext("anotherKey");

      @Test
      public void then_both_should_be_different() {
        assertThat(first).isNotSameAs(second);
      }
    }
  }
}

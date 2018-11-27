package li.naska.spring.ejb;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import li.naska.spring.ejb.test.FakeStatelessSessionBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class StatelessSessionBeanTest {

  @BeforeAll
  public static void setupLogger() {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
    Logger.getLogger("").setLevel(Level.INFO);
  }

  @Nested
  class given_a_deployed_environment {

    private EJBContainer ejbContainer = mountContainer();

    @Nested
    class when_looking_up_a_stateless_session_bean {

      private FakeStatelessSessionBean stateless = lookupSessionBean();

      @Test
      public void then_it_should_be_autowired() throws NamingException {
        assertThat(stateless).isNotNull();
        assertThat(stateless.getAutowired()).isNotNull();
      }

    }

    private FakeStatelessSessionBean lookupSessionBean() {
      try {
        return (FakeStatelessSessionBean) ejbContainer.getContext()
            .lookup("java:global/test-classes/FakeStatelessSessionBean");
      } catch (NamingException e) {
        throw new IllegalStateException(e);
      }
    }

    private EJBContainer mountContainer() {
      Map<String, Object> properties = new HashMap<>();
      properties.put(EJBContainer.MODULES, new File("target/test-classes"));
      return EJBContainer.createEJBContainer(properties);
    }
  }
}

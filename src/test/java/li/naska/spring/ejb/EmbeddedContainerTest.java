package li.naska.spring.ejb;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import li.naska.spring.ejb.test.FakeSpringSingletonBean;
import li.naska.spring.ejb.test.FakeStatelessSessionBean;

public class EmbeddedContainerTest {

  private static EJBContainer ejbContainer;

  @BeforeAll
  public static void before() {
    Map<String, Object> properties = new HashMap<>();
    properties.put(EJBContainer.MODULES, new File("target/test-classes"));
    properties.put("org.glassfish.ejb.embedded.glassfish.installation.root", "glassfish");
    // properties.put(EJBContainer.APP_NAME, "weducation");
    ejbContainer = EJBContainer.createEJBContainer(properties);
  }

  @Test
  public void test() throws NamingException {
    Context ctx = ejbContainer.getContext();
    FakeSpringSingletonBean singleton = (FakeSpringSingletonBean) ctx
      .lookup("java:global/test-classes/FakeSpringSingletonBean");
    assertThat(singleton).isNotNull();
    FakeStatelessSessionBean stateless = (FakeStatelessSessionBean) ctx
      .lookup("java:global/test-classes/FakeStatelessSessionBean");
    assertThat(stateless).isNotNull();
    assertThat(stateless.getAutowired()).isNotNull();
  }

}

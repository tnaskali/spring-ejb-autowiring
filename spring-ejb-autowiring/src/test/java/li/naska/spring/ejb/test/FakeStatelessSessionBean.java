package li.naska.spring.ejb.test;

import java.time.LocalDateTime;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;

@Stateless
@LocalBean
@Interceptors(FakeSpringAutowiringInterceptor.class)
public class FakeStatelessSessionBean {

  @Autowired
  private LocalDateTime autowired;

  public LocalDateTime getAutowired() {
    return autowired;
  }

}

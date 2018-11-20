package li.naska.spring.ejb.test;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FakeConfiguration {

  @Bean
  public LocalDateTime now() {
    return LocalDateTime.now();
  }

}

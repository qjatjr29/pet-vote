package numble.pet.vote.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mongo")
@Getter
@AllArgsConstructor
public class MongoProperties {

  private final String uri;
  private final String database;
  private final String username;
  private final String password;

}

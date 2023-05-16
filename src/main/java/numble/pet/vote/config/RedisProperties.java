package numble.pet.vote.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.redis")
@Getter
@AllArgsConstructor
public class RedisProperties {

  private final String host;

  private final int port;
}

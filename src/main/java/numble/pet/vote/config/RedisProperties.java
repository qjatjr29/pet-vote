package numble.pet.vote.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "spring.redis")
@ConstructorBinding
@Getter
@AllArgsConstructor
public class RedisProperties {

  private final String host;

  private final int port;
}

package numble.pet.vote.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka")
@Getter
@RequiredArgsConstructor
public class KafkaProperties {

  private final String bootstrapServers;
  private final String keySerializer;
  private final String valueSerializer;
  private final Topic topic;


  @Getter
  public static class Topic {
    private final String voteEvent;

    public Topic(String voteEvent) {
      this.voteEvent = voteEvent;
    }
  }

}

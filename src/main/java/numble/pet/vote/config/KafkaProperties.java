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
    private final String voteSubmitEvent;
    private final String voteCancelEvent;
    private final String petUpdateEvent;

    public Topic(String voteSubmitEvent, String voteCancelEvent, String petUpdateEvent) {
      this.voteSubmitEvent = voteSubmitEvent;
      this.voteCancelEvent = voteCancelEvent;
      this.petUpdateEvent = petUpdateEvent;
    }
  }

}

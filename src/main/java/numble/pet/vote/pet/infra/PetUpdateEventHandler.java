package numble.pet.vote.pet.infra;

import numble.pet.vote.config.KafkaProperties;
import numble.pet.vote.pet.command.domain.PetUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PetUpdateEventHandler {

  private final KafkaProperties kafkaProperties;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public PetUpdateEventHandler(KafkaProperties kafkaProperties,
      KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaProperties = kafkaProperties;
    this.kafkaTemplate = kafkaTemplate;
  }

  @EventListener(PetUpdatedEvent.class)
  public void voteSubmitHandle(PetUpdatedEvent event) {
    kafkaTemplate.send(kafkaProperties.getTopic().getPetUpdateEvent(), event);
  }

}

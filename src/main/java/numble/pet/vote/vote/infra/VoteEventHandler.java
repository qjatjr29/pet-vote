package numble.pet.vote.vote.infra;

import numble.pet.vote.config.KafkaProperties;
import numble.pet.vote.vote.command.domain.VoteCanceledEvent;
import numble.pet.vote.vote.command.domain.VoteSubmittedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class VoteEventHandler {

  private final KafkaProperties kafkaProperties;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public VoteEventHandler(KafkaProperties kafkaProperties,
      KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaProperties = kafkaProperties;
    this.kafkaTemplate = kafkaTemplate;
  }

  @EventListener(VoteSubmittedEvent.class)
  public void voteSubmitHandle(VoteSubmittedEvent event) {
    kafkaTemplate.send(kafkaProperties.getTopic().getVoteSubmitEvent(), event);
  }

  @EventListener(VoteCanceledEvent.class)
  public void voteCancelHandle(VoteCanceledEvent event) {
    kafkaTemplate.send(kafkaProperties.getTopic().getVoteCancelEvent(), event);
  }
}

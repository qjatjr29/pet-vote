package numble.pet.vote.pet.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import numble.pet.vote.pet.command.application.PetService;
import numble.pet.vote.pet.query.application.PetQueryService;
import numble.pet.vote.vote.command.domain.VoteCanceledEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VoteCanceledEventListener {
  private static final String VOTE_CANCEL_EVENT_BUFFER_KEY = "vote_cancel_event_buffer";

  private final PetService petService;
  private final PetQueryService petQueryService;
  private final RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  public VoteCanceledEventListener(PetService petService,
      PetQueryService petQueryService,
      RedisTemplate<String, Object> redisTemplate) {
    this.petService = petService;
    this.petQueryService = petQueryService;
    this.redisTemplate = redisTemplate;
  }

  @KafkaListener(topics = "${spring.kafka.topic.vote-cancel-event}", groupId = "${spring.kafka.group-id}")
  public void submit(String message) {
    try {
      VoteCanceledEvent voteCanceledEvent = objectMapper.readValue(message, VoteCanceledEvent.class);
      addEvent(voteCanceledEvent);
    } catch (JsonProcessingException e) {
      log.error("Error - vote submit");
      e.printStackTrace();
    }
  }

  private void addEvent(VoteCanceledEvent event) {
    Long petId = event.getPetId();
    redisTemplate.opsForHash().increment(VOTE_CANCEL_EVENT_BUFFER_KEY, petId, 1L);
  }

  @Scheduled(fixedRate = 10000)
  public void processBufferedEvents() {

    Map<Object, Object> bufferedEvents = redisTemplate.opsForHash().entries(VOTE_CANCEL_EVENT_BUFFER_KEY);

    for (Map.Entry<Object, Object> entry : bufferedEvents.entrySet()) {
      Long petId  = (Long) entry.getKey();
      int count = (Integer) entry.getValue();

      petService.cancelVote(petId, count);
      petQueryService.cancelVote(petId, count);

    }

    redisTemplate.delete(VOTE_CANCEL_EVENT_BUFFER_KEY);
  }
}

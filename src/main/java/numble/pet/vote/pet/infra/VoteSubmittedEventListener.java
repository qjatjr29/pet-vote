package numble.pet.vote.pet.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import numble.pet.vote.pet.command.application.PetService;
import numble.pet.vote.pet.query.application.PetQueryService;
import numble.pet.vote.vote.command.domain.VoteSubmittedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VoteSubmittedEventListener {

  private static final String VOTE_SUBMIT_EVENT_BUFFER_KEY = "vote_submit_event_buffer";

  private final PetService petService;
  private final PetQueryService petQueryService;
  private final RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  public VoteSubmittedEventListener(PetService petService,
      PetQueryService petQueryService,
      RedisTemplate<String, Object> redisTemplate) {
    this.petService = petService;
    this.petQueryService = petQueryService;
    this.redisTemplate = redisTemplate;
  }

  @KafkaListener(topics = "${spring.kafka.topic.vote-submit-event}", groupId = "${spring.kafka.group-id}")
  public void submit(String message) {
    try {
      VoteSubmittedEvent voteSubmittedEvent = objectMapper.readValue(message, VoteSubmittedEvent.class);
      addEvent(voteSubmittedEvent);
    } catch (JsonProcessingException e) {
      log.error("Error - vote submit");
      e.printStackTrace();
    }
  }

  private void addEvent(VoteSubmittedEvent event) {
    Long petId = event.getPetId();
    redisTemplate.opsForHash().increment(VOTE_SUBMIT_EVENT_BUFFER_KEY, petId, 1L);
  }

  @Scheduled(fixedRate = 10000)
  public void processBufferedEvents() {

    Map<Object, Object> bufferedEvents = redisTemplate.opsForHash().entries(VOTE_SUBMIT_EVENT_BUFFER_KEY);
    for (Map.Entry<Object, Object> entry : bufferedEvents.entrySet()) {

      Long petId  = (Long) entry.getKey();
      int count = (Integer) entry.getValue();

      // MongoDB에서 해당 PetId에 대한 데이터를 가져오고, count 값만큼 voteCount를 증가시킴
      petService.submitVote(petId, count);
      petQueryService.submitVote(petId, count);
    }

    redisTemplate.delete(VOTE_SUBMIT_EVENT_BUFFER_KEY);
  }

}

package numble.pet.vote.vote.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import numble.pet.vote.pet.query.domain.PetData;
import numble.pet.vote.pet.query.domain.PetQueryRepository;
import numble.pet.vote.vote.command.application.VoteService;
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

  private final VoteService voteService;
  private final PetQueryRepository petQueryRepository;
  private final RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  public VoteCanceledEventListener(VoteService voteService,
      PetQueryRepository petQueryRepository,
      RedisTemplate<String, Object> redisTemplate) {
    this.voteService = voteService;
    this.petQueryRepository = petQueryRepository;
    this.redisTemplate = redisTemplate;
  }

  @KafkaListener(topics = "${spring.kafka.topic.vote-cancel-event}")
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
      String petIdStr = (String) entry.getKey();
      Long count = (Long) entry.getValue();
      Long petId = Long.valueOf(petIdStr);

      PetData petData = petQueryRepository.findById(petId)
          .orElseThrow(RuntimeException::new);

      petData.subtractVoteCount(count);
      petQueryRepository.save(petData);
    }

    redisTemplate.delete(VOTE_CANCEL_EVENT_BUFFER_KEY);
  }
}

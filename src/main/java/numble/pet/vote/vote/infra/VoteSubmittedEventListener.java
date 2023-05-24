package numble.pet.vote.vote.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import numble.pet.vote.pet.query.domain.PetData;
import numble.pet.vote.pet.query.domain.PetQueryRepository;
import numble.pet.vote.vote.command.application.VoteService;
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

  private final VoteService voteService;
  private final PetQueryRepository petQueryRepository;
  private final RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  public VoteSubmittedEventListener(VoteService voteService,
      PetQueryRepository petQueryRepository,
      RedisTemplate<String, Object> redisTemplate) {
    this.voteService = voteService;
    this.petQueryRepository = petQueryRepository;
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
      String petIdStr = (String) entry.getKey();
      Long count = (Long) entry.getValue();
      Long petId = Long.valueOf(petIdStr);

      // MongoDB에서 해당 PetId에 대한 데이터를 가져오고, count 값만큼 voteCount를 증가시킴
      PetData petData = petQueryRepository.findById(petId)
          .orElseThrow(RuntimeException::new);

      petData.addVoteCount(count);
      petQueryRepository.save(petData);
    }

    redisTemplate.delete(VOTE_SUBMIT_EVENT_BUFFER_KEY);
  }

}

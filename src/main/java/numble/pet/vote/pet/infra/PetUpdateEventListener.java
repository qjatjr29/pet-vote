package numble.pet.vote.pet.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import numble.pet.vote.common.exception.ErrorCode;
import numble.pet.vote.common.exception.NotFoundException;
import numble.pet.vote.pet.command.domain.Pet;
import numble.pet.vote.pet.command.domain.PetEventType;
import numble.pet.vote.pet.command.domain.PetRepository;
import numble.pet.vote.pet.command.domain.PetUpdatedEvent;
import numble.pet.vote.pet.query.domain.PetData;
import numble.pet.vote.pet.query.domain.PetQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PetUpdateEventListener {

  private static final String PET_CREATE_EVENT_BUFFER = "pet_create_event_buffer";
  private static final String PET_UPDATE_EVENT_BUFFER = "pet_update_event_buffer";
  private static final String PET_DELETE_EVENT_BUFFER = "pet_delete_event_buffer";

  private final PetRepository petRepository;
  private final PetQueryRepository petQueryRepository;

  @Autowired
  private ObjectMapper objectMapper;

  public PetUpdateEventListener(PetRepository petRepository,
      PetQueryRepository petQueryRepository) {
    this.petRepository = petRepository;
    this.petQueryRepository = petQueryRepository;
  }

  @KafkaListener(topics = "${spring.kafka.topic.pet-update-event}", groupId = "${spring.kafka.group-id}")
  public void submit(String message) {
    try {
      PetUpdatedEvent petUpdatedEvent = objectMapper.readValue(message, PetUpdatedEvent.class);

      Long petId = petUpdatedEvent.getPetId();
      PetEventType petEventType = petUpdatedEvent.getPetEventType();

      Pet pet = petRepository.findById(petId)
          .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));

      if(petEventType.equals(PetEventType.CREATE) || petEventType.equals(PetEventType.UPDATE)) {

        PetData petData = PetData.builder()
            .id(pet.getId())
            .name(pet.getName())
            .species(pet.getSpecies().name())
            .image(pet.getImage())
            .description(pet.getDescription())
            .voteCount(pet.getVoteCount())
            .createdAt(pet.getCreatedAt())
            .updatedAt(pet.getUpdatedAt())
            .build();

        petQueryRepository.save(petData);
      }
      else if(petEventType.equals(PetEventType.DELETE)) {
        petQueryRepository.deleteById(petId);
      }
    } catch (JsonProcessingException e) {
      log.error("Error - vote submit");
      e.printStackTrace();
    }
  }


}

package numble.pet.vote.pet.infra;

import java.util.Optional;
import numble.pet.vote.config.KafkaProperties;
import numble.pet.vote.pet.command.domain.Pet;
import numble.pet.vote.pet.command.domain.PetEventType;
import numble.pet.vote.pet.command.domain.PetRepository;
import numble.pet.vote.pet.command.domain.PetUpdatedEvent;
import numble.pet.vote.pet.command.domain.Species;
import numble.pet.vote.pet.query.domain.PetData;
import numble.pet.vote.pet.query.domain.PetQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Pet Update Event 관련 테스트")
class PetUpdateEventListenerTest {

  @Autowired
  private KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  private KafkaProperties kafkaProperties;

  @MockBean
  private PetRepository petRepository;

  @MockBean
  private PetQueryRepository petQueryRepository;

  private Pet pet;

  @BeforeEach
  void setup() {
    pet = Pet.builder()
        .id(1L)
        .name("pet")
        .species(Species.강아지)
        .description("귀여운 강아지")
        .build();
  }

  @Test
  @DisplayName("pet 생성 이벤트 리스너 성공 테스트")
  void petCreateEventListener_success() throws InterruptedException {

    // given
    PetUpdatedEvent petUpdatedEvent = new PetUpdatedEvent(1L, PetEventType.CREATE);

    // when
    Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
    kafkaTemplate.send(kafkaProperties.getTopic().getPetUpdateEvent(), petUpdatedEvent);

    Thread.sleep(1000);

    // then
    Mockito.verify(petQueryRepository).save(Mockito.any(PetData.class));
  }

  @Test
  @DisplayName("pet 정보 수정 이벤트 리스너 성공 테스트")
  void petUpdateEventListener_success() throws InterruptedException {

    // given
    PetUpdatedEvent petUpdatedEvent = new PetUpdatedEvent(1L, PetEventType.UPDATE);

    // when
    Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
    kafkaTemplate.send(kafkaProperties.getTopic().getPetUpdateEvent(), petUpdatedEvent);

    Thread.sleep(1000);

    // then
    Mockito.verify(petQueryRepository).save(Mockito.any(PetData.class));
  }

  @Test
  @DisplayName("pet 삭제 이벤트 리스너 성공 테스트")
  void petDeleteEventListener_success() throws InterruptedException {

    // given
    PetUpdatedEvent petUpdatedEvent = new PetUpdatedEvent(1L, PetEventType.DELETE);

    // when
    Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
    kafkaTemplate.send(kafkaProperties.getTopic().getPetUpdateEvent(), petUpdatedEvent);

    Thread.sleep(1000);

    // then
    Mockito.verify(petQueryRepository).deleteById(Mockito.any(Long.class));
  }

}
package numble.pet.vote.pet.command.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import numble.pet.vote.common.event.Event;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetUpdatedEvent extends Event {

  // todo: Pet에 대한 정보가 생성 또는 수정, 삭제가 되었을 때
  private Long petId;
  private PetEventType petEventType;

  public PetUpdatedEvent(Long petId, PetEventType petEventType) {
    this.petId = petId;
    this.petEventType = petEventType;
  }

  public Long getPetId() {
    return petId;
  }

  public PetEventType getPetEventType() {
    return petEventType;
  }
}

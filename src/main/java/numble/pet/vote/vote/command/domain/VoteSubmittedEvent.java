package numble.pet.vote.vote.command.domain;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import numble.pet.vote.common.event.Event;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteSubmittedEvent extends Event {

  private Long petId;

  public VoteSubmittedEvent(Long petId) {
    this.petId = petId;
  }

  public Long getPetId() {
    return petId;
  }
}

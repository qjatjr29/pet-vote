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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("VoteSubmittedEvent{");
    sb.append("petId=").append(petId);
    sb.append('}');
    return sb.toString();
  }
}

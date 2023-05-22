package numble.pet.vote.vote.command.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteCanceledEvent {

  private Long petId;

  public VoteCanceledEvent(Long petId) {
    this.petId = petId;
  }

  public Long getPetId() {
    return petId;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("VoteCanceledEvent{");
    sb.append("petId=").append(petId);
    sb.append('}');
    return sb.toString();
  }
}

package numble.pet.vote.vote.command.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteCanceledEvent {

  private Long petId;

  public Long getPetId() {
    return petId;
  }
}

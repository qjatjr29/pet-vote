package numble.pet.vote.vote.command.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteCancelRequest {

  private String email;
  private Long petId;

}

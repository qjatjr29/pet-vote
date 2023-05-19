package numble.pet.vote.pet.query.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.pet.vote.pet.query.domain.PetData;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetDetailResponse {

  public static PetDetailResponse of(PetData pet) {
    return new PetDetailResponse();
  }

}

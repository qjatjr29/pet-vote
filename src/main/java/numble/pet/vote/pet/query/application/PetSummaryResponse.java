package numble.pet.vote.pet.query.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.pet.vote.pet.query.domain.PetData;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetSummaryResponse {

  private Long id;
  private String name;
  private String description;
  private Long voteCount;

  private PetSummaryResponse(final Long id, final String name, final String description, final Long voteCount) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.voteCount = voteCount;
  }

  public static PetSummaryResponse of(final PetData pet) {
    return new PetSummaryResponse(pet.getId(), pet.getName(), pet.getDescription(), pet.getVoteCount());
  }
}

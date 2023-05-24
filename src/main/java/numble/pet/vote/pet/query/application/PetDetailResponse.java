package numble.pet.vote.pet.query.application;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.pet.vote.pet.query.domain.PetData;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetDetailResponse {

  private Long id;
  private String name;
  private String species;
  private String description;
  private String image;
  private Long voteCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private PetDetailResponse(final PetData pet) {
    this.id = pet.getId();
    this.name = pet.getName();
    this.species = pet.getSpecies();
    this.description = pet.getDescription();
    this.image = pet.getImage();
    this.voteCount = pet.getVoteCount();
    this.createdAt = pet.getCreatedAt();
    this.updatedAt = pet.getUpdatedAt();
  }

  public static PetDetailResponse of(PetData pet) {
    return new PetDetailResponse(pet);
  }

}

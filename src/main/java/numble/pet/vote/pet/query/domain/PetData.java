package numble.pet.vote.pet.query.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "pet")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PetData implements Serializable {

  @Id
  private String id;

  @Field(name = "pet_id")
  private Long petId;

  @Field(name = "pet_name")
  private String name;

  @Field(name = "image")
  private String image;

  @Field(name = "species")
  private String species;

  @Field(name = "description")
  private String description;

  @Field(name = "vote_count")
  private Long voteCount;

  @Field(name = "created_at")
  private LocalDateTime createdAt;

  @Field(name = "updated_at")
  private LocalDateTime updatedAt;

  public void addVoteCount(int count) {
    this.voteCount += count;
  }

  public void subtractVoteCount(int count) {
    this.voteCount -= count;
  }

  public void update(String name, String species, String description) {
    if(!name.isBlank()) setName(name);
    if(!species.isBlank()) setSpecies(species);
    if(!description.isBlank()) setDescription(description);
  }

  private void setName(String name) {
    this.name = name;
  }

  private void setSpecies(String species) {
    this.species = species;
  }

  private void setDescription(String description) {
    this.description = description;
  }
}

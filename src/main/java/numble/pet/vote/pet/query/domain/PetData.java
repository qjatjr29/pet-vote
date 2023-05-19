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
  private Long id;

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

}

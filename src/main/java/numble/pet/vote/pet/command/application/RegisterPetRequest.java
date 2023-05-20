package numble.pet.vote.pet.command.application;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RegisterPetRequest {

  private String name;
  private String Species;
  private String description;
  private String image;

}

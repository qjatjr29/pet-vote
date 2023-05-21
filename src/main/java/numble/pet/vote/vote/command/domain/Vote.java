package numble.pet.vote.vote.command.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.pet.vote.common.domain.BaseEntity;

@Entity
@Table(name = "vote")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Vote extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "voter_email")
  private String email;

  @Column(name = "pet_id")
  private Long petId;

  public Vote(String email, Long petId) {
    this.email = email;
    this.petId = petId;
  }
}

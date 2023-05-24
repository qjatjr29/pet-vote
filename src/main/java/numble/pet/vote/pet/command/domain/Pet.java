package numble.pet.vote.pet.command.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "pet")
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE pet SET is_deleted = true WHERE id = ?")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Pet extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pet_id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "species")
  @Enumerated(EnumType.STRING)
  private Species species;

  @Column(name = "image_url")
  @Builder.Default
  private String image = "";

  @Column(name = "description")
  private String description;

  @Column(name = " vote_count")
  @Builder.Default
  private Long voteCount = 0L;

  @Column(name = "is_deleted")
  @Builder.Default
  private Boolean isDeleted = Boolean.FALSE;

  public void increaseVoteCount() {
    this.voteCount++;
  }

  public void decreaseVoteCount() {
    this.voteCount--;
  }

  public void update(String name, Species species, String description) {
    this.name = name;
    this.species = species;
    this.description = description;
  }

  public void changeImage(String image) {
    if(!image.isBlank()) setImage(image);
  }

  private void setImage(String image) {
    this.image = image;
  }
}

package numble.pet.vote.vote.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

  @Query("SELECT v FROM Vote v WHERE v.email =:email")
  Boolean existsByEmail(@Param("email") String email);
}

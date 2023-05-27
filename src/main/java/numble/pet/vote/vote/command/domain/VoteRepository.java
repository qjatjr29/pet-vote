package numble.pet.vote.vote.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

  Boolean existsByEmail(String email);

  void deleteByEmail(String email);
}

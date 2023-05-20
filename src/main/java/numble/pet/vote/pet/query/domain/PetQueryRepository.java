package numble.pet.vote.pet.query.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PetQueryRepository extends MongoRepository<PetData, Long> {

  Page<PetData> findAll(Pageable pageable);

  @Query("{ 'petId' : ?0 }")
  Optional<PetData> findByPetId(Long petId);
}

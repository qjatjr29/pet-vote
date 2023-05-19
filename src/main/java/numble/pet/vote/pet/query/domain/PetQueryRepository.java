package numble.pet.vote.pet.query.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PetQueryRepository extends MongoRepository<PetData, Long> {

  Page<PetData> findAll(Pageable pageable);

}

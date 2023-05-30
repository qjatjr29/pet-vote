package numble.pet.vote.pet.query.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PetQueryRepository extends MongoRepository<PetData, Long>,
    QuerydslPredicateExecutor<PetData>,
    UserQueryRepositoryWrapper {

  List<PetData> findAll();

  Page<PetData> findAll(Pageable pageable);

  @Query("{ 'id' : ?0 }")
  Optional<PetData> findById(Long id);

  Optional<PetData> findByPetId(Long id);

  void deleteByPetId(Long petId);
}

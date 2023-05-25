package numble.pet.vote.pet.query.application;

import numble.pet.vote.common.presentation.RestPage;
import numble.pet.vote.pet.query.domain.PetQueryRepository;
import numble.pet.vote.pet.query.domain.PetData;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PetQueryService {

  private final PetQueryRepository petQueryRepository;

  public PetQueryService(PetQueryRepository petQueryRepository) {
    this.petQueryRepository = petQueryRepository;
  }

  @Cacheable(value = "petCacheStore")
  public Page<PetSummaryResponse> findAllPets(Pageable pageable) {
    return new RestPage<>(petQueryRepository.findAll(pageable).map(PetSummaryResponse::of));
  }

  @Cacheable(key = "#id", value = "petCacheStore")
  public PetDetailResponse findPetById(Long id) {
    PetData petData = petQueryRepository.findById(id)
        .orElseThrow(RuntimeException::new);

    return PetDetailResponse.of(petData);
  }

}

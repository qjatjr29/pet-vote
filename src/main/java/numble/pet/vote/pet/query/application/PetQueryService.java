package numble.pet.vote.pet.query.application;

import numble.pet.vote.pet.query.domain.PetQueryRepository;
import numble.pet.vote.pet.query.domain.PetData;
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

  public Page<PetSummaryResponse> findAllPets(Pageable pageable) {
    return petQueryRepository.findAll(pageable).map(PetSummaryResponse::of);
  }

  public PetDetailResponse findPetById(Long id) {
    PetData petData = petQueryRepository.findById(id)
        .orElseThrow(RuntimeException::new);

    return PetDetailResponse.of(petData);
  }

}

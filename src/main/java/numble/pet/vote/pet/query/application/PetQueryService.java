package numble.pet.vote.pet.query.application;

import numble.pet.vote.common.exception.ErrorCode;
import numble.pet.vote.common.exception.NotFoundException;
import numble.pet.vote.common.presentation.RestPage;
import numble.pet.vote.pet.query.domain.PetQueryRepository;
import numble.pet.vote.pet.query.domain.PetData;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

  @Transactional
  @CacheEvict(value = "petAllCacheStore", allEntries = true)
  public void savePet(Long id, PetData petData) {
    petQueryRepository.save(petData);
  }

  @Cacheable(value = "petAllCacheStore")
  public Page<PetSummaryResponse> findAllPets(Pageable pageable) {
    return new RestPage<>(petQueryRepository.findAll(pageable).map(PetSummaryResponse::of));
  }

  public Page<PetSummaryResponse> findAllPetsSortedBySortType(Pageable pageable, String sortType) {
    return petQueryRepository
        .findAllSortedBy(SortType.of(sortType), pageable)
        .map(PetSummaryResponse::of);
  }

  @Cacheable(key = "#id", value = "petAllCacheStore")
  public PetDetailResponse findPetById(Long id) {
    PetData petData = petQueryRepository.findByPetId(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));

    return PetDetailResponse.of(petData);
  }

  @Transactional
  @Caching(evict = {
      @CacheEvict(value = "petCacheStore", allEntries = true),
      @CacheEvict(value = "petAllCacheStore", allEntries = true)
  })
  public void deletePetById(Long id) {
    petQueryRepository.deleteByPetId(id);
  }

  @Transactional
  @Caching(evict = {
      @CacheEvict(value = "petCacheStore", allEntries = true),
      @CacheEvict(value = "petAllCacheStore", allEntries = true)
  })
  public void updatePet(Long id, String name, String species, String description) {
    PetData petData = petQueryRepository.findByPetId(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));
    petData.update(name, species, description);
    petQueryRepository.save(petData);
  }

  @Transactional
  @Caching(evict = {
      @CacheEvict(value = "petCacheStore", allEntries = true),
      @CacheEvict(value = "petAllCacheStore", allEntries = true)
  })
  public void submitVote(Long id, int count) {
    PetData petData = petQueryRepository.findByPetId(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));
    petData.addVoteCount(count);
    petQueryRepository.save(petData);
  }

  @Transactional
  @Caching(evict = {
      @CacheEvict(value = "petCacheStore", allEntries = true),
      @CacheEvict(value = "petAllCacheStore", allEntries = true)
  })
  public void cancelVote(Long id, int count) {
    PetData petData = petQueryRepository.findByPetId(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));
    petData.subtractVoteCount(count);
    petQueryRepository.save(petData);
  }
}

package numble.pet.vote.pet.infra;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import numble.pet.vote.common.exception.ErrorCode;
import numble.pet.vote.common.exception.NotFoundException;
import numble.pet.vote.pet.command.domain.Pet;
import numble.pet.vote.pet.command.domain.PetRepository;
import numble.pet.vote.pet.query.domain.PetData;
import numble.pet.vote.pet.query.domain.PetQueryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PetDataSynchronizationScheduler {

  private final PetRepository petRepository;
  private final PetQueryRepository petQueryRepository;

  public PetDataSynchronizationScheduler(
      PetRepository petRepository,
      PetQueryRepository petQueryRepository) {
    this.petRepository = petRepository;
    this.petQueryRepository = petQueryRepository;
  }

  @Scheduled(cron = "0 0 0/1 * * *")
  @Caching(evict = {
      @CacheEvict(value = "petCacheStore", allEntries = true),
      @CacheEvict(value = "petAllCacheStore", allEntries = true)
  })
  public void petDataSynchronization() {

    // petQueryRepository에서 모든 PetData 정보를 가져옵니다.
    List<PetData> petDataList = petQueryRepository.findAll();

    for (PetData petData : petDataList) {
      Long petId = petData.getPetId();

      // petRepository에서 해당 petId에 해당하는 Pet 데이터를 가져옵니다.
      Pet pet = petRepository.findById(petId)
          .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));

      // petQueryRepository의 PetData를 petRepository의 데이터로 업데이트합니다.
      petData.synchronization(pet.getName(),
          pet.getSpecies().name(),
          pet.getDescription(),
          pet.getImage(),
          pet.getVoteCount());
    }

    // 업데이트된 PetData를 petQueryRepository에 저장합니다.
    petQueryRepository.saveAll(petDataList);
  }
}

package numble.pet.vote.pet.command.application;

import numble.pet.vote.common.event.Events;
import numble.pet.vote.common.exception.ErrorCode;
import numble.pet.vote.common.exception.NotFoundException;
import numble.pet.vote.pet.command.domain.Pet;
import numble.pet.vote.pet.command.domain.PetEventType;
import numble.pet.vote.pet.command.domain.PetRepository;
import numble.pet.vote.pet.command.domain.PetUpdatedEvent;
import numble.pet.vote.pet.command.domain.Species;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PetService {

  private final PetRepository petRepository;

  public PetService(PetRepository petRepository) {
    this.petRepository = petRepository;
  }


  public Pet register(String name, String species, String description) {

    Pet pet = Pet.builder()
        .name(name)
        .species(Species.of(species))
        .description(description)
        .build();

    Pet savedPet = petRepository.save(pet);

    PetUpdatedEvent petUpdatedEvent = new PetUpdatedEvent(savedPet.getId(), PetEventType.CREATE);
    Events.raise(petUpdatedEvent);
    return savedPet;
  }

  public Pet registerImage(Long id, String imageUrl) {
    Pet pet = petRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));

    pet.changeImage(imageUrl);
    return petRepository.save(pet);
  }

  public Pet update(Long id, String name, String species, String description) {

    Pet pet = petRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));

    pet.update(name, Species.of(species), description);
    Pet updatedPet = petRepository.save(pet);
    petRepository.flush();

    PetUpdatedEvent petUpdatedEvent = new PetUpdatedEvent(updatedPet.getId(), PetEventType.UPDATE);
    Events.raise(petUpdatedEvent);

    return updatedPet;
  }

  public void delete(Long petId) {

    Pet pet = petRepository.findById(petId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));

    petRepository.deleteById(petId);
    PetUpdatedEvent petUpdatedEvent = new PetUpdatedEvent(pet.getId(), PetEventType.DELETE);
    Events.raise(petUpdatedEvent);

  }
}

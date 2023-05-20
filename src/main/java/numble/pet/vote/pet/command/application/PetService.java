package numble.pet.vote.pet.command.application;

import numble.pet.vote.common.event.Events;
import numble.pet.vote.pet.command.domain.Pet;
import numble.pet.vote.pet.command.domain.PetEventType;
import numble.pet.vote.pet.command.domain.PetRepository;
import numble.pet.vote.pet.command.domain.PetUpdatedEvent;
import numble.pet.vote.pet.command.domain.Species;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PetService {

  private final PetRepository petRepository;

  public PetService(PetRepository petRepository) {
    this.petRepository = petRepository;
  }


  public Pet register(String name, String species, String description, String image) {
    Pet pet = Pet.builder()
        .name(name)
        .species(Species.of(species))
        .description(description)
        .image(image)
        .build();

    Pet savedPet = petRepository.save(pet);

    PetUpdatedEvent petUpdatedEvent = new PetUpdatedEvent(savedPet.getId(), PetEventType.CREATE);
    Events.raise(petUpdatedEvent);
    return savedPet;
  }

  public Pet update(Long id, String name, String species, String description, String image) {

    // todo: exception 변경
    Pet pet = petRepository.findById(id)
        .orElseThrow(RuntimeException::new);

    pet.update(name, Species.of(species), description, image);
    Pet updatedPet = petRepository.save(pet);

    PetUpdatedEvent petUpdatedEvent = new PetUpdatedEvent(updatedPet.getId(), PetEventType.UPDATE);
    Events.raise(petUpdatedEvent);

    return updatedPet;
  }
}

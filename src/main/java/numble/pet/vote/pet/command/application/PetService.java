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

}

package numble.pet.vote.pet.presentation;

import numble.pet.vote.pet.command.application.PetService;
import numble.pet.vote.pet.query.application.PetDetailResponse;
import numble.pet.vote.pet.query.application.PetQueryService;
import numble.pet.vote.pet.query.application.PetSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pets")
public class PetController {

  private final PetService petService;
  private final PetQueryService petQueryService;

  public PetController(PetService petService,
      PetQueryService petQueryService) {
    this.petService = petService;
    this.petQueryService = petQueryService;
  }

  @GetMapping
  public ResponseEntity<Page<PetSummaryResponse>> findAllPets(@PageableDefault(page = 0, size = 15) Pageable pageable) {
    Page<PetSummaryResponse> response = petQueryService.findAllPets(pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{petId}")
  public ResponseEntity<PetDetailResponse> findPetById(@PathVariable Long petId) {
    PetDetailResponse response = petQueryService.findPetById(petId);
    return ResponseEntity.ok(response);
  }

}

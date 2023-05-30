package numble.pet.vote.pet.presentation;

import java.net.URI;
import numble.pet.vote.pet.command.application.PetService;
import numble.pet.vote.pet.command.application.RegisterPetRequest;
import numble.pet.vote.pet.command.application.UpdatePetRequest;
import numble.pet.vote.pet.command.domain.Pet;
import numble.pet.vote.pet.infra.AwsS3Service;
import numble.pet.vote.pet.query.application.PetDetailResponse;
import numble.pet.vote.pet.query.application.PetQueryService;
import numble.pet.vote.pet.query.application.PetSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pets")
public class PetController {

  private final PetService petService;
  private final PetQueryService petQueryService;
  private final AwsS3Service awsS3Service;

  public PetController(PetService petService,
      PetQueryService petQueryService, AwsS3Service awsS3Service) {
    this.petService = petService;
    this.petQueryService = petQueryService;
    this.awsS3Service = awsS3Service;
  }

  @PostMapping
  public ResponseEntity<Pet> registerPet(@RequestBody RegisterPetRequest request) {

    Pet pet = petService.register(request.getName(), request.getSpecies(), request.getDescription());
    return ResponseEntity.created(URI.create("")).body(pet);
  }

  @PostMapping("/image/{petId}")
  public ResponseEntity<Pet> registerPetImage(@PathVariable Long petId, @RequestParam(value = "image") MultipartFile imageFile) {
    String image = awsS3Service.upload(imageFile);
    Pet pet = petService.registerImage(petId, image);
    return ResponseEntity.ok(pet);
  }

  @PutMapping("/{petId}")
  public ResponseEntity<Pet> updatePet(@PathVariable Long petId, @RequestBody UpdatePetRequest request) {
    Pet pet = petService.update(petId, request.getName(), request.getSpecies(), request.getDescription());
    return ResponseEntity.ok(pet);
  }

//  @GetMapping
//  public ResponseEntity<Page<PetSummaryResponse>> findAllPets(@PageableDefault(page = 0, size = 15) Pageable pageable) {
//    Page<PetSummaryResponse> response = petQueryService.findAllPets(pageable);
//    return ResponseEntity.ok(response);
//  }

  @GetMapping("/{petId}")
  public ResponseEntity<PetDetailResponse> findPetById(@PathVariable Long petId) {
    PetDetailResponse response = petQueryService.findPetById(petId);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<Page<PetSummaryResponse>> findAllSortedPets(
      @PageableDefault(page = 0, size = 15) Pageable pageable,
      @RequestParam("sort") String sortType) {
    Page<PetSummaryResponse> response = petQueryService.findAllPetsSortedBySortType(pageable, sortType);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{petId}")
  public ResponseEntity<Void> delete(@PathVariable Long petId) {
    petService.delete(petId);
    return ResponseEntity.noContent().build();
  }

}

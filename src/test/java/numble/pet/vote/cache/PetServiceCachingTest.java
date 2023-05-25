package numble.pet.vote.cache;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import numble.pet.vote.pet.query.application.PetDetailResponse;
import numble.pet.vote.pet.query.application.PetQueryService;
import numble.pet.vote.pet.query.application.PetSummaryResponse;
import numble.pet.vote.pet.query.domain.PetData;
import numble.pet.vote.pet.query.domain.PetQueryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class PetServiceCachingTest {

  private static final String PET_CACHE_STORE = "petCacheStore";
  private static final String PET_ALL_CACHE_STORE = "petAllCacheStore";

  @MockBean
  private PetQueryRepository petQueryRepository;

  @Autowired
  private PetQueryService petQueryService;

  @Autowired
  private CacheManager cacheManager;

  private Long id;
  private String name;
  private String species;
  private String description;
  private PetData mockPetData;

  @BeforeEach
  void setup() {
    id = 1L;
    name = "mock pet";
    species = "강아지";
    description = "강아지는 언제나 귀여워";
    mockPetData = PetData.builder()
        .id(id)
        .name(name)
        .species(species)
        .description(description)
        .build();
  }

  @AfterEach
  void tearDown() {
    Objects.requireNonNull(cacheManager.getCache(PET_CACHE_STORE)).clear(); // 테스트 종료 후 레디스 캐시를 비움
    Objects.requireNonNull(cacheManager.getCache(PET_ALL_CACHE_STORE)).clear(); // 테스트 종료 후 레디스 캐시를 비움
  }

  @Test
  @DisplayName("레디스 캐시를 이용한 id로 pet 조회 성공 테스트")
  void givenRedisCaching_whenFindPetById_thenPetReturnedFromCache_success() {

    // given

    // when
    Mockito.when(petQueryRepository.findById(id)).thenReturn(Optional.of(mockPetData));
    PetDetailResponse cacheMissResponse = petQueryService.findPetById(1L);
    PetDetailResponse cacheHitResponse = petQueryService.findPetById(1L);

    // then
    Assertions.assertThat(cacheMissResponse.getId()).isEqualTo(1L);
    Assertions.assertThat(cacheMissResponse.getName()).isEqualTo(name);
    Assertions.assertThat(cacheMissResponse.getDescription()).isEqualTo(description);
    Assertions.assertThat(cacheMissResponse.getSpecies()).isEqualTo(species);

    Assertions.assertThat(cacheHitResponse.getId()).isEqualTo(1L);
    Assertions.assertThat(cacheHitResponse.getName()).isEqualTo(name);
    Assertions.assertThat(cacheHitResponse.getDescription()).isEqualTo(description);
    Assertions.assertThat(cacheHitResponse.getSpecies()).isEqualTo(species);

    verify(petQueryRepository, times(1)).findById(id);
  }

  @Test
  @DisplayName("레디스 캐시를 이용한 전체 pet 조회 성공 테스트")
  void givenRedisCaching_whenFindAllPet_thenPetReturnedFromCache_success() {

    // given
    List<PetData> petDataList = new ArrayList<>();
    petDataList.add(mockPetData);
    Pageable pageable = PageRequest.of(0, 10);
    Page<PetData> petDataPage = new PageImpl<>(petDataList, pageable, petDataList.size());

    // when
    Mockito.when(petQueryRepository.findAll()).thenReturn(petDataList);
    Mockito.when(petQueryRepository.findAll(pageable)).thenReturn(petDataPage);
    Page<PetSummaryResponse> cacheMissResponse = petQueryService.findAllPets(pageable);
    Page<PetSummaryResponse> cacheHitResponse = petQueryService.findAllPets(pageable);


    // then
    Assertions.assertThat(cacheMissResponse.getContent().size()).isEqualTo(1);
    Assertions.assertThat(cacheMissResponse.getContent().get(0).getName()).isEqualTo(name);
    Assertions.assertThat(cacheMissResponse.getContent().get(0).getDescription()).isEqualTo(description);


    Assertions.assertThat(cacheHitResponse.getContent().size()).isEqualTo(1);
    Assertions.assertThat(cacheHitResponse.getContent().get(0).getName()).isEqualTo(name);
    Assertions.assertThat(cacheHitResponse.getContent().get(0).getDescription()).isEqualTo(description);

    verify(petQueryRepository, times(1)).findAll(pageable);
  }
}

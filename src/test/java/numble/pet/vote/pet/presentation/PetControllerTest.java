package numble.pet.vote.pet.presentation;

import static numble.pet.vote.common.controller.ApiDocumentUtils.getDocumentRequest;
import static numble.pet.vote.common.controller.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import numble.pet.vote.common.controller.BaseControllerTest;
import numble.pet.vote.pet.command.application.PetService;
import numble.pet.vote.pet.command.application.RegisterPetRequest;
import numble.pet.vote.pet.command.domain.Pet;
import numble.pet.vote.pet.command.domain.Species;
import numble.pet.vote.pet.infra.AwsS3Service;
import numble.pet.vote.pet.query.application.PetDetailResponse;
import numble.pet.vote.pet.query.application.PetQueryService;
import numble.pet.vote.pet.query.domain.PetData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("PET API TEST")
class PetControllerTest extends BaseControllerTest {

  @MockBean
  private PetService petService;

  @MockBean
  private PetQueryService petQueryService;

  @MockBean
  private AwsS3Service awsS3Service;

  private Pet mockPet;
  private String petName;
  private String species;
  private String description;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);
    petName = "TEST_PET";
    species = "강아지";
    description = "이 강아지는 엄청 귀엽습니다.";
    mockPet = Pet.builder()
        .id(1L)
        .name(petName)
        .species(Species.of(species))
        .description(description)
        .isDeleted(false)
        .build();
  }

  @Test
  @DisplayName("PET 등록 성공 테스트")
  void registerPet_success() throws Exception {

    // given
    RegisterPetRequest registerPetRequest = new RegisterPetRequest(petName, species, description);

    // when
    Mockito.when(petService.register(petName, species, description)).thenReturn(mockPet);

    ResultActions result = mockMvc.perform(post("/pets")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerPetRequest)));

    // then
    result.andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("name").value(petName))
        .andExpect(jsonPath("description").value(description))
        .andExpect(jsonPath("species").value(species))
        .andDo(document("PET - 등록 성공 API",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("species").type(JsonFieldType.STRING).description("종"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("설명")),
            responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("species").type(JsonFieldType.STRING).description("종"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                fieldWithPath("image").type(JsonFieldType.STRING).description("사진 URL"),
                fieldWithPath("voteCount").type(JsonFieldType.NUMBER).description("받은 투표 수"),
                fieldWithPath("isDeleted").type(JsonFieldType.BOOLEAN).description("삭제 여부"),
                fieldWithPath("createdAt").type(JsonFieldType.VARIES).description("생성 일자"),
                fieldWithPath("updatedAt").type(JsonFieldType.VARIES).description("수정 일자"))
            ));
  }

  @Test
  @DisplayName("PET 이미지 등록 성공 테스트")
  void registerPetImage_success() throws Exception {

    // given
    String imageUrl = "https://example.com/test.jpeg";
    MockMultipartFile file = new MockMultipartFile("image",
        "test_image.jpeg",
        MediaType.IMAGE_JPEG_VALUE,
        new FileInputStream("src/test/resources/image/test_image.jpeg"));

    // when
    Mockito.when(awsS3Service.upload(Mockito.any(MultipartFile.class))).thenReturn(imageUrl);
    Mockito.when(petService.registerImage(mockPet.getId(), imageUrl)).thenReturn(mockPet);

    // then
    mockMvc.perform((multipart(HttpMethod.POST, "/pets/image/{petId}", mockPet.getId()))
            .file(file)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("name").value(petName))
        .andExpect(jsonPath("description").value(description))
        .andExpect(jsonPath("species").value(species))
        .andDo(document("PET - 이미지 등록 성공 API",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("species").type(JsonFieldType.STRING).description("종"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                fieldWithPath("image").type(JsonFieldType.STRING).description("사진 URL"),
                fieldWithPath("voteCount").type(JsonFieldType.NUMBER).description("받은 투표 수"),
                fieldWithPath("isDeleted").type(JsonFieldType.BOOLEAN).description("삭제 여부"),
                fieldWithPath("createdAt").type(JsonFieldType.VARIES).description("생성 일자"),
                fieldWithPath("updatedAt").type(JsonFieldType.VARIES).description("수정 일자"))
        ));
  }

  @Test
  @DisplayName("모든 펫 조회 테스트 ")
  void findAllPets_success() throws Exception {

    // given
    // when
    // then
    mockMvc.perform(get("/pets"))
        .andExpect(status().isOk())
        .andDo(document("PET - 전체 pet 정보 조회 성공 API",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("content").type(JsonFieldType.ARRAY).description("검색 결과 리스트"),
                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("id").optional(),
                fieldWithPath("content.[].name").type(JsonFieldType.STRING).description("이름").optional(),
                fieldWithPath("content.[].species").type(JsonFieldType.STRING).description("종").optional(),
                fieldWithPath("content.[].description").type(JsonFieldType.STRING).description("설명").optional(),
                fieldWithPath("content.[].image").type(JsonFieldType.STRING).description("사진 URL").optional(),
                fieldWithPath("content.[].voteCount").type(JsonFieldType.NUMBER).description("받은 투표 수").optional(),
                fieldWithPath("content.[].createdAt").type(JsonFieldType.VARIES).description("생성 일자").optional(),

                fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                fieldWithPath("pageable.sort").type(JsonFieldType.OBJECT).description("페이지 정렬 정보"),
                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 empty"),
                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 X 여부"),
                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 O 여부"),

                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("한 페이지에 나오는 원소 수"),
                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description(""),

                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 검색 갯수"),
                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("size").type(JsonFieldType.NUMBER).description("한 페이지 사이즈"),
                fieldWithPath("number").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 empty"),
                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 X 여부"),
                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 O 여부"),
                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("한 페이지의 원소 수")
        )));
  }

  @Test
  @DisplayName("특정 펫 조회 테스트 ")
  void findPetById_success() throws Exception {

    // given
    PetData petdata = PetData.builder()
        .id(mockPet.getId())
        .name(mockPet.getName())
        .species(mockPet.getSpecies().name())
        .voteCount(mockPet.getVoteCount())
        .description(mockPet.getDescription())
        .image(mockPet.getImage())
        .build();

    PetDetailResponse response = PetDetailResponse.of(petdata);

    // when
    Mockito.when(petQueryService.findPetById(mockPet.getId())).thenReturn(response);

    // then
    mockMvc.perform(get("/pets/{petId}", mockPet.getId()))
        .andExpect(status().isOk())
        .andDo(document("PET - 상세 조회 성공 API",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("species").type(JsonFieldType.STRING).description("종"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
                fieldWithPath("image").type(JsonFieldType.STRING).description("사진 URL"),
                fieldWithPath("voteCount").type(JsonFieldType.NUMBER).description("받은 투표 수"),
                fieldWithPath("createdAt").type(JsonFieldType.VARIES).description("생성 일자"),
                fieldWithPath("updatedAt").type(JsonFieldType.VARIES).description("수정 일자"))
            ));
  }
}

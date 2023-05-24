package numble.pet.vote.pet.presentation;

import static numble.pet.vote.common.controller.ApiDocumentUtils.getDocumentRequest;
import static numble.pet.vote.common.controller.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
  void register_pet_success() throws Exception {

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
  void register_pet_image_success() throws Exception {

    // given
    String imageUrl = "https://example.com/test.jpeg";
    MockMultipartFile file = new MockMultipartFile("image",
        "test_image.jpeg",
        "image/jpeg",
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
}

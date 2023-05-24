package numble.pet.vote.pet.infra;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import numble.pet.vote.common.exception.BusinessException;
import numble.pet.vote.common.exception.ErrorCode;
import numble.pet.vote.config.AwsProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class AwsS3Service {

  private final AmazonS3 amazonS3Client;
  private final AwsProperties awsProperties;
  private String petImageBucket;

  public AwsS3Service(AmazonS3 amazonS3Client, AwsProperties awsProperties) {
    this.amazonS3Client = amazonS3Client;
    this.awsProperties = awsProperties;
    petImageBucket = awsProperties.getS3().getPetImageBucket();
  }

  public String upload(MultipartFile multipartFile){
    String filename = multipartFile.getOriginalFilename();

    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(multipartFile.getSize());
    objectMetadata.setContentType(multipartFile.getContentType());

    URL url = null;

    try(InputStream inputStream = multipartFile.getInputStream()) {
      amazonS3Client.putObject(new PutObjectRequest(petImageBucket, filename, inputStream, objectMetadata)
          .withCannedAcl(CannedAccessControlList.PublicRead));

      url = amazonS3Client.getUrl(petImageBucket, filename);
    } catch (IOException e) {
      log.error("파일 업로드 실패!!!! -> {}", e.getMessage());
      throw new BusinessException(ErrorCode.AWS_S3_UPLOAD_FAIL);
    }
    return String.valueOf(url);
  }
}

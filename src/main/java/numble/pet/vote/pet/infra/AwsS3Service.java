package numble.pet.vote.pet.infra;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.netty.util.internal.StringUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import numble.pet.vote.common.exception.BadRequestException;
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

    try {
      File file = convertToFile(multipartFile)
          .orElseThrow(() -> new BadRequestException(ErrorCode.FILE_CONVERT_FAIL));
      return upload(file, filename);
    } catch (IOException e) {
      log.info("file convert fail");
      return StringUtil.EMPTY_STRING;
    }
  }

  private String upload(File file, String filename) {
    String imageUrl = uploadS3(file, filename);
    deleteLocalFile(file);
    return imageUrl;
  }

  private Optional<File> convertToFile(MultipartFile file) throws IOException {
    File convertFile = new File(file.getOriginalFilename());
    if(convertFile.createNewFile()) {
      try(FileOutputStream fos = new FileOutputStream(convertFile)){
        fos.write(file.getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
      return Optional.of(convertFile);
    }
    return Optional.empty();
  }

  private String uploadS3(File file, String filename) {
    amazonS3Client.putObject(
        new PutObjectRequest(petImageBucket, filename, file));
    log.info("File upload success! => {}", filename);

    return amazonS3Client.getUrl(petImageBucket, filename).toString();
  }

  private void deleteLocalFile(File file) {
    file.delete();
  }
}

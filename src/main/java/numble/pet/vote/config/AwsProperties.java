package numble.pet.vote.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "cloud.aws")
@ConstructorBinding
@Getter
@RequiredArgsConstructor
public class AwsProperties {

  private final String region;
  private final String endPoint;
  private final Credentials credentials;
  private final S3 s3;

  @Getter
  public static class Credentials {
    private final String accessKey;
    private final String secretKey;

    public Credentials(String accessKey, String secretKey) {
      this.accessKey = accessKey;
      this.secretKey = secretKey;
    }
  }

  @Getter
  public static class S3 {
    private final String petImageBucket;
    private final String multipartMinPartSize;

    public S3(String petImageBucket, String multipartMinPartSize) {
      this.petImageBucket = petImageBucket;
      this.multipartMinPartSize = multipartMinPartSize;
    }
  }

}

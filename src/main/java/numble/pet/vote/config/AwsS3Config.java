package numble.pet.vote.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(AwsProperties.class)
public class AwsS3Config {

  private final AwsProperties awsProperties;

  public AwsS3Config(AwsProperties awsProperties) {
    this.awsProperties = awsProperties;
  }

  @Bean
  public AmazonS3 amazonS3Client() {
    AWSCredentials credentials =
        new BasicAWSCredentials(
            awsProperties.getCredentials().getAccessKey(),
            awsProperties.getCredentials().getSecretKey());

    return AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withRegion(awsProperties.getRegion())
        .build();
  }

}

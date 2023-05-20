package numble.pet.vote.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
@RequiredArgsConstructor
public class MongoConfig extends AbstractMongoClientConfiguration {

  private final MongoProperties mongoProperties;

  @Override
  public MongoClient mongoClient() {
    return MongoClients.create(mongoProperties.getUri());
  }

  @Override
  protected String getDatabaseName() {
    return mongoProperties.getDatabase();
  }
}

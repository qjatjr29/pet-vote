package numble.pet.vote.config;

import java.util.HashMap;
import java.util.Map;
import numble.pet.vote.pet.command.domain.PetUpdatedEvent;
import numble.pet.vote.vote.command.domain.VoteCanceledEvent;
import numble.pet.vote.vote.command.domain.VoteSubmittedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@EnableKafka
public class KafkaConsumerConfig {

  private final KafkaProperties kafkaProperties;

  public KafkaConsumerConfig(KafkaProperties kafkaProperties) {
    this.kafkaProperties = kafkaProperties;
  }

  @Bean Map<String, Object> consumerConfigs() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
    return configProps;
  }

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, PetUpdatedEvent> PetUpdatedEventKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, PetUpdatedEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(
        new DefaultKafkaConsumerFactory<>(consumerConfigs(),
            new StringDeserializer(),
            new JsonDeserializer<>(PetUpdatedEvent.class)));
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, VoteSubmittedEvent> VoteSubmittedEventKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, VoteSubmittedEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(
        new DefaultKafkaConsumerFactory<>(consumerConfigs(),
            new StringDeserializer(),
            new JsonDeserializer<>(VoteSubmittedEvent.class)));
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, VoteCanceledEvent> VoteCanceledEventKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, VoteCanceledEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(
        new DefaultKafkaConsumerFactory<>(consumerConfigs(),
            new StringDeserializer(),
            new JsonDeserializer<>(VoteCanceledEvent.class)));
    return factory;
  }

}

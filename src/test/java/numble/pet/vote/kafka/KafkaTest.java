package numble.pet.vote.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import numble.pet.vote.pet.command.domain.PetEventType;
import numble.pet.vote.pet.command.domain.PetUpdatedEvent;
import numble.pet.vote.vote.command.domain.VoteSubmittedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"test-topic1", "test-topic2"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class KafkaTest {

  private static final String TEST_TOPIC1 = "test-topic1";
  private static final String TEST_TOPIC2 = "test-topic2";
  private static final String TEST_GROUP = "test-group";
  private static final String BOOTSTRAP_SERVER = "localhost:9092";

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  private Map<String, Object> props;

  @BeforeEach
  void setup() {

    MockitoAnnotations.initMocks(this);

    props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, TEST_GROUP);
  }

  @Test
  @DisplayName("Embedded Kafka를 이용한 String receive 테스트")
  public void givenEmbeddedKafka_whenSendingWithString_thenMessageReceived() throws Exception {

    // given
    Consumer<String, String> consumer = new KafkaConsumer<>(props,
        new StringDeserializer(),
        new JsonDeserializer<>(String.class, false));

    String testData = "test data";

    // when
    consumer.subscribe(Collections.singleton(TEST_TOPIC1));
    kafkaTemplate.send(TEST_TOPIC1, testData);
    Thread.sleep(1000);

    // then
    ConsumerRecords<String, String> consumerRecords = KafkaTestUtils.getRecords(consumer, 10000);
    ConsumerRecord<String, String> consumerRecord = consumerRecords.iterator().next();
    String received = consumerRecord.value();

    assertThat(received).isEqualTo(testData);
  }

  @Test
  @DisplayName("Embedded Kafka를 이용한 Event receive 테스트")
  public void givenEmbeddedKafka_whenSendingWithEvent_thenEventReceived() throws Exception {

    // given
    PetUpdatedEvent petUpdatedEvent = new PetUpdatedEvent(1L, PetEventType.CREATE);
    VoteSubmittedEvent voteSubmittedEvent = new VoteSubmittedEvent(1L);

    // when
    when(kafkaTemplate.send(eq(TEST_TOPIC1), any(PetUpdatedEvent.class))).thenReturn(null);
    when(kafkaTemplate.send(eq(TEST_TOPIC2), any(VoteSubmittedEvent.class))).thenReturn(null);

    kafkaTemplate.send(TEST_TOPIC1, petUpdatedEvent);
    kafkaTemplate.send(TEST_TOPIC2, voteSubmittedEvent);

    Thread.sleep(1000);

    // then
    verify(kafkaTemplate).send(eq(TEST_TOPIC1), eq(petUpdatedEvent));
    verify(kafkaTemplate).send(eq(TEST_TOPIC2), eq(voteSubmittedEvent));

  }
}

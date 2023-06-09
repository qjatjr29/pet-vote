package numble.pet.vote.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

  private final RedisProperties redisProperties;

  public RedisConfig(RedisProperties redisProperties) {
    this.redisProperties = redisProperties;
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
  }

  @Bean
  public CacheManager redisCacheManager() {
    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())))
        .entryTtl(Duration.ofMinutes(3L));

    RedisCacheManager redisCacheManager = RedisCacheManager
        .RedisCacheManagerBuilder
        .fromConnectionFactory(redisConnectionFactory())
        .cacheDefaults(redisCacheConfiguration)
        .build();
    return redisCacheManager;
  }

  private ObjectMapper objectMapper() {
    PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
        .allowIfSubType(Object.class)
        .build();

    JavaTimeModule javaTimeModule = new JavaTimeModule();
    // 자바 8 시간 관련 직렬화를 위한 모듈 등록
    javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    return new ObjectMapper()
        .findAndRegisterModules()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .activateDefaultTyping(typeValidator, DefaultTyping.NON_FINAL)
        .registerModule(javaTimeModule);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

    redisTemplate.setConnectionFactory(redisConnectionFactory());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
    redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
//    redisTemplate.setKeySerializer(new StringRedisSerializer());
//    redisTemplate.setValueSerializer(new StringRedisSerializer());

    return redisTemplate;
  }

}

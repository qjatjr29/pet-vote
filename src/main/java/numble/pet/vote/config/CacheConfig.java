package numble.pet.vote.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {

  @Autowired
  RedisConnectionFactory redisConnectionFactory;

  @Autowired
  RedisConnectionFactory connectionFactory;


  @Bean
  public CacheManager redisCacheManager() {
    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())))
        .entryTtl(Duration.ofMinutes(3L));

    RedisCacheManager redisCacheManager = RedisCacheManager
        .RedisCacheManagerBuilder
        .fromConnectionFactory(connectionFactory)
        .cacheDefaults(redisCacheConfiguration)
        .build();
    return redisCacheManager;
  }

  private ObjectMapper objectMapper() {

    PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
        .build();

    return new ObjectMapper()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .registerModule(new JavaTimeModule())
        .activateDefaultTyping(typeValidator, DefaultTyping.NON_FINAL);
  }

}

package vn.vnpay.commoninterface.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import io.lettuce.core.ReadFrom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.*;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.service.RedisMessageSubscriber;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Redis Cache config
 */
@Configuration
public class RedisCacheConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.master}")
    private String redisMaster;

    @Value("${redis.password}")
    private String redisPassword;

    @Value("${redis.db}")
    private int redisDb;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        String[] redisHostArr = redisHost.split(",");

        if (redisHostArr.length > 1) {
            RedisSentinelConfiguration serverConfig = new RedisSentinelConfiguration(redisMaster, new HashSet<String>(Arrays.asList(redisHostArr)));
            serverConfig.setPassword(redisPassword);
            serverConfig.setDatabase(redisDb);

            // Write to master & Read from replica
            LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().readFrom(ReadFrom.REPLICA_PREFERRED).build();
            return new LettuceConnectionFactory(serverConfig, clientConfig);
        } else {
            String hostName = redisHost.split(":")[0];
            int port = Integer.parseInt(redisHost.split(":")[1]);

            RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(hostName, port);
            serverConfig.setPassword(redisPassword);
            serverConfig.setDatabase(redisDb);
            return new LettuceConnectionFactory(serverConfig);
        }
    }

    /**
     * Tạo Bean RedisTemplate nhận key Object và value Object
     *
     * @return redisTemplate
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(), ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2))
                .disableCachingNullValues()
                .prefixCacheNameWith(Constants.CACHE_PREFIX)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
        return cacheConfig;
    }

    @Primary
    @Bean(name = "redisCacheManager")
    public CacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(cacheConfiguration())
                .transactionAware()
                .build();
        return redisCacheManager;
    }

    /* -------------------- Redis Pub/Sub configuration -------------------- */

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisMessageSubscriber());
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(messageListener(), topic());
        return container;
    }

    @Bean
    MessagePublisher redisPublisher() {
        return new RedisMessagePublisher(redisTemplate(), topic());
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("messageQueue");
    }
}

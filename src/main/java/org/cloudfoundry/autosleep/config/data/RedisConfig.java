package org.cloudfoundry.autosleep.config.data;

import org.cloudfoundry.autosleep.servicebroker.model.AutoSleepServiceInstance;
import org.cloudfoundry.autosleep.repositories.ServiceRepository;
import org.cloudfoundry.autosleep.repositories.redis.RedisServiceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile("redis")
public class RedisConfig {

    @Bean
    public ServiceRepository redisRepository(RedisTemplate<String, AutoSleepServiceInstance> redisTemplate) {
        return new RedisServiceRepository(redisTemplate);
    }

    @Bean
    public RedisTemplate<String, AutoSleepServiceInstance> redisTemplate(RedisConnectionFactory
                                                                                     redisConnectionFactory) {
        RedisTemplate<String, AutoSleepServiceInstance> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<AutoSleepServiceInstance> serviceSerializer = new JacksonJsonRedisSerializer<>
                (AutoSleepServiceInstance.class);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(serviceSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(serviceSerializer);

        return template;
    }

}

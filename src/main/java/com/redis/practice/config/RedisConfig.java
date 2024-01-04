package com.redis.practice.config;

import com.redis.practice.property.RedisProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis 연결을 설정하는 설정 클래스.
 * RedisProperty 클래스에서 Redis 속성(host, port)를 읽어 온다.
 * Lettuce를 사용하여 RedisConnectionFactory 빈을 정의한다.
 * RedisTemplate 빈을 정의하는데 이는 Redis와 상호 작용하기 위한 고수준 추상화이다.
 */
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperty redisProperty;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperty.getHost(), redisProperty.getPort());
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
    /**
     * Spring Data Redis는 Redis에 두가지 접근 방법을 제공하는데
     * 하나는 RedisTemplate을 이용한 방식, 다른 하나는 RedisRepository를 이용한 방식.
     * 이번 Practice Project에서는 전자를 채택.
     */
}

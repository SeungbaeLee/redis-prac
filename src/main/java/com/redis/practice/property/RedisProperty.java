package com.redis.practice.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Redis 구성 속성(host, port)를 보유하고 있는 클래스.
 */
@Getter
@Setter
@Component
@ConfigurationProperties("spring.redis")
public class RedisProperty {
    private String host;
    private int port;
}

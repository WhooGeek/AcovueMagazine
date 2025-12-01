package com.AcovueMagazine.Member.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    //application.yml에서 host, port 값을 주입
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;


    //Redis 연결 팩토리 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis 설정 - host와 port가 필요함
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);

        //Lettuce VS Jedis -> Lettusce를 선택 (Jedis보다 성능 좋고 비동기 처리 가능)
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    // RedisTemplate 설정
    // RedisTemplate은 DB 서버에 Set, Get, Delete 등을 사용할 수 있음
    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        // RedisTemplate은 트랙젝션 지원
        // 트랜젝션 안에서 오류가 발생하면 그 작업 모두 취소

        // Redis와 통신할 때 사용할 탬플릿 설정
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        //Key, value 직렬화 방법 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}

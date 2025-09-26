package com.AcovueMagazine.Member.Dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.Duration;

// 레디스 접근을 위한 클래스
@Component
@RequiredArgsConstructor
public class RedisDao {
    private final RedisTemplate<String, Object> redisTemplate;

    // 저장
    public void setValues(String key, String data){
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    //만료 제한 있는 데이터 저장
    //RefreshToken 용
    public void setValues(String key, String data, Duration duration){
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    //데이터 조회
    //RefreshToken 검증 할때 이용
    public Object getValues(String key){
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    //데이터 삭제
    //로그아웃 할때 RefreshTOken 삭제
    public void deleteValues(String key){
        redisTemplate.delete(key);
    }

}

package com.example.oops.api.email;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    public void setData(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value,duration);
    }

    public String getData(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? "" : value.toString();
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    public boolean existData(String key) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().get(key));
    }
}

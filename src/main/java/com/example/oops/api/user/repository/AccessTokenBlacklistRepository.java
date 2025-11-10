package com.example.oops.api.user.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class AccessTokenBlacklistRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public AccessTokenBlacklistRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistAccessToken(String accessToken, long expirationMillis) {
        if (expirationMillis > 0) {
            redisTemplate.opsForValue().set(accessToken, "logout", expirationMillis, TimeUnit.MILLISECONDS);
        }
    }
    public boolean isBlacklisted(String accessToken) {
        // Redis에서 해당 Access Token을 키로 갖는 데이터가 존재하는지 확인
        // redisTemplate.hasKey()는 키가 존재하는지 확인하는 가장 효율적인 방법입니다.
        Boolean hasKey = redisTemplate.hasKey(accessToken);

        // hasKey가 null일 경우 (연결 문제 등)를 대비하여 안전하게 처리합니다.
        return hasKey != null && hasKey;
    }
}
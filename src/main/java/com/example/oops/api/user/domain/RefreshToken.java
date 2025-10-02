package com.example.oops.api.user.domain;



import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;


@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@RedisHash(value = "refreshToken")
public class RefreshToken {

    @Id
    private String userName;

    // Redis에서 token 값으로 조회 가능
    @Indexed
    private String token;

    // 만료시간 지나면 자동 삭제
    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long expiryTime;



}

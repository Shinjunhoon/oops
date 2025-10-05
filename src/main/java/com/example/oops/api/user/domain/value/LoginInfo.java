package com.example.oops.api.user.domain.value;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.*;
import org.hibernate.sql.exec.spi.StandardEntityInstanceResolver;

import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 👈 JPA 사용을 위해 protected 기본 생성자 추가
@AllArgsConstructor
public class LoginInfo {
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // 별도 테이블 없이 DB에 저장 후 User 조회 할때 roles도 가져옴
    // 나중에 role을 가져올때 null을 방지하기 위해 기본값을 @Builder.Default 어노테이션을 사용하여 방지해준다.
    @ElementCollection(fetch= FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

}

package com.example.oops.api.user.domain.value;

import com.example.oops.api.user.domain.enums.Line;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jdk.jfr.Enabled;
import lombok.*;

@Getter
@Embeddable
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 👈 JPA 사용을 위해 protected 기본 생성자 추가
@AllArgsConstructor
public class UserInfo {
    @Enumerated(EnumType.STRING)
    private Line line;

}

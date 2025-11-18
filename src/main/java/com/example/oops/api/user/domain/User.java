package com.example.oops.api.user.domain;


import com.example.oops.api.user.domain.enums.AuthType;
import com.example.oops.api.user.domain.enums.Line;
import com.example.oops.api.user.domain.value.LoginInfo;
import com.example.oops.api.user.domain.value.UserInfo;
import com.example.oops.api.user.dto.OAuthAttributes;
import com.example.oops.api.user.dto.SignRequestDto;
import com.example.oops.api.vote.domain.Vote;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SQLDelete(sql = "UPDATE user SET del_yn = true WHERE id = ?")
@SQLRestriction("del_yn = false")
@Entity
@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private UserInfo userInfo;

    @Embedded
    private LoginInfo loginInfo;

    @Column(name = "del_yn")
    private boolean delYn = false;

    @Column(name = "social_id")
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false)
    private AuthType authType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<com.example.oops.api.post.domain.Post> posts = new ArrayList<>();


    public static User of(String encodedPassword,SignRequestDto signRequestDto) {

        UserInfo userInfo1 = UserInfo.builder()
                .nickname(signRequestDto.getNickname())
                .line(signRequestDto.getLine())
                .build();

        LoginInfo loginInfo1 = LoginInfo.builder()
                .username(signRequestDto.getUsername())
                .password(encodedPassword) // ✨ 해싱된 비밀번호 사용
                .roles(List.of("ROLE_USER")) // ✨ 기본 역할 부여
                .build();

        return User.builder()
                .userInfo(userInfo1)
                .loginInfo(loginInfo1)
                .authType(AuthType.LOCAL)
                .build();
    }

    public static User ofOAuth(OAuthAttributes attributes) {

        UserInfo userInfo1 = UserInfo.builder()
                .nickname(attributes.getName())
                // ⭐️ 수정: line 대신 lolPosition 필드 사용 및 초기값 설정
                .line(Line.NONE)
                .build();

        LoginInfo loginInfo1 = LoginInfo.builder()
                .username(attributes.getEmail())
                .password("oauth_user_not_applicable")
                .roles(List.of("ROLE_USER"))
                .build();

        return User.builder()
                // ⭐️ 올바른 로직: 소셜 타입은 AuthType에 저장
                .authType(AuthType.valueOf(attributes.getRegistrationId().toUpperCase()))
                .userInfo(userInfo1)
                .loginInfo(loginInfo1)
                .socialId(attributes.getSocialId())
                .build();
    }

    // 3. ✨ 소셜 로그인 시 정보 업데이트 메서드 (닉네임/프로필 사진)
    public User update(String nickname) {// UserInfo는 불변 객체라고 가정하고 새로운 객체를 생성
        this.userInfo = this.userInfo.updateNickname(nickname);
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.loginInfo.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.loginInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return this.loginInfo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() {  return !this.isDelYn();  }
}

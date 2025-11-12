package com.example.oops.api.user.domain;


import com.example.oops.api.user.domain.enums.Line;
import com.example.oops.api.user.domain.value.LoginInfo;
import com.example.oops.api.user.domain.value.UserInfo;
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
                .build();
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

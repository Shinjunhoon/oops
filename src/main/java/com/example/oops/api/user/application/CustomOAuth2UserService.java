package com.example.oops.api.user.application;

import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.domain.enums.AuthType;
import com.example.oops.api.user.domain.enums.Line;
import com.example.oops.api.user.dto.OAuthAttributes;
import com.example.oops.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 1. 소셜 서비스 정보 추출 (기존 로직 유지)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 2. OAuthAttributes로 정보 파싱 및 통일 (기존 로직 유지)
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 3. DB 저장 또는 업데이트 (핵심 로직)
        User user = saveOrUpdate(attributes);

        // 4. Spring Security에 전달할 인증 객체 생성 (기존 로직 유지)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getLoginInfo().getRoles().get(0))),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        // ⭐️ AuthType으로 변경 및 변환
        AuthType authType = AuthType.valueOf(attributes.getRegistrationId().toUpperCase());

        // ⚠️ 주의: Repository 메서드 이름이 변경되어야 합니다.
        // findBySocialIdAndUserInfo_Line -> findBySocialIdAndAuthType
        Optional<User> existingUser = userRepository.findBySocialIdAndAuthType(attributes.getSocialId(), authType);

        if (existingUser.isPresent()) {
            // 정보 업데이트 (Dirty Checking)
            User user = existingUser.get();
            // ⭐️ picture 인자 제거
            return user.update(attributes.getName());
        } else {
            // 신규 회원가입
            User newUser = User.ofOAuth(attributes);
            return userRepository.save(newUser);
        }
    }
}
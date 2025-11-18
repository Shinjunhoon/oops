package com.example.oops.api.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private final String registrationId;
    private final String socialId;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    // ⚠️ picture 필드는 이미 제거되었습니다.

    @Builder
    // ⭐️ 수정: picture 인자를 제거했습니다.
    public OAuthAttributes(String registrationId, String socialId, Map<String, Object> attributes,
                           String nameAttributeKey, String name, String email) { // 👈 String picture 제거
        this.registrationId = registrationId;
        this.socialId = socialId;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return ofGoogle(registrationId, userNameAttributeName, attributes);
        }
        return null;
    }

    private static OAuthAttributes ofGoogle(String registrationId, String userNameAttributeName,
                                            Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .registrationId(registrationId)
                .socialId((String) attributes.get(userNameAttributeName))
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                // picture가 제거되었으므로, 여기에 .picture(...) 호출도 없습니다. (정상)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}
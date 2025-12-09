package com.example.oops.api.RiotApiService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerkStyleSelectionDto {
    private int perk; // 🎯 룬의 ID (핵심 룬 ID가 여기에 담깁니다)
    // 나머지 필드 (var1, var2, var3)는 필요 없으면 생략 가능
}

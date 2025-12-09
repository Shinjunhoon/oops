package com.example.oops.api.RiotApiService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerksDto {

    // 주 룬 트리와 보조 룬 트리를 포함하는 리스트
    // styles 리스트의 첫 번째 항목이 주 룬, 두 번째 항목이 보조 룬입니다.
    private List<PerkStyleDto> styles;

    // 능력치 파편 정보는 필요 없으시다면 포함하지 않아도 됩니다.
    // private PerkStatsDto statPerks;
}
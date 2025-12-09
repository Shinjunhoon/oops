package com.example.oops.api.RiotApiService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerkStyleDto {

    private String description; // 룬 트리의 이름 (예: "Precision", "Domination")
    private int style;         // 룬 트리의 고유 ID (예: 8000)

    private List<PerkStyleSelectionDto> selections;
}
package com.example.oops.api.RiotApiService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDTO {
    private String puuid;
    private String gameName;
    private String tagLine;
}
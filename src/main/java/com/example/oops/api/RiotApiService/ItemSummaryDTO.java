package com.example.oops.api.RiotApiService;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public  class ItemSummaryDTO {
    private int itemId;
    private String itemName;             // DDragon 적용 (한글)
    private String itemImageUrl;         // DDragon 적용
}
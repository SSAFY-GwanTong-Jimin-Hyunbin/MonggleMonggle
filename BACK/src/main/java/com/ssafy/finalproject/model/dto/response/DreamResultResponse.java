package com.ssafy.finalproject.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DreamResultResponse {
    private Long id;
    private Long dreamId;
    private String dreamInterpretation;
    private String todayFortuneSummary;
    private LuckyColorDto luckyColor;
    private LuckyItemDto luckyItem;
    private String imageUrl;
    private LocalDateTime createdDate;
    private LocalDateTime deletedDate;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LuckyColorDto {
        private String name;
        private Integer number;
        private String reason;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LuckyItemDto {
        private String name;
        private String reason;
    }
}


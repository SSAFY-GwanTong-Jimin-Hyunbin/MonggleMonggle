package com.ssafy.finalproject.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveDreamResultRequest {
    
    @NotBlank(message = "꿈 해몽 결과는 필수입니다.")
    private String dreamInterpretation;
    
    @NotBlank(message = "오늘의 운세 요약은 필수입니다.")
    private String todayFortuneSummary;
    
    @NotNull(message = "행운의 색 정보는 필수입니다.")
    private LuckyColorDto luckyColor;
    
    @NotNull(message = "행운의 아이템 정보는 필수입니다.")
    private LuckyItemDto luckyItem;
    
    private String imageUrl;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LuckyColorDto {
        @NotBlank(message = "색상 이름은 필수입니다.")
        private String name;
        
        @NotNull(message = "색상 번호는 필수입니다.")
        private Integer number;
        
        @NotBlank(message = "색상 추천 이유는 필수입니다.")
        private String reason;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LuckyItemDto {
        @NotBlank(message = "아이템 이름은 필수입니다.")
        private String name;
        
        @NotBlank(message = "아이템 추천 이유는 필수입니다.")
        private String reason;
    }
}


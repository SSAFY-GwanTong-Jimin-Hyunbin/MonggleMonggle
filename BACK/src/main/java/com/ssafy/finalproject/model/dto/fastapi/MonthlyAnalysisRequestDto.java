package com.ssafy.finalproject.model.dto.fastapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAnalysisRequestDto {
    
    @JsonProperty("user_name")
    private String userName;
    
    @JsonProperty("birth_date")
    private String birthDate;
    
    @JsonProperty("daily_data")
    private List<DailyDataDto> dailyData;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyDataDto {
        @JsonProperty("date")
        private String date;
        
        @JsonProperty("dream_content")
        private String dreamContent;
        
        @JsonProperty("today_fortune_summary")
        private String todayFortuneSummary;
        
        @JsonProperty("emotion_score")
        private Integer emotionScore;
    }
}


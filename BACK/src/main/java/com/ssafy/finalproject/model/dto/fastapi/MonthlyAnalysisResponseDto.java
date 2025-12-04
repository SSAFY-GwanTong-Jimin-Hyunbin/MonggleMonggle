package com.ssafy.finalproject.model.dto.fastapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAnalysisResponseDto {
    
    @JsonProperty("period")
    private String period;
    
    @JsonProperty("user_name")
    private String userName;
    
    @JsonProperty("report")
    private String report;
    
    @JsonProperty("statistics")
    private StatisticsDto statistics;
    
    @JsonProperty("metadata")
    private MetadataDto metadata;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticsDto {
        @JsonProperty("total_dreams")
        private Integer totalDreams;
        
        @JsonProperty("avg_emotion_score")
        private Double avgEmotionScore;
        
        @JsonProperty("emotion_distribution")
        private Map<String, Integer> emotionDistribution;
        
        @JsonProperty("most_common_themes")
        private List<String> mostCommonThemes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetadataDto {
        @JsonProperty("generated_at")
        private String generatedAt;
        
        @JsonProperty("model")
        private String model;
        
        @JsonProperty("version")
        private String version;
    }
}


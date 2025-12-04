package com.ssafy.finalproject.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAnalysis {
    private Long analysisId;
    private Long userId;
    private Integer year;
    private Integer month;
    private Integer dreamCount;
    private BigDecimal avgEmotionScore;
    private String monthlyReport;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}


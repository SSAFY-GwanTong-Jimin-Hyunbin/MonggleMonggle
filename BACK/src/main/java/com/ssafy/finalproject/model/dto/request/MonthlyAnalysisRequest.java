package com.ssafy.finalproject.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAnalysisRequest {
    
    @NotNull(message = "연도는 필수입니다.")
    @Min(value = 2000, message = "연도는 2000년 이상이어야 합니다.")
    @Max(value = 2100, message = "연도는 2100년 이하여야 합니다.")
    private Integer year;
    
    @NotNull(message = "월은 필수입니다.")
    @Min(value = 1, message = "월은 1 이상이어야 합니다.")
    @Max(value = 12, message = "월은 12 이하여야 합니다.")
    private Integer month;
}


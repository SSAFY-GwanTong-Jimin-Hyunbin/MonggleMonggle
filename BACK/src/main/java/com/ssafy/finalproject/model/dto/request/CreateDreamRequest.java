package com.ssafy.finalproject.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDreamRequest {
    
    @NotNull(message = "감정 ID는 필수입니다.")
    private Integer emotionId;
    
    @NotNull(message = "꿈 날짜는 필수입니다.")
    private LocalDate dreamDate;
    
    @NotBlank(message = "제목은 필수입니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}


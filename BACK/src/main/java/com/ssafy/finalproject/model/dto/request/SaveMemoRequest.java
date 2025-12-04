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
public class SaveMemoRequest {
    
    @NotNull(message = "분석 ID는 필수입니다.")
    private Long analysisId;
    
    @NotBlank(message = "메모 내용은 필수입니다.")
    private String memoContent;
}


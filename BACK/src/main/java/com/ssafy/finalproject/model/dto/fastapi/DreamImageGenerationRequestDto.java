package com.ssafy.finalproject.model.dto.fastapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FastAPI 이미지 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DreamImageGenerationRequestDto {
    
    @JsonProperty("dream_prompt")
    private String dreamPrompt;
    
    @JsonProperty("style")
    private String style;
}

package com.ssafy.finalproject.model.dto.fastapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FastAPI 이미지 생성 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DreamImageGenerationResponseDto {
    
    @JsonProperty("success")
    private Boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("images")
    private List<GeneratedImage> images;
    
    @JsonProperty("model_text")
    private String modelText;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneratedImage {
        
        @JsonProperty("image_data")
        private String imageData;
        
        @JsonProperty("mime_type")
        private String mimeType;
    }
}

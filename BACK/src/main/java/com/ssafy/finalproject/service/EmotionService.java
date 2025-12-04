package com.ssafy.finalproject.service;

import com.ssafy.finalproject.model.dao.EmotionDao;
import com.ssafy.finalproject.model.dto.response.EmotionListResponse;
import com.ssafy.finalproject.model.entity.EmotionScore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmotionService {
    
    private final EmotionDao emotionDao;
    
    // 감정 점수 목록 조회
    public EmotionListResponse getAllEmotions() {
        List<EmotionScore> emotions = emotionDao.findAll();
        
        List<EmotionListResponse.EmotionDto> emotionDtos = emotions.stream()
                .map(emotion -> EmotionListResponse.EmotionDto.builder()
                        .emotionId(emotion.getEmotionId())
                        .emotionName(emotion.getEmotionName())
                        .score(emotion.getScore())
                        .build())
                .collect(Collectors.toList());
        
        return EmotionListResponse.builder()
                .emotions(emotionDtos)
                .build();
    }
}


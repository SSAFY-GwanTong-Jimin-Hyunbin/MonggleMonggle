package com.ssafy.finalproject.model.dao;

import com.ssafy.finalproject.model.entity.EmotionScore;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmotionDao {
    
    // 모든 감정 점수 목록 조회
    List<EmotionScore> findAll();
}

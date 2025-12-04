package com.ssafy.finalproject.model.dao;

import com.ssafy.finalproject.model.entity.MonthlyMemo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MonthlyMemoDao {
    
    // 월별 메모 저장
    void insertMemo(MonthlyMemo memo);
    
    // 분석 ID로 메모 조회
    Optional<MonthlyMemo> findByAnalysisId(@Param("analysisId") Long analysisId);
    
    // 월별 메모 수정
    void updateMemo(MonthlyMemo memo);
    
    // 월별 메모 삭제
    void deleteMemo(@Param("memoId") Long memoId);
}

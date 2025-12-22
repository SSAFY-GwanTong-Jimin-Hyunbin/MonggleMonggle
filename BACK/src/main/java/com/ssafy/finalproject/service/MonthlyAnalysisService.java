package com.ssafy.finalproject.service;

import com.ssafy.finalproject.exception.ResourceNotFoundException;
import com.ssafy.finalproject.exception.ServiceUnavailableException;
import com.ssafy.finalproject.model.dao.DreamsDao;
import com.ssafy.finalproject.model.dao.DreamsResultsDao;
import com.ssafy.finalproject.model.dao.MonthlyAnalysisDao;
import com.ssafy.finalproject.model.dao.UserDao;
import com.ssafy.finalproject.model.dto.fastapi.MonthlyAnalysisRequestDto;
import com.ssafy.finalproject.model.dto.fastapi.MonthlyAnalysisResponseDto;
import com.ssafy.finalproject.model.dto.response.MonthlyAnalysisResponse;
import com.ssafy.finalproject.model.entity.Dream;
import com.ssafy.finalproject.model.entity.DreamResult;
import com.ssafy.finalproject.model.entity.MonthlyAnalysis;
import com.ssafy.finalproject.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MonthlyAnalysisService {
    
    private final MonthlyAnalysisDao monthlyAnalysisDao;
    private final DreamsDao dreamsDao;
    private final DreamsResultsDao dreamsResultsDao;
    private final UserDao userDao;
    private final WebClient webClient;
    
    // 월별 꿈 통계 조회
    @Transactional(readOnly = true)
    public MonthlyAnalysisResponse getMonthlyAnalysis(Long userId, Integer year, Integer month) {
        return monthlyAnalysisDao.findByUserIdAndYearMonth(userId, year, month)
                .map(this::toResponse)
                .orElseGet(() -> MonthlyAnalysisResponse.builder()
                        .analysisId(null)
                        .year(year)
                        .month(month)
                        .dreamCount(0)
                        .avgEmotionScore(BigDecimal.ZERO)
                        .monthlyReport(null)
                        .createdDate(null)
                        .updatedDate(null)
                        .build());
    }
    
    /**
     * 월별 통계/리포트를 조회하고, 없으면 생성 후 반환.
     */
    public MonthlyAnalysisResponse getOrGenerateMonthlyAnalysis(Long userId, Integer year, Integer month) {
        return monthlyAnalysisDao.findByUserIdAndYearMonth(userId, year, month)
                .map(this::toResponse)
                .orElseGet(() -> generateMonthlyReport(userId, year, month));
    }
    
    /**
     * 오늘 기준 지난달 통계/리포트를 조회하고 없으면 생성 후 반환.
     */
    public MonthlyAnalysisResponse getOrGeneratePreviousMonthAnalysis(Long userId) {
        LocalDate targetDate = LocalDate.now().minusMonths(1);
        return getOrGenerateMonthlyAnalysis(userId, targetDate.getYear(), targetDate.getMonthValue());
    }
    
    // 월별 AI 리포트 생성 요청
    public MonthlyAnalysisResponse generateMonthlyReport(Long userId, Integer year, Integer month) {
        // 사용자 정보 조회
        User user = userDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
        
        // 해당 월의 꿈 일기 조회
        List<Dream> dreams = dreamsDao.findByUserIdAndYearMonth(userId, year, month);
        
        // 해당 월에 꿈 일기가 없으면 빈 응답 반환
        if (dreams.isEmpty()) {
            return MonthlyAnalysisResponse.builder()
                    .analysisId(null)
                    .year(year)
                    .month(month)
                    .dreamCount(0)
                    .avgEmotionScore(BigDecimal.ZERO)
                    .monthlyReport(null)
                    .createdDate(null)
                    .updatedDate(null)
                    .build();
        }
        
        // FastAPI 요청 데이터 생성
        List<MonthlyAnalysisRequestDto.DailyDataDto> dailyData = dreams.stream()
                .map(dream -> {
                    DreamResult result = dreamsResultsDao.findByDreamId(dream.getDreamId()).orElse(null);
                    
                    return MonthlyAnalysisRequestDto.DailyDataDto.builder()
                            .date(dream.getDreamDate().toString())
                            .dreamContent(dream.getContent())
                            .todayFortuneSummary(result != null ? result.getTodayFortuneSummary() : "")
                            .emotionScore(dream.getEmotionId())
                            .build();
                })
                .collect(Collectors.toList());
        
        MonthlyAnalysisRequestDto fastapiRequest = MonthlyAnalysisRequestDto.builder()
                .userName(user.getName())
                .birthDate(user.getBirthDate().toString())
                .dailyData(dailyData)
                .build();
        
        // FastAPI 호출
        MonthlyAnalysisResponseDto fastapiResponse;
        try {
            fastapiResponse = webClient.post()
                    .uri("/api/v1/fortune/monthly-analysis")
                    .bodyValue(fastapiRequest)
                    .retrieve()
                    .bodyToMono(MonthlyAnalysisResponseDto.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
        } catch (Exception e) {
            log.error("FastAPI 호출 실패", e);
            throw new ServiceUnavailableException("AI 서비스 오류가 발생했습니다.");
        }
        
        if (fastapiResponse == null) {
            throw new ServiceUnavailableException("AI 서비스로부터 응답을 받지 못했습니다.");
        }
        
        log.info("[월간분석] FastAPI 응답 수신 - report 길이: {}", 
            fastapiResponse.getReport() != null ? fastapiResponse.getReport().length() : "null");
        
        // 통계 계산
        int dreamCount = dreams.size();
        double avgScore = dreams.stream()
                .mapToInt(Dream::getEmotionId)
                .average()
                .orElse(0.0);
        
        // 분석 데이터 생성 (ON DUPLICATE KEY UPDATE로 중복 방지)
        MonthlyAnalysis analysis = MonthlyAnalysis.builder()
                .userId(userId)
                .year(year)
                .month(month)
                .dreamCount(dreamCount)
                .avgEmotionScore(BigDecimal.valueOf(avgScore))
                .monthlyReport(fastapiResponse.getReport())
                .build();
        
        log.info("[월간분석] DB 저장 시도 - userId: {}, year: {}, month: {}", userId, year, month);
        
        // INSERT ... ON DUPLICATE KEY UPDATE 사용으로 race condition 방지
        monthlyAnalysisDao.insertMonthlyAnalysis(analysis);
        
        log.info("[월간분석] DB 저장 완료, 조회 시작");
        
        // 저장 후 최신 데이터 조회하여 반환 (analysisId 등 DB에서 생성된 값 포함)
        MonthlyAnalysis savedAnalysis = monthlyAnalysisDao.findByUserIdAndYearMonth(userId, year, month)
                .orElse(null);
        
        if (savedAnalysis != null) {
            log.info("[월간분석] DB 조회 성공 - analysisId: {}, report 길이: {}", 
                savedAnalysis.getAnalysisId(),
                savedAnalysis.getMonthlyReport() != null ? savedAnalysis.getMonthlyReport().length() : "null");
            return toResponse(savedAnalysis);
        } else {
            log.warn("[월간분석] DB 조회 실패 - 저장된 데이터 없음");
            return toResponse(analysis);
        }
    }
    
    private MonthlyAnalysisResponse toResponse(MonthlyAnalysis analysis) {
        return MonthlyAnalysisResponse.builder()
                .analysisId(analysis.getAnalysisId())
                .year(analysis.getYear())
                .month(analysis.getMonth())
                .dreamCount(analysis.getDreamCount())
                .avgEmotionScore(analysis.getAvgEmotionScore())
                .monthlyReport(analysis.getMonthlyReport())
                .createdDate(analysis.getCreatedDate())
                .updatedDate(analysis.getUpdatedDate())
                .build();
    }
}


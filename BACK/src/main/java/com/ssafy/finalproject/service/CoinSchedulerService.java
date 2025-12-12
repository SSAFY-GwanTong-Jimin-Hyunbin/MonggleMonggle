package com.ssafy.finalproject.service;

import com.ssafy.finalproject.model.dao.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 코인 스케줄러 서비스
 * - 매일 자정(KST)에 모든 사용자의 코인을 자동으로 리셋
 * - 서버 재시작 시 누락된 날짜의 코인 리셋 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CoinSchedulerService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final UserDao userDao;

    /**
     * 매일 자정 0시 0분 0초(KST)에 모든 사용자 코인 리셋
     * cron: "초 분 시 일 월 요일"
     * zone: Asia/Seoul로 KST 기준 실행
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void resetAllUserCoinsDaily() {
        LocalDate today = LocalDate.now(KST);
        log.info("=== 일일 코인 리셋 시작 (날짜: {}) ===", today);
        
        try {
            // 모든 사용자의 코인 리셋 (last_coin_reset_at이 오늘보다 이전인 경우만)
            int updatedCount = userDao.resetAllUserCoins(today);
            log.info("=== 일일 코인 리셋 완료: {} 명의 사용자 코인 갱신 ===", updatedCount);
        } catch (Exception e) {
            log.error("일일 코인 리셋 중 오류 발생", e);
        }
    }
}

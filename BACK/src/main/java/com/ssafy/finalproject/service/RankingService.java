package com.ssafy.finalproject.service;

import com.ssafy.finalproject.model.dao.DreamsDao;
import com.ssafy.finalproject.model.dto.response.RankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {
    
    private final DreamsDao dreamsDao;
    
    /**
     * 사용자별 꿈 일기 개수 랭킹 조회
     * 이름의 마지막 글자를 *로 마스킹하여 반환
     */
    public RankingResponse getDreamCountRanking() {
        List<Map<String, Object>> rawRankings = dreamsDao.findDreamCountRanking();
        
        List<RankingResponse.RankingItem> rankings = new ArrayList<>();
        int rank = 1;
        
        for (Map<String, Object> row : rawRankings) {
            String name = (String) row.get("name");
            Number dreamCountNumber = (Number) row.get("dream_count");
            int dreamCount = dreamCountNumber != null ? dreamCountNumber.intValue() : 0;
            
            String maskedName = maskName(name);
            
            rankings.add(RankingResponse.RankingItem.builder()
                    .rank(rank++)
                    .maskedName(maskedName)
                    .dreamCount(dreamCount)
                    .build());
        }
        
        return RankingResponse.builder()
                .rankings(rankings)
                .totalUsers(rankings.size())
                .build();
    }
    
    /**
     * 이름 마스킹 처리
     * 예: "김현빈" -> "김현*"
     *     "이름" -> "이*"
     *     "김" -> "*"
     */
    private String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return "*";
        }
        
        if (name.length() == 1) {
            return "*";
        }
        
        // 마지막 글자를 *로 대체
        return name.substring(0, name.length() - 1) + "*";
    }
}


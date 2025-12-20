<script setup>
import { ref, onMounted, computed } from "vue";
import { getDreamCountRanking } from "../services/rankingService";

const rankings = ref([]);
const totalUsers = ref(0);
const isLoading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    isLoading.value = true;
    const data = await getDreamCountRanking();
    rankings.value = data.rankings || [];
    totalUsers.value = data.totalUsers || 0;
  } catch (err) {
    console.error("랭킹 조회 실패:", err);
    error.value = "랭킹을 불러오는데 실패했습니다.";
  } finally {
    isLoading.value = false;
  }
});

// 상위 3명 (트로피용)
const topThree = computed(() => rankings.value.slice(0, 3));

// 나머지 랭킹
const restRankings = computed(() => rankings.value.slice(3));

// 순위에 따른 카드 스타일
function getTopCardClass(rank) {
  switch (rank) {
    case 1:
      return "top-card gold";
    case 2:
      return "top-card silver";
    case 3:
      return "top-card bronze";
    default:
      return "top-card";
  }
}
</script>

<template>
  <div class="ranking-container">
    <header class="ranking-header">
      <h1 class="title">
        <span class="title-icon">
          <!-- Trophy SVG -->
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"></path>
            <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"></path>
            <path d="M4 22h16"></path>
            <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"></path>
            <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"></path>
            <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"></path>
          </svg>
        </span>
        꿈 일기 랭킹
      </h1>
      <p class="subtitle">가장 많은 꿈 일기를 작성한 몽글러들!</p>
    </header>

    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-state">
      <div class="spinner"></div>
      <p>랭킹을 불러오는 중...</p>
    </div>

    <!-- 에러 상태 -->
    <div v-else-if="error" class="error-state">
      <span class="error-icon">
        <!-- Sad Face SVG -->
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <path d="M16 16s-1.5-2-4-2-4 2-4 2"></path>
          <line x1="9" y1="9" x2="9.01" y2="9"></line>
          <line x1="15" y1="9" x2="15.01" y2="9"></line>
        </svg>
      </span>
      <p>{{ error }}</p>
    </div>

    <!-- 데이터 없음 -->
    <div v-else-if="rankings.length === 0" class="empty-state">
      <span class="empty-icon">
        <!-- Note/Edit SVG -->
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"></path>
          <polyline points="14 2 14 8 20 8"></polyline>
          <line x1="16" y1="13" x2="8" y2="13"></line>
          <line x1="16" y1="17" x2="8" y2="17"></line>
          <line x1="10" y1="9" x2="8" y2="9"></line>
        </svg>
      </span>
      <p>아직 꿈 일기를 작성한 사용자가 없습니다.</p>
      <p class="empty-hint">첫 번째 랭커가 되어보세요!</p>
    </div>

    <!-- 랭킹 표시 -->
    <div v-else class="ranking-content">
      <!-- 상위 3명 포디움 -->
      <div class="podium-section">
        <div class="podium">
          <!-- 2등 (왼쪽) -->
          <div v-if="topThree[1]" :class="getTopCardClass(2)">
            <div class="medal silver-medal">
              <!-- Silver Medal SVG -->
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="8" fill="#C0C0C0" stroke="#A8A8A8" stroke-width="2" />
                <text x="12" y="16" text-anchor="middle" fill="#666" font-size="10" font-weight="bold">2</text>
              </svg>
            </div>
            <div class="avatar silver-avatar">2</div>
            <div class="name">{{ topThree[1].maskedName }}</div>
            <div class="dream-count">{{ topThree[1].dreamCount }}개</div>
            <div class="podium-stand silver-stand"></div>
          </div>
          <div v-else class="top-card placeholder"></div>

          <!-- 1등 (가운데) -->
          <div v-if="topThree[0]" :class="getTopCardClass(1)">
            <div class="crown">
              <!-- Crown SVG -->
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
                <path d="M2 20h20L19 8l-5 4-2-6-2 6-5-4-3 12z" fill="#FFD700" stroke="#DAA520" stroke-width="1.5" />
                <circle cx="12" cy="6" r="2" fill="#FFD700" stroke="#DAA520" stroke-width="1" />
                <circle cx="4" cy="8" r="1.5" fill="#FFD700" stroke="#DAA520" stroke-width="1" />
                <circle cx="20" cy="8" r="1.5" fill="#FFD700" stroke="#DAA520" stroke-width="1" />
              </svg>
            </div>
            <div class="medal gold-medal">
              <!-- Gold Medal SVG -->
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="8" fill="#FFD700" stroke="#DAA520" stroke-width="2" />
                <text x="12" y="16" text-anchor="middle" fill="#8B6914" font-size="10" font-weight="bold">1</text>
              </svg>
            </div>
            <div class="avatar gold-avatar">1</div>
            <div class="name">{{ topThree[0].maskedName }}</div>
            <div class="dream-count">{{ topThree[0].dreamCount }}개</div>
            <div class="podium-stand gold-stand"></div>
          </div>
          <div v-else class="top-card placeholder"></div>

          <!-- 3등 (오른쪽) -->
          <div v-if="topThree[2]" :class="getTopCardClass(3)">
            <div class="medal bronze-medal">
              <!-- Bronze Medal SVG -->
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="8" fill="#CD7F32" stroke="#8B4513" stroke-width="2" />
                <text x="12" y="16" text-anchor="middle" fill="#5C3317" font-size="10" font-weight="bold">3</text>
              </svg>
            </div>
            <div class="avatar bronze-avatar">3</div>
            <div class="name">{{ topThree[2].maskedName }}</div>
            <div class="dream-count">{{ topThree[2].dreamCount }}개</div>
            <div class="podium-stand bronze-stand"></div>
          </div>
          <div v-else class="top-card placeholder"></div>
        </div>
      </div>

      <!-- 나머지 랭킹 리스트 -->
      <div v-if="restRankings.length > 0" class="ranking-list">
        <div class="list-header">
          <span>순위</span>
          <span>이름</span>
          <span>꿈 일기 수</span>
        </div>
        <div v-for="item in restRankings" :key="item.rank" class="ranking-item">
          <span class="rank">{{ item.rank }}</span>
          <span class="name">{{ item.maskedName }}</span>
          <span class="count">{{ item.dreamCount }}개</span>
        </div>
      </div>

      <!-- 총 참여자 수 -->
      <div class="total-users">
        총
        <strong>{{ totalUsers }}</strong>
        명의 몽글러가 참여 중!
      </div>
    </div>
  </div>
</template>

<style scoped>
.ranking-container {
  width: 100%;
  max-width: 600px;
  padding: 2rem;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.ranking-header {
  text-align: center;
  margin-bottom: 2rem;
}

.title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-family: "Nunito", sans-serif;
  font-size: 2rem;
  font-weight: 800;
  color: white;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  margin: 0;
}

.title-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  animation: bounce 2s infinite;
  color: #ffd700;
}

.title-icon svg {
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.subtitle {
  color: rgba(255, 255, 255, 0.85);
  font-family: "Nunito", sans-serif;
  font-size: 1rem;
  margin-top: 0.5rem;
}

/* 로딩 상태 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem;
  color: white;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 에러 및 빈 상태 */
.error-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 3rem;
  color: white;
  text-align: center;
}

.error-icon,
.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.9);
}

.empty-hint {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

/* 포디움 섹션 */
.podium-section {
  margin-bottom: 2rem;
}

.podium {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 1rem;
}

.top-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1.5rem 1rem 0;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 20px 20px 0 0;
  min-width: 100px;
  position: relative;
  transition: transform 0.3s ease;
}

.top-card:hover {
  transform: translateY(-5px);
}

.top-card.placeholder {
  visibility: hidden;
}

.top-card.gold {
  background: linear-gradient(180deg, rgba(255, 215, 0, 0.3), rgba(255, 215, 0, 0.1));
  border: 2px solid rgba(255, 215, 0, 0.5);
}

.top-card.silver {
  background: linear-gradient(180deg, rgba(192, 192, 192, 0.3), rgba(192, 192, 192, 0.1));
  border: 2px solid rgba(192, 192, 192, 0.5);
}

.top-card.bronze {
  background: linear-gradient(180deg, rgba(205, 127, 50, 0.3), rgba(205, 127, 50, 0.1));
  border: 2px solid rgba(205, 127, 50, 0.5);
}

.crown {
  position: absolute;
  top: -30px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.medal {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.5rem;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: "Nunito", sans-serif;
  font-weight: 800;
  font-size: 1.2rem;
  color: white;
  margin-bottom: 0.5rem;
}

.gold-avatar {
  background: linear-gradient(135deg, #ffd700, #ffb347);
  box-shadow: 0 4px 15px rgba(255, 215, 0, 0.4);
}

.silver-avatar {
  background: linear-gradient(135deg, #c0c0c0, #a8a8a8);
  box-shadow: 0 4px 15px rgba(192, 192, 192, 0.4);
}

.bronze-avatar {
  background: linear-gradient(135deg, #cd7f32, #b8860b);
  box-shadow: 0 4px 15px rgba(205, 127, 50, 0.4);
}

.top-card .name {
  font-family: "Nunito", sans-serif;
  font-weight: 700;
  font-size: 1rem;
  color: white;
  margin-bottom: 0.25rem;
}

.top-card .dream-count {
  font-family: "Nunito", sans-serif;
  font-weight: 600;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 1rem;
}

.podium-stand {
  width: 100%;
  border-radius: 0;
}

.gold-stand {
  height: 80px;
  background: linear-gradient(180deg, #ffd700, #daa520);
}

.silver-stand {
  height: 60px;
  background: linear-gradient(180deg, #c0c0c0, #a0a0a0);
}

.bronze-stand {
  height: 40px;
  background: linear-gradient(180deg, #cd7f32, #8b4513);
}

/* 랭킹 리스트 */
.ranking-list {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1rem;
  margin-bottom: 1.5rem;
}

.list-header {
  display: grid;
  grid-template-columns: 60px 1fr 100px;
  padding: 0.75rem 1rem;
  font-family: "Nunito", sans-serif;
  font-weight: 700;
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.7);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.ranking-item {
  display: grid;
  grid-template-columns: 60px 1fr 100px;
  padding: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  transition: background 0.2s ease;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-item:hover {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
}

.ranking-item .rank {
  font-family: "Nunito", sans-serif;
  font-weight: 800;
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.9);
}

.ranking-item .name {
  font-family: "Nunito", sans-serif;
  font-weight: 600;
  color: white;
}

.ranking-item .count {
  font-family: "Nunito", sans-serif;
  font-weight: 700;
  color: #a8d8ea;
  text-align: right;
}

/* 총 참여자 수 */
.total-users {
  text-align: center;
  font-family: "Nunito", sans-serif;
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.85);
  padding: 1rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 30px;
}

.total-users strong {
  color: #f3e366;
  font-weight: 800;
}

/* 반응형 */
@media (max-width: 480px) {
  .ranking-container {
    padding: 1rem;
  }

  .title {
    font-size: 1.5rem;
  }

  .title-icon svg {
    width: 32px;
    height: 32px;
  }

  .podium {
    gap: 0.5rem;
  }

  .top-card {
    min-width: 80px;
    padding: 1rem 0.5rem 0;
  }

  .medal svg {
    width: 24px;
    height: 24px;
  }

  .crown svg {
    width: 22px;
    height: 22px;
  }

  .avatar {
    width: 40px;
    height: 40px;
    font-size: 1rem;
  }

  .top-card .name {
    font-size: 0.85rem;
  }

  .top-card .dream-count {
    font-size: 0.8rem;
  }

  .list-header,
  .ranking-item {
    grid-template-columns: 50px 1fr 80px;
    padding: 0.75rem;
  }
}
</style>

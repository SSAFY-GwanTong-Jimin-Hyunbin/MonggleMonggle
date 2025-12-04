<template>
  <div class="monthly-analysis-card">
    <div class="card-header">
      <button @click="handleBack" class="back-btn">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
      </button>
      <h2 class="page-title">월별 분석</h2>
      <div class="spacer"></div>
    </div>

    <div class="monthly-analysis-content">
      <!-- 월 선택 -->
      <div class="month-selector-section">
        <button @click="changeMonth(-1)" class="month-nav-btn">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
        </button>
        <div class="current-month">
          {{ currentYear }}년 {{ currentMonth }}월
        </div>
        <button @click="changeMonth(1)" class="month-nav-btn">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </button>
      </div>

      <!-- 통계 카드 -->
      <div class="stats-grid">
        <div class="stat-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <div class="stat-icon">📝</div>
          <div class="stat-value">{{ monthlyStats.totalDreams }}</div>
          <div class="stat-label">기록된 꿈</div>
        </div>
        <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
          <div class="stat-icon">❤️</div>
          <div class="stat-value">{{ monthlyStats.favoriteTheme }}</div>
          <div class="stat-label">주요 테마</div>
        </div>
        <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
          <div class="stat-icon">🌟</div>
          <div class="stat-value">{{ monthlyStats.averageMood }}</div>
          <div class="stat-label">평균 감정</div>
        </div>
        <div class="stat-card" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
          <div class="stat-icon">🔥</div>
          <div class="stat-value">{{ monthlyStats.streak }}</div>
          <div class="stat-label">연속 기록</div>
        </div>
      </div>

      <!-- 키워드 클라우드 -->
      <div class="section-card">
        <h3 class="section-title">
          <span class="title-icon">☁️</span>
          이번 달 키워드
        </h3>
        <div class="keyword-cloud">
          <span 
            v-for="keyword in keywords" 
            :key="keyword.text"
            class="keyword-tag"
            :style="{ 
              fontSize: keyword.size + 'rem',
              color: keyword.color
            }"
          >
            {{ keyword.text }}
          </span>
        </div>
      </div>

      <!-- 감정 차트 -->
      <div class="section-card">
        <h3 class="section-title">
          <span class="title-icon">📊</span>
          감정 분석
        </h3>
        <div class="emotion-chart">
          <div 
            v-for="emotion in emotions" 
            :key="emotion.name"
            class="emotion-bar-container"
          >
            <div class="emotion-label">
              <span class="emotion-emoji">{{ emotion.emoji }}</span>
              <span class="emotion-name">{{ emotion.name }}</span>
            </div>
            <div class="emotion-bar-bg">
              <div 
                class="emotion-bar" 
                :style="{ 
                  width: emotion.percentage + '%',
                  background: emotion.color
                }"
              >
                <span class="emotion-percentage">{{ emotion.percentage }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 월별 메모 -->
      <div class="section-card">
        <h3 class="section-title">
          <span class="title-icon">💭</span>
          이번 달 회고
        </h3>
        <div v-if="!editingMemo" class="memo-display">
          <p v-if="monthlyMemo">{{ monthlyMemo }}</p>
          <p v-else class="empty-memo">이번 달의 꿈에 대한 소감을 작성해보세요</p>
          <button @click="editingMemo = true" class="edit-memo-btn">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
            {{ monthlyMemo ? '수정하기' : '작성하기' }}
          </button>
        </div>
        <div v-else class="memo-edit">
          <textarea 
            v-model="tempMemo" 
            class="memo-textarea"
            placeholder="이번 달 꿈 기록을 되돌아보며 느낀 점을 자유롭게 작성해보세요..."
          />
          <div class="memo-actions">
            <button @click="saveMemo" class="save-btn">저장</button>
            <button @click="cancelEdit" class="cancel-btn">취소</button>
          </div>
        </div>
      </div>

      <!-- 꿈 목록 -->
      <div class="section-card">
        <h3 class="section-title">
          <span class="title-icon">📚</span>
          이번 달 꿈 목록
        </h3>
        <div v-if="monthlyDreams.length > 0" class="dream-list">
          <div 
            v-for="dream in monthlyDreams" 
            :key="dream.date"
            class="dream-item"
            @click="goToDream(dream.date)"
          >
            <div class="dream-date-badge" :style="{ background: dream.color }">
              {{ dream.day }}일
            </div>
            <div class="dream-info">
              <h4>{{ dream.title }}</h4>
              <p>{{ dream.preview }}</p>
            </div>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6"></polyline>
            </svg>
          </div>
        </div>
        <div v-else class="empty-state">
          <span class="empty-emoji">😴</span>
          <p>이번 달에 기록된 꿈이 없습니다</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useDreamEntriesStore } from '../stores/dreamEntriesStore';
import { useMonthlyMemoStore } from '../stores/monthlyMemoStore';

const router = useRouter();
const dreamEntriesStore = useDreamEntriesStore();
const memoStore = useMonthlyMemoStore();
const { postedDates } = storeToRefs(dreamEntriesStore);

const currentYear = ref(new Date().getFullYear());
const currentMonth = ref(new Date().getMonth() + 1);
const editingMemo = ref(false);
const monthlyMemo = ref('');
const tempMemo = ref('');

// 월 변경
function changeMonth(delta) {
  currentMonth.value += delta;
  if (currentMonth.value > 12) {
    currentMonth.value = 1;
    currentYear.value += 1;
  } else if (currentMonth.value < 1) {
    currentMonth.value = 12;
    currentYear.value -= 1;
  }
  loadMonthlyMemo();
}

// 해당 월의 꿈 필터링
const monthlyDreams = computed(() => {
  const dreams = [];
  const yearMonth = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}`;
  
  Object.entries(postedDates.value).forEach(([dateKey, dream]) => {
    if (dateKey.startsWith(yearMonth)) {
      const day = parseInt(dateKey.split('-')[2]);
      dreams.push({
        date: dateKey,
        day: day,
        title: dream.title,
        preview: dream.content.substring(0, 50) + (dream.content.length > 50 ? '...' : ''),
        color: dream.color || '#A2D2FF'
      });
    }
  });
  
  return dreams.sort((a, b) => b.day - a.day);
});

// 통계 계산
const monthlyStats = computed(() => {
  const total = monthlyDreams.value.length;
  const themes = ['모험', '일상', '판타지', '공포', '낭만'];
  const moods = ['행복', '평온', '흥분', '불안', '슬픔'];
  
  return {
    totalDreams: total,
    favoriteTheme: themes[Math.floor(Math.random() * themes.length)],
    averageMood: moods[Math.floor(Math.random() * moods.length)],
    streak: Math.min(total, 7) + '일'
  };
});

// 키워드
const keywords = [
  { text: '하늘', size: 1.8, color: '#667eea' },
  { text: '바다', size: 1.5, color: '#4facfe' },
  { text: '여행', size: 2, color: '#f093fb' },
  { text: '친구', size: 1.3, color: '#43e97b' },
  { text: '집', size: 1.6, color: '#fa709a' },
  { text: '학교', size: 1.4, color: '#30cfd0' },
  { text: '비행', size: 1.7, color: '#a8edea' },
  { text: '음악', size: 1.2, color: '#ffd89b' },
];

// 감정 분석
const emotions = [
  { emoji: '😊', name: '행복', percentage: 35, color: 'linear-gradient(90deg, #FFD89B, #FFA36C)' },
  { emoji: '😌', name: '평온', percentage: 25, color: 'linear-gradient(90deg, #A8EDEA, #43E97B)' },
  { emoji: '😮', name: '놀람', percentage: 20, color: 'linear-gradient(90deg, #667eea, #764ba2)' },
  { emoji: '😰', name: '불안', percentage: 15, color: 'linear-gradient(90deg, #F093FB, #F5576C)' },
  { emoji: '😢', name: '슬픔', percentage: 5, color: 'linear-gradient(90deg, #4FACFE, #00F2FE)' },
];

function handleBack() {
  router.push({ name: 'calendar' });
}

function goToDream(dateKey) {
  // 해당 날짜의 꿈으로 이동
  router.push({ name: 'calendar', query: { date: dateKey } });
}

function loadMonthlyMemo() {
  monthlyMemo.value = memoStore.getMonthlyMemo(currentYear.value, currentMonth.value);
  tempMemo.value = monthlyMemo.value;
}

function saveMemo() {
  memoStore.saveMonthlyMemo(currentYear.value, currentMonth.value, tempMemo.value);
  monthlyMemo.value = tempMemo.value;
  editingMemo.value = false;
}

function cancelEdit() {
  tempMemo.value = monthlyMemo.value;
  editingMemo.value = false;
}

// 초기 로드
loadMonthlyMemo();
watch([currentYear, currentMonth], loadMonthlyMemo);
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;600;700;800&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Dongle:wght@300;400;700&display=swap');

.monthly-analysis-card {
  background: white;
  border-radius: 40px;
  padding: 2rem;
  width: 100%;
  max-width: 900px;
  box-shadow: 0 20px 60px rgba(100, 100, 200, 0.15);
  font-family: 'Nunito', sans-serif;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.back-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #888;
  padding: 5px;
  transition: color 0.2s;
}

.back-btn:hover {
  color: #333;
}

.page-title {
  font-family: 'Dongle', sans-serif;
  font-size: 2.5rem;
  font-weight: 700;
  color: #333;
  margin: 0;
  line-height: 1;
}

.spacer {
  width: 34px;
}

.monthly-analysis-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.month-selector-section {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 2rem;
  padding: 1rem;
}

.month-nav-btn {
  background: #F0F4F8;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  cursor: pointer;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.month-nav-btn:hover {
  background: #E8F0FE;
  color: #667eea;
  transform: scale(1.1);
}

.current-month {
  font-size: 1.8rem;
  font-weight: 700;
  color: #333;
  min-width: 200px;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.stat-card {
  padding: 1.5rem;
  border-radius: 20px;
  color: white;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.9;
}

.section-card {
  background: #F8FAFF;
  border-radius: 20px;
  padding: 2rem;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.5rem;
  margin: 0 0 1.5rem 0;
  color: #333;
}

.title-icon {
  font-size: 1.5rem;
}

.keyword-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  justify-content: center;
  padding: 2rem 0;
}

.keyword-tag {
  font-weight: 700;
  opacity: 0.8;
  transition: opacity 0.2s, transform 0.2s;
  cursor: default;
}

.keyword-tag:hover {
  opacity: 1;
  transform: scale(1.1);
}

.emotion-chart {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.emotion-bar-container {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.emotion-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  min-width: 100px;
}

.emotion-emoji {
  font-size: 1.5rem;
}

.emotion-name {
  font-weight: 600;
  color: #666;
}

.emotion-bar-bg {
  flex: 1;
  height: 35px;
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
}

.emotion-bar {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 1rem;
  transition: width 0.5s ease;
}

.emotion-percentage {
  color: white;
  font-weight: 700;
  font-size: 0.9rem;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.memo-display {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.memo-display p {
  line-height: 1.8;
  color: #666;
  white-space: pre-wrap;
}

.empty-memo {
  color: #999;
  font-style: italic;
}

.edit-memo-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: 2px solid #E8F0FE;
  background: white;
  border-radius: 12px;
  color: #667eea;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  align-self: flex-start;
}

.edit-memo-btn:hover {
  background: #F8F9FF;
  border-color: #A2D2FF;
  transform: translateY(-2px);
}

.memo-edit {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.memo-textarea {
  width: 100%;
  min-height: 150px;
  padding: 1rem;
  border: 2px solid #E8F0FE;
  border-radius: 15px;
  font-size: 1rem;
  font-family: 'Nunito', sans-serif;
  resize: vertical;
  transition: border-color 0.3s;
}

.memo-textarea:focus {
  outline: none;
  border-color: #A2D2FF;
}

.memo-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

.save-btn,
.cancel-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.save-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.cancel-btn {
  background: #F0F4F8;
  color: #666;
}

.cancel-btn:hover {
  background: #E8F0FE;
}

.dream-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.dream-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: white;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.2s;
}

.dream-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.dream-date-badge {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.1rem;
  color: white;
  flex-shrink: 0;
}

.dream-info {
  flex: 1;
}

.dream-info h4 {
  margin: 0 0 0.25rem 0;
  font-size: 1.1rem;
  color: #333;
}

.dream-info p {
  margin: 0;
  font-size: 0.9rem;
  color: #999;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #999;
}

.empty-emoji {
  font-size: 4rem;
  display: block;
  margin-bottom: 1rem;
}
</style>


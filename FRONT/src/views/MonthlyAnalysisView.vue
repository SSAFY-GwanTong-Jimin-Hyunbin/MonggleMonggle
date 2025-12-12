<template>
  <div class="monthly-analysis-card">
    <div class="card-header">
      <button @click="handleBack" class="back-btn">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7" />
        </svg>
      </button>
      <h2 class="page-title">
        월별 분석
        <span class="title-badge">Monthly</span>
      </h2>
      <button v-if="hasReportContent" class="report-btn" @click="openReport" :disabled="isLockedMonth" aria-label="월별 분석 리포트 열기">
        <span v-if="isLetterUnread" class="report-badge" aria-label="새 편지 도착">1</span>
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M3 8l9 6 9-6" />
          <path d="M5 19h14a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2z" />
          <path d="M3 8l9 6 9-6" />
        </svg>
        <span>우체통</span>
      </button>
      <div class="header-actions">
        <button @click="handleClose" class="close-btn" aria-label="닫기">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>

    <div class="month-selector-section">
      <button @click="changeMonth(-1)" class="month-nav-btn">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </button>
      <div class="current-month">{{ currentYear }}년 {{ currentMonth }}월</div>
      <button @click="changeMonth(1)" class="month-nav-btn">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="9 18 15 12 9 6"></polyline>
        </svg>
      </button>
    </div>

    <div class="monthly-analysis-content" v-if="!isLockedMonth">
      <!-- 왼쪽 컬럼 -->
      <div class="left-column">
        <!-- 통계 카드 -->
        <div class="stats-grid">
          <div class="stat-card dreams-card">
            <div class="stat-icon-wrap">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z" />
                <polyline points="14 2 14 8 20 8" />
                <line x1="16" y1="13" x2="8" y2="13" />
                <line x1="16" y1="17" x2="8" y2="17" />
                <line x1="10" y1="9" x2="8" y2="9" />
              </svg>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ monthlyStats.totalDreams }}</div>
              <div class="stat-label">기록된 꿈</div>
            </div>
          </div>
          <div class="stat-card streak-card">
            <div class="stat-icon-wrap">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 2c1 3 2.5 3.5 3.5 4.5A5 5 0 0 1 17 10a5 5 0 1 1-10 0c0-.3 0-.6.1-.9a2 2 0 1 0 3.3-2C11 6.5 12 5 12 2z" />
              </svg>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ monthlyStats.streak }}</div>
              <div class="stat-label">연속 기록</div>
            </div>
          </div>
        </div>

        <!-- 꿈 목록 -->
        <div class="section-card">
          <div class="dream-header">
            <h3 class="section-title">
              <span class="title-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
                  <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
                </svg>
              </span>
              이번 달 꿈 목록
            </h3>
            <div v-if="totalDreamPages > 1" class="dream-pagination">
              <button class="page-btn icon" @click="prevDreamPage" :disabled="currentDreamPage === 1">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="15 18 9 12 15 6"></polyline>
                </svg>
              </button>
              <span class="page-indicator">{{ currentDreamPage }} / {{ totalDreamPages }}</span>
              <button class="page-btn icon" @click="nextDreamPage" :disabled="currentDreamPage === totalDreamPages">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="9 6 15 12 9 18"></polyline>
                </svg>
              </button>
            </div>
          </div>
          <div v-if="monthlyDreams.length > 0" class="dream-list">
            <div v-for="dream in visibleDreams" :key="dream.date" class="dream-item" @click="goToDream(dream.date)">
              <div class="dream-date-badge" :class="dream.colorClass">{{ dream.day }}일</div>
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
            <span class="empty-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
              </svg>
            </span>
            <p>이번 달에 기록된 꿈이 없습니다</p>
          </div>
        </div>
      </div>

      <!-- 오른쪽 컬럼 -->
      <div class="right-column">
        <!-- 월별 메모 -->
        <div class="section-card memo-section">
          <div class="section-header">
            <h3 class="section-title">
              <span class="title-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                </svg>
              </span>
              이번 달 회고
            </h3>
            <button @click="startAddMemo" class="add-memo-btn">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19"></line>
                <line x1="5" y1="12" x2="19" y2="12"></line>
              </svg>
              메모 추가
            </button>
          </div>

          <!-- 새 메모 작성 -->
          <div v-if="isAddingMemo" class="memo-note new-memo">
            <textarea v-model="newMemoContent" class="memo-textarea" placeholder="이번 달 꿈 기록을 되돌아보며 느낀 점을 자유롭게 작성해보세요..." ref="newMemoInput" />
            <div class="memo-note-actions">
              <button @click="addNewMemo" class="save-btn" :disabled="!newMemoContent.trim()">저장</button>
              <button @click="cancelAddMemo" class="cancel-btn">취소</button>
            </div>
          </div>

          <!-- 메모 목록 -->
          <div v-if="monthlyMemos.length > 0" class="memo-list">
            <div v-for="memo in monthlyMemos" :key="memo.id" class="memo-note" :class="getMemoColorClass(memo.id)">
              <div v-if="editingMemoId !== memo.id" class="memo-note-content">
                <p :class="{ 'memo-text-clamped': !isExpanded(memo.id) }">{{ memo.content }}</p>
                <button v-if="shouldShowExpandBtn(memo.content)" @click="toggleExpand(memo.id)" class="expand-btn">
                  {{ isExpanded(memo.id) ? "접기" : "더보기" }}
                </button>
                <div class="memo-note-footer">
                  <span class="memo-date">{{ formatDate(memo.createdAt) }}</span>
                  <div class="memo-note-btns">
                    <button @click="startEditMemo(memo)" class="memo-action-btn edit">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                      </svg>
                    </button>
                    <button @click="deleteMemoItem(memo.id)" class="memo-action-btn delete">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
              <div v-else class="memo-note-edit">
                <textarea v-model="editMemoContent" class="memo-textarea" />
                <div class="memo-note-actions">
                  <button @click="saveEditMemo" class="save-btn" :disabled="!editMemoContent.trim()">저장</button>
                  <button @click="cancelEditMemo" class="cancel-btn">취소</button>
                </div>
              </div>
            </div>
          </div>

          <!-- 빈 상태 -->
          <div v-else-if="!isAddingMemo" class="memo-empty">
            <p>이번 달의 꿈에 대한 소감을 작성해보세요</p>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="lock-card">
      <div class="lock-text">
        <div class="lock-caption">
          <div class="lock-emoji">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 11h18v6a3 3 0 0 1-3 3H6a3 3 0 0 1-3-3v-6z" />
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              <path d="M14 7h2" />
            </svg>
          </div>
          <div class="caption-main">이 달의 꿈 기록은 {{ nextAvailableText }}에 열려요</div>
          <div class="caption-sub">조금만 더 기다려 주세요 ✨</div>
        </div>
      </div>
    </div>

    <!-- 월별 리포트 모달 -->
    <transition name="fade">
      <div v-if="isReportOpen" class="report-modal-overlay" @click.self="closeReport">
        <div class="report-modal">
          <div class="report-modal-header">
            <div class="report-mailbox">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 6h16a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2Z" />
                <path d="m4 8 7.4 4.63a2 2 0 0 0 2.2 0L21 8" />
                <path d="M4 16.5 9 13" />
                <path d="m20 16.5-5-3.5" />
              </svg>
            </div>
            <div>
              <div class="report-title">{{ currentYear }}년 {{ currentMonth }}월 편지</div>
            </div>
            <button class="modal-close-btn" @click="closeReport" aria-label="리포트 닫기">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>
          </div>

          <div class="report-letter">
            <div class="letter-top">
              <div class="letter-stamp">Monthly Letter</div>
              <div class="letter-tofrom">
                <div class="letter-line">
                  <span>To.</span>
                  {{ currentYear }}년 {{ currentMonth }}월의 {{ reportReceiver }}
                </div>
                <div class="letter-line">
                  <span>From.</span>
                  몽글몽글
                </div>
              </div>
            </div>

            <div class="letter-body">
              <div v-if="isLoadingReport" class="letter-placeholder">편지를 준비하는 중이에요...</div>
              <div v-else-if="reportError" class="letter-error">{{ reportError }}</div>
              <div v-else-if="monthlyReport" class="letter-content" v-html="formattedReport"></div>
              <div v-else class="letter-placeholder">편지 내용을 여기에 담아 전달해 드릴게요.</div>
            </div>

            <div class="letter-footer">
              좋았던 꿈도, 조금 무서웠던 꿈도 모두 당신의 이야기예요. 다음 달에도 편지를 띄워요
              <svg class="footer-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79Z" />
              </svg>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useDreamEntriesStore } from "../stores/dreamEntriesStore";
import { useAuthStore } from "../stores/authStore";
import { monthlyAnalysisService } from "../services/monthlyAnalysisService";

const route = useRoute();
const router = useRouter();
const dreamEntriesStore = useDreamEntriesStore();
const authStore = useAuthStore();
const { postedDates } = storeToRefs(dreamEntriesStore);
const { currentUser } = storeToRefs(authStore);

const now = ref(new Date());
let nowTimer = null;

const lastMonthDate = computed(() => new Date(now.value.getFullYear(), now.value.getMonth() - 1, 1));
const currentYear = ref(lastMonthDate.value.getFullYear());
const currentMonth = ref(lastMonthDate.value.getMonth() + 1);
const selectedMonthDate = computed(() => new Date(currentYear.value, currentMonth.value - 1, 1));

const monthlyReport = ref("");
const isLoadingReport = ref(false);
const reportError = ref("");
const isGeneratingReport = ref(false); // 리포트 생성 중 상태 (중복 호출 방지)
let reportAbortController = null; // 요청 취소용

const isLockedMonth = computed(() => {
  const selectedKey = currentYear.value * 12 + currentMonth.value;
  const currentKey = now.value.getFullYear() * 12 + (now.value.getMonth() + 1);
  return selectedKey >= currentKey;
});

// 직전 달인지 확인 (편지 생성/조회는 직전 달만 가능)
const isPreviousMonth = computed(() => {
  const nowDate = now.value;
  const lastMonth = new Date(nowDate.getFullYear(), nowDate.getMonth() - 1, 1);
  return currentYear.value === lastMonth.getFullYear() && currentMonth.value === lastMonth.getMonth() + 1;
});

const formattedReport = computed(() => (monthlyReport.value ? monthlyReport.value.replace(/\n/g, "<br />") : ""));
const reportReceiver = computed(() => {
  const name = currentUser.value?.name;
  return name ? `${name} 님에게` : "나에게";
});

const nextAvailableText = computed(() => {
  // 선택된 월 기준 다음 달 1일을 안내
  const nextMonthDate = new Date(currentYear.value, currentMonth.value, 1);
  const y = nextMonthDate.getFullYear();
  const m = String(nextMonthDate.getMonth() + 1).padStart(2, "0");
  return `${y}년 ${m}월 01일`;
});

// 메모 관련 상태
const monthlyMemos = ref([]);
const isAddingMemo = ref(false);
const newMemoContent = ref("");
const editingMemoId = ref(null);
const editMemoContent = ref("");
const expandedMemos = ref(new Set());
const isReportOpen = ref(false);
const letterReadStatus = ref({});
const LETTER_READ_KEY = "monthlyReportRead";
// 편지 버튼 표시 여부 - 직전 달이면 항상 표시, 그 외에는 기존 편지가 있을 때만 표시
const hasReportContent = computed(() => {
  // 직전 달인 경우 항상 편지 버튼 표시 (로딩 중이거나 편지가 있거나 생성 가능)
  if (isPreviousMonth.value) {
    return true;
  }
  // 직전 달이 아닌 과거 달은 편지 기능 비활성화
  return false;
});

// 색상 클래스 배열
const colorClasses = ["color-purple", "color-pink", "color-blue"];
const memoColorClasses = ["memo-purple", "memo-pink", "memo-pink", "memo-purple"];

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
  updateRouteQuery();
}

// 라우터 쿼리와 상태 동기화
function syncFromRoute() {
  const qYear = Number(route.query.year);
  const qMonth = Number(route.query.month);
  if (!Number.isNaN(qYear) && qYear > 0) currentYear.value = qYear;
  if (!Number.isNaN(qMonth) && qMonth >= 1 && qMonth <= 12) currentMonth.value = qMonth;
}

function updateRouteQuery() {
  const query = {
    ...route.query,
    year: currentYear.value,
    month: String(currentMonth.value).padStart(2, "0"),
  };
  router.replace({ name: "monthly-analysis", query });
}

onMounted(() => {
  syncFromRoute();
  updateRouteQuery();
  loadLetterReadStatus();
  nowTimer = setInterval(() => {
    now.value = new Date();
  }, 60 * 1000);
});

onUnmounted(() => {
  if (nowTimer) {
    clearInterval(nowTimer);
  }
  // 컴포넌트 언마운트 시 진행 중인 상태 정리
  isLoadingReport.value = false;
  isGeneratingReport.value = false;
});

watch(
  () => route.query,
  () => {
    syncFromRoute();
  }
);

// 해당 월의 꿈 필터링
const monthlyDreams = computed(() => {
  if (isLockedMonth.value) return [];

  const dreams = [];
  const yearMonth = `${currentYear.value}-${String(currentMonth.value).padStart(2, "0")}`;

  const entries = postedDates.value ? Object.entries(postedDates.value) : [];

  entries.forEach(([dateKey, dream], index) => {
    if (dateKey.startsWith(yearMonth)) {
      const day = parseInt(dateKey.split("-")[2]);
      const title = dream?.title || "";
      const content = dream?.content || "";
      dreams.push({
        date: dateKey,
        day: day,
        title,
        preview: content.substring(0, 50) + (content.length > 50 ? "..." : ""),
        colorClass: colorClasses[index % 3],
      });
    }
  });

  return dreams.sort((a, b) => a.day - b.day);
});

// 꿈 목록 페이지네이션 (4개씩)
const dreamPageSize = 4;
const currentDreamPage = ref(1);
const totalDreamPages = computed(() => Math.max(1, Math.ceil(monthlyDreams.value.length / dreamPageSize)));
const visibleDreams = computed(() => {
  const start = (currentDreamPage.value - 1) * dreamPageSize;
  return monthlyDreams.value.slice(start, start + dreamPageSize);
});

function nextDreamPage() {
  if (currentDreamPage.value < totalDreamPages.value) currentDreamPage.value += 1;
}

function prevDreamPage() {
  if (currentDreamPage.value > 1) currentDreamPage.value -= 1;
}

watch(monthlyDreams, () => {
  currentDreamPage.value = 1;
});

// 통계 계산 (실제 최장 연속 기록일을 계산)
const monthlyStats = computed(() => {
  const total = monthlyDreams.value.length;

  // 날짜(day)만 뽑아서 정렬 후 최장 연속 길이 계산
  const days = Array.from(new Set(monthlyDreams.value.map((d) => d.day))).sort((a, b) => a - b);
  let longest = 0;
  let current = 0;
  let prev = null;

  for (const day of days) {
    if (prev !== null && day === prev + 1) {
      current += 1;
    } else {
      current = 1;
    }
    longest = Math.max(longest, current);
    prev = day;
  }

  return {
    totalDreams: total,
    streak: `${longest}일`,
  };
});

function handleBack() {
  // 브라우저 히스토리가 있을 경우 뒤로가기, 없으면 캘린더로 이동
  const historyLength = window.history.length;
  if (historyLength > 1) {
    router.back();
  } else {
    router.push({ name: "calendar" });
  }
}

function handleClose() {
  router.push({ name: "calendar" });
}

function goToDream(dateKey) {
  // 해당 날짜의 꿈으로 이동
  router.push({ name: "write", query: { date: dateKey } });
}

function currentMonthKey() {
  return `${currentYear.value}-${String(currentMonth.value).padStart(2, "0")}`;
}

function loadLetterReadStatus() {
  if (typeof window === "undefined") return;
  const saved = localStorage.getItem(LETTER_READ_KEY);
  letterReadStatus.value = saved ? JSON.parse(saved) : {};
}

function persistLetterReadStatus() {
  if (typeof window === "undefined") return;
  localStorage.setItem(LETTER_READ_KEY, JSON.stringify(letterReadStatus.value));
}

const isLetterUnread = computed(() => {
  const key = currentMonthKey();
  return !letterReadStatus.value?.[key];
});

// 메모 로드 (백엔드 연동)
async function loadMonthlyMemos() {
  if (isLockedMonth.value) {
    monthlyMemos.value = [];
    return;
  }

  // 상태 초기화
  isAddingMemo.value = false;
  newMemoContent.value = "";
  editingMemoId.value = null;
  editMemoContent.value = "";

  try {
    const memos = await monthlyAnalysisService.getMemo(currentYear.value, currentMonth.value);
    monthlyMemos.value = sanitizeMemos(
      Array.isArray(memos)
        ? memos.map((m) => ({
            id: m.memoId,
            content: m.memoContent,
            createdAt: m.createdDate || m.updatedDate || new Date().toISOString(),
          }))
        : []
    );
  } catch (err) {
    // 404면 메모 없음으로 처리
    if (err?.response?.status === 404) {
      monthlyMemos.value = [];
    } else {
      console.error("메모 조회 실패:", err);
    }
  }
}

// 내용이 비어 있는 메모 제거
function sanitizeMemos(memos) {
  if (!Array.isArray(memos)) return [];
  return memos.filter((memo) => memo?.content && memo.content.trim().length > 0);
}

// 선택된 연월의 꿈 일기 서버 동기화
function loadMonthlyDreams() {
  if (isLockedMonth.value) return;
  dreamEntriesStore.fetchDreamsByMonth(currentYear.value, currentMonth.value);
}

// 월간 리포트 조회(없으면 생성) - 직전 달만 조회/생성 가능
async function fetchMonthlyReport() {
  // 잠긴 달(현재 달 이후)이면 리포트 없음
  if (isLockedMonth.value) {
    monthlyReport.value = "";
    reportError.value = "";
    return;
  }

  // 직전 달이 아니면 (2달 이상 과거) 편지 기능 비활성화
  if (!isPreviousMonth.value) {
    monthlyReport.value = "";
    reportError.value = "";
    return;
  }

  // 이미 로딩 중이거나 생성 중이면 중복 호출 방지
  if (isLoadingReport.value || isGeneratingReport.value) {
    return;
  }

  // 직전 달인 경우에만 편지 조회/생성 진행
  isLoadingReport.value = true;
  reportError.value = "";

  try {
    // 먼저 기존 편지가 있는지 조회
    const response = await monthlyAnalysisService.getMonthlyAnalysis(currentYear.value, currentMonth.value);
    const report = response?.monthlyReport || "";

    if (report) {
      monthlyReport.value = report;
    } else {
      // 조회는 성공했지만 편지 내용이 비어있는 경우 자동 생성 시도
      await tryGenerateReport();
    }
  } catch (err) {
    if (err?.response?.status === 404) {
      // 리포트가 없는 경우 자동 생성 시도
      await tryGenerateReport();
    } else {
      reportError.value = err?.response?.data?.message || err.message || "월간 리포트를 불러오지 못했습니다.";
      monthlyReport.value = "";
    }
  } finally {
    isLoadingReport.value = false;
  }
}

// 월간 리포트 자동 생성 시도
async function tryGenerateReport() {
  // 이미 생성 중이면 중복 호출 방지
  if (isGeneratingReport.value) {
    return;
  }

  isGeneratingReport.value = true;

  try {
    const generated = await monthlyAnalysisService.generateMonthlyAnalysis(currentYear.value, currentMonth.value);
    const report = generated?.monthlyReport || "";

    if (report) {
      monthlyReport.value = report;
      reportError.value = "";
    } else {
      // 생성 요청은 성공했지만 아직 결과가 없는 경우 - 잠시 후 다시 조회
      await retryFetchReport();
    }
  } catch (genErr) {
    if (genErr?.response?.status === 400) {
      reportError.value = "이번 달에 작성된 꿈 일기가 없어요. 꿈 일기를 먼저 작성해주세요! 📝";
    } else if (genErr?.response?.status === 503) {
      reportError.value = "AI 서비스가 일시적으로 이용 불가해요. 잠시 후 다시 시도해주세요. 🔄";
    } else {
      reportError.value = genErr?.response?.data?.message || genErr.message || "월간 리포트를 생성하지 못했습니다.";
    }
    monthlyReport.value = "";
  } finally {
    isGeneratingReport.value = false;
  }
}

// 생성 후 결과 다시 조회 (polling)
async function retryFetchReport(retries = 3, delay = 2000) {
  for (let i = 0; i < retries; i++) {
    await new Promise((resolve) => setTimeout(resolve, delay));

    try {
      const response = await monthlyAnalysisService.getMonthlyAnalysis(currentYear.value, currentMonth.value);
      const report = response?.monthlyReport || "";

      if (report) {
        monthlyReport.value = report;
        reportError.value = "";
        return;
      }
    } catch (err) {
      // 아직 생성 중이면 계속 재시도
      if (err?.response?.status !== 404) {
        break;
      }
    }
  }

  // 재시도 후에도 결과가 없으면 에러 메시지
  if (!monthlyReport.value) {
    reportError.value = "편지 생성에 시간이 걸리고 있어요. 잠시 후 다시 확인해주세요. ⏳";
  }
}

// 새 메모 추가 시작
function startAddMemo() {
  isAddingMemo.value = true;
  newMemoContent.value = "";
}

// 새 메모 저장
async function addNewMemo() {
  if (!newMemoContent.value.trim()) return;
  try {
    await monthlyAnalysisService.saveMemo({
      year: currentYear.value,
      month: currentMonth.value,
      memoContent: newMemoContent.value.trim(),
    });
    await loadMonthlyMemos();
  } catch (err) {
    console.error("메모 저장 실패:", err);
    alert(err?.response?.data?.message || "메모 저장에 실패했습니다. 다시 시도해주세요.");
  }
}

// 새 메모 추가 취소
function cancelAddMemo() {
  isAddingMemo.value = false;
  newMemoContent.value = "";
}

// 메모 수정 시작
function startEditMemo(memo) {
  editingMemoId.value = memo.id;
  editMemoContent.value = memo.content;
}

// 메모 수정 저장
async function saveEditMemo() {
  if (!editMemoContent.value.trim()) return;
  try {
    await monthlyAnalysisService.saveMemo({
      year: currentYear.value,
      month: currentMonth.value,
      memoContent: editMemoContent.value.trim(),
      memoId: editingMemoId.value,
    });
    await loadMonthlyMemos();
  } catch (err) {
    console.error("메모 수정 실패:", err);
    alert(err?.response?.data?.message || "메모 수정에 실패했습니다. 다시 시도해주세요.");
  }
}

// 메모 수정 취소
function cancelEditMemo() {
  editingMemoId.value = null;
  editMemoContent.value = "";
}

// 메모 삭제
async function deleteMemoItem(memoId) {
  if (confirm("이 메모를 삭제하시겠습니까?")) {
    try {
      await monthlyAnalysisService.deleteMemo(memoId);
      await loadMonthlyMemos();
    } catch (err) {
      console.error("메모 삭제 실패:", err);
      alert(err?.response?.data?.message || "메모 삭제에 실패했습니다. 다시 시도해주세요.");
    }
  }
}

// 메모 색상 클래스
function getMemoColorClass(memoId) {
  const index = monthlyMemos.value.findIndex((m) => m.id === memoId);
  return memoColorClasses[index % memoColorClasses.length];
}

// 날짜 포맷
function formatDate(dateString) {
  const date = new Date(dateString);
  const yy = String(date.getFullYear()).slice(-2);
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  return `${yy}.${mm}.${dd}`;
}

// 메모 펼치기/접기
function toggleExpand(memoId) {
  if (expandedMemos.value.has(memoId)) {
    expandedMemos.value.delete(memoId);
  } else {
    expandedMemos.value.add(memoId);
  }
  // 반응성 트리거
  expandedMemos.value = new Set(expandedMemos.value);
}

function isExpanded(memoId) {
  return expandedMemos.value.has(memoId);
}

// 내용이 5줄 이상인지 체크 (대략 100자 이상)
function shouldShowExpandBtn(content) {
  return content.length > 100 || content.split("\n").length > 4;
}

function openReport() {
  if (isLockedMonth.value) return;
  isReportOpen.value = true;
  const key = currentMonthKey();
  letterReadStatus.value = { ...letterReadStatus.value, [key]: true };
  persistLetterReadStatus();
}

function closeReport() {
  isReportOpen.value = false;
}

// 초기 로드
loadMonthlyMemos();
loadMonthlyDreams();
fetchMonthlyReport();
watch([currentYear, currentMonth], () => {
  loadMonthlyMemos();
  loadMonthlyDreams();
  fetchMonthlyReport();
});
</script>

<style scoped>
/* 파스텔 색상 변수 */
:root {
  --pastel-purple: #cdb4db;
  --pastel-pink: #ffc8dd;
  --pastel-blue: #a2d2ff;
  --pastel-purple-dark: #b799c7;
  --pastel-pink-dark: #ffb3cc;
  --pastel-blue-dark: #8bb8e8;
}

.monthly-analysis-card {
  background: white;
  border-radius: 32px;
  padding: 1.5rem 2rem 2rem;
  width: 100%;
  max-width: 1100px;
  box-shadow: 0 16px 48px rgba(100, 100, 200, 0.12);
  animation: fadeSlideUp 0.5s ease;
  overflow: hidden; /* 레이아웃 튐 방지 */
}

@keyframes fadeSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.back-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
  padding: 8px;
  border-radius: 12px;
  transition: all 0.2s;
}

.back-btn:hover,
.close-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.header-actions {
  display: inline-flex;
  gap: 0.5rem;
  align-items: center;
}

.report-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.55rem 0.9rem;
  border: 1px solid rgba(205, 180, 219, 0.4);
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(205, 180, 219, 0.16), rgba(162, 210, 255, 0.16));
  color: #6a4a88;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(205, 180, 219, 0.18);
  font-family: "Dongle", sans-serif !important;
  font-weight: 700;
  font-size: 1.3rem;
  position: absolute !important;
  right: 53px;
  width: 90px;
  position: relative;
}

@media (max-width: 768px) {
  .report-btn {
    padding: 0.55rem;
    width: 42px;
    height: 42px;
    right: 52px;
    justify-content: center;
  }
  .report-btn span {
    display: none;
  }
}

.report-btn svg {
  stroke: currentColor;
}

.report-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  border-radius: 999px;
  background: #ff7bb1;
  box-shadow: 0 0 0 3px rgba(255, 123, 177, 0.25);
  color: #fff;
  font-family: "Dongle", sans-serif;
  font-weight: 700;
  font-size: 1.1rem;
  line-height: 18px;
  text-align: center;
  animation: badge-pulse 1.4s ease-in-out infinite;
}

@keyframes badge-pulse {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 3px rgba(255, 123, 177, 0.25);
    opacity: 1;
  }
  50% {
    transform: scale(1.12);
    box-shadow: 0 0 0 4px rgba(255, 123, 177, 0.35);
    opacity: 0.8;
  }
  100% {
    transform: scale(1);
    box-shadow: 0 0 0 3px rgba(255, 123, 177, 0.25);
    opacity: 1;
  }
}

.report-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(205, 180, 219, 0.28);
  color: #4c2b7b;
}

.report-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
  padding: 8px;
  border-radius: 12px;
  transition: all 0.2s;
}

.page-title {
  font-family: "Dongle", sans-serif;
  font-size: 2.2rem;
  font-weight: 700;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 1.25rem;
  border-radius: 999px;
  background: linear-gradient(135deg, rgba(205, 180, 219, 0.3), rgba(162, 210, 255, 0.3));
  -webkit-text-fill-color: #4c2b7b;
  line-height: 1.2;
  overflow: visible;
}

.title-badge {
  font-size: 0.7rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  padding: 0.3rem 0.8rem;
  border-radius: 999px;
  background: white;
  color: #b799c7;
  font-weight: 600;
}

.monthly-analysis-content {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(320px, 380px);
  gap: 1.8rem;
  align-items: start;
}

.left-column {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  min-width: 0;
}

.right-column {
  display: flex;
  flex-direction: column;
  padding-left: 1.8rem;
  border-left: 1px solid rgba(205, 180, 219, 0.3);
  min-width: 0; /* 폭 확장으로 인한 튐 방지 */
  height: 100%;
}

.right-column .memo-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.month-selector-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1.5rem;
  padding: 1rem;
  background: linear-gradient(135deg, rgba(205, 180, 219, 0.15), rgba(255, 200, 221, 0.15), rgba(162, 210, 255, 0.15));
  border-radius: 20px;
  margin-bottom: 1.5rem;
}

.month-nav-btn {
  background: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  cursor: pointer;
  color: #b799c7;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(205, 180, 219, 0.2);
}

.month-nav-btn:hover {
  background: #cdb4db;
  color: white;
  transform: scale(1.08);
}

.current-month {
  font-family: "Dongle", sans-serif;
  font-size: 3rem;
  font-weight: 700;
  color: #4c2b7b;
  min-width: 180px;
  text-align: center;
  line-height: 1.2;
}

.stats-grid {
  display: flex;
  gap: 0.75rem;
}

.stat-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 2rem;
  padding: 1rem 1.25rem;
  border-radius: 18px;
  transition: all 0.25s ease;
  cursor: default;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.dreams-card {
  background: linear-gradient(135deg, #e8dcf0 0%, #ddd0e8 100%);
  color: #5a3d7a;
  box-shadow: 0 6px 20px rgba(205, 180, 219, 0.25);
}

.dreams-card:hover {
  box-shadow: 0 8px 28px rgba(205, 180, 219, 0.4);
}

.streak-card {
  background: linear-gradient(135deg, #ffe4ec 0%, #ffd6e0 100%);
  color: #7a4a5a;
  box-shadow: 0 6px 20px rgba(255, 200, 221, 0.25);
}

.streak-card:hover {
  box-shadow: 0 8px 28px rgba(255, 200, 221, 0.4);
}

.stat-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.6);
  flex-shrink: 0;
}

.dreams-card .stat-icon-wrap svg {
  width: 26px;
  height: 26px;
  stroke: #8a6aa8;
}

.streak-card .stat-icon-wrap svg {
  width: 38px;
  height: 38px;
  stroke: #c97a8d;
  top: 5px;
}

.stat-content {
  display: flex;
  flex-direction: row;
  min-width: 0;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.stat-value {
  font-family: "Dongle", sans-serif;
  font-size: 3rem;
  font-weight: 700;
  line-height: 1;
  letter-spacing: -0.5px;
  color: #4c2b7b;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.5);
}

.stat-label {
  font-size: 1rem;
  opacity: 0.9;
  font-weight: 700;
  margin-top: 2px;
}

.section-card {
  background: linear-gradient(135deg, rgba(205, 180, 219, 0.1), rgba(255, 200, 221, 0.1), rgba(162, 210, 255, 0.1));
  border-radius: 20px;
  padding: 1.5rem;
  border: 1px solid rgba(205, 180, 219, 0.2);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-family: "Dongle", sans-serif;
  font-size: 1.8rem;
  margin: 0 0 1rem 0;
  color: #4c2b7b;
  line-height: 1.2;
}

.title-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.title-icon svg {
  width: 20px;
  height: 20px;
  stroke: #8a6aa8;
}

/* 메모 섹션 */
.memo-section {
  padding-bottom: 1rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.section-header .section-title {
  margin: 0;
}

.add-memo-btn {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #cdb4db, #b799c7);
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 3px 10px rgba(205, 180, 219, 0.3);
}

.add-memo-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(205, 180, 219, 0.4);
}

.memo-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  flex: 1;
  overflow-y: auto;
  /* max-height: 500px; */
  padding-right: 0.5rem;
  /* Firefox 스크롤바 */
  scrollbar-width: thin;
  scrollbar-color: #cdb4db rgba(205, 180, 219, 0.1);
}

/* Webkit 스크롤바 (Chrome, Safari, Edge) */
.memo-list::-webkit-scrollbar {
  width: 6px;
}

.memo-list::-webkit-scrollbar-track {
  background: rgba(205, 180, 219, 0.1);
  border-radius: 3px;
}

.memo-list::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #cdb4db, #ffc8dd);
  border-radius: 3px;
}

.memo-list::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #b799c7, #ffb3cc);
}

.memo-note {
  position: relative;
  padding: 1rem;
  border-radius: 4px 4px 20px 4px;
  min-height: 100px;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 3px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.2s;
}

.memo-note::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  border-radius: 4px 4px 0 0;
}

.memo-note:hover {
  transform: translateY(-3px) rotate(-0.5deg);
  box-shadow: 4px 6px 15px rgba(0, 0, 0, 0.12);
}

/* 메모 색상들 */
.memo-purple {
  background: linear-gradient(180deg, #f3eef7 0%, #ebe4f0 100%);
}
.memo-purple::before {
  background: #cdb4db;
}

.memo-pink {
  background: linear-gradient(180deg, #fff0f5 0%, #ffe8ef 100%);
}
.memo-pink::before {
  background: #ffc8dd;
}

.new-memo {
  background: white;
  border: 2px dashed rgba(205, 180, 219, 0.4);
  grid-column: 1 / -1;
  min-height: auto;
  margin-bottom: 16px;
}

.new-memo::before {
  display: none;
}

.memo-note-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.memo-note-content p {
  margin: 0;
  font-size: 0.9rem;
  line-height: 1.6;
  color: #444;
  white-space: pre-wrap;
  word-break: break-word;
}

.memo-note-content p.memo-text-clamped {
  display: -webkit-box;
  -webkit-line-clamp: 5;
  line-clamp: 5;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.expand-btn {
  display: inline-block;
  margin-top: 0.5rem;
  padding: 0.25rem 0.6rem;
  border: none;
  border-radius: 6px;
  background: rgba(205, 180, 219, 0.2);
  color: #8a6aa8;
  font-size: 0.75rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.expand-btn:hover {
  background: rgba(205, 180, 219, 0.4);
  color: #6a4a88;
}

.memo-note-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.75rem;
  padding-top: 0.5rem;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.memo-date {
  font-size: 0.75rem;
  color: #999;
}

.memo-note-btns {
  display: flex;
  gap: 0.25rem;
}

.memo-action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.2s;
}

.memo-action-btn.edit {
  color: #8a6aa8;
}

.memo-action-btn.edit:hover {
  background: #cdb4db;
  color: white;
}

.memo-action-btn.delete {
  color: #c97a8d;
}

.memo-action-btn.delete:hover {
  background: #ffc8dd;
  color: white;
}

.memo-note-edit {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.memo-textarea {
  width: 100%;
  min-height: 121px;
  padding: 0.75rem;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-family: inherit;
  resize: none;
  background: rgba(255, 255, 255, 0.8);
  transition: all 0.2s;
  /* Firefox 스크롤바 */
  scrollbar-width: thin;
  scrollbar-color: #cdb4db rgba(205, 180, 219, 0.1);
}

/* Webkit 스크롤바 (Chrome, Safari, Edge) */
.memo-textarea::-webkit-scrollbar {
  width: 8px;
}

.memo-textarea::-webkit-scrollbar-track {
  background: rgba(205, 180, 219, 0.1);
  border-radius: 4px;
}

.memo-textarea::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #cdb4db, #ffc8dd);
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: padding-box;
}

.memo-textarea::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #b799c7, #ffb3cc);
  background-clip: padding-box;
}

.memo-textarea:focus {
  outline: none;
  background: white;
  box-shadow: 0 0 0 2px rgba(205, 180, 219, 0.3);
}

.memo-note-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
  margin-top: 16px;
}

.memo-note-actions .save-btn,
.memo-note-actions .cancel-btn {
  padding: 0.5rem 1rem;
  font-size: 0.85rem;
}

.memo-empty {
  text-align: center;
  padding-top: 2rem;
  color: #aaa;
  font-style: italic;
}

.memo-actions {
  display: flex;
  gap: 0.75rem;
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
  background: linear-gradient(135deg, #cdb4db, #b799c7);
  color: white;
  box-shadow: 0 4px 12px rgba(205, 180, 219, 0.4);
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(205, 180, 219, 0.5);
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.cancel-btn:hover {
  background: #eee;
}

.dream-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.dream-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
}

.dream-pagination {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  margin: 0 0 1rem 0;
}

.page-btn {
  padding: 0.35rem 0.45rem;
  border: 1px solid rgba(205, 180, 219, 0.4);
  border-radius: 10px;
  background: white;
  color: #8a6aa8;
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-btn.icon svg {
  display: block;
}

.page-btn:hover:not(:disabled) {
  background: rgba(205, 180, 219, 0.22);
  color: #754f9b;
  transform: translateY(-1px);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: default;
}

.page-indicator {
  font-family: "Dongle", sans-serif;
  font-size: 1.5rem;
  font-weight: 700;
  color: #6a4a88;
  padding: 0rem 0.45rem;
  border-radius: 12px;
  background: rgba(205, 180, 219, 0.18);
  box-shadow: 0 4px 10px rgba(205, 180, 219, 0.15);
}

.lock-card {
  margin: 1.2rem auto 0;
  padding: 11.4rem 2.2rem;
  border-radius: 32px;
  border: 1px solid rgba(205, 180, 219, 0.32);
  background: radial-gradient(circle at 18% 18%, rgba(255, 200, 221, 0.22), transparent 40%), radial-gradient(circle at 82% 32%, rgba(162, 210, 255, 0.22), transparent 45%),
    linear-gradient(135deg, rgba(205, 180, 219, 0.2), rgba(255, 200, 221, 0.18), rgba(162, 210, 255, 0.18));
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1.6rem;
  box-shadow: 0 18px 40px rgba(205, 180, 219, 0.24);
  text-align: center;
}

.lock-text {
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
  align-items: center;
}

.lock-emoji {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, #cdb4db, #a2d2ff);
  display: grid;
  place-items: center;
  color: #fff;
}

.lock-caption {
  display: flex;
  flex-direction: column;
  gap: 1.45rem;
  align-items: center;
}

.lock-caption .pill {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.55rem 1rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.9);
  color: #7a5fa3;
  font-weight: 800;
  font-size: 0.92rem;
  box-shadow: 0 12px 24px rgba(205, 180, 219, 0.24);
}

.lock-caption .caption-main {
  font-weight: 800;
  font-size: 1.1rem;
  color: #6f4d95;
  background: rgba(255, 255, 255, 0.85);
  padding: 0.8rem 1.1rem;
  border-radius: 16px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.7), 0 10px 24px rgba(205, 180, 219, 0.18);
}

.lock-caption .caption-sub {
  font-size: 0.98rem;
  color: #8a6aa8;
  font-weight: 700;
}

/* 리포트 모달 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.report-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.28);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  z-index: 50;
}

.report-modal {
  background: #fff;
  width: min(880px, 95vw);
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 20px;
  box-shadow: 0 22px 48px rgba(0, 0, 0, 0.18);
  padding: 1.6rem;
  border: 1px solid rgba(205, 180, 219, 0.35);
}

.report-modal-header {
  display: flex;
  align-items: center;
  gap: 0.9rem;
  margin-bottom: 1.1rem;
}

.report-mailbox {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, #cdb4db, #a2d2ff);
  display: grid;
  place-items: center;
  color: #fff;
}

.report-title {
  font-family: "Dongle", sans-serif;
  font-weight: 700;
  font-size: 1.8rem;
  color: #4c2b7b;
  line-height: 1.05;
}

.modal-close-btn {
  margin-left: auto;
  border: none;
  background: none;
  cursor: pointer;
  color: #aaa;
  padding: 6px;
  border-radius: 10px;
}

.modal-close-btn:hover {
  background: #f6f3f9;
  color: #555;
}

.report-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 0.75rem;
  margin-bottom: 1.2rem;
}

.report-card {
  background: linear-gradient(135deg, rgba(205, 180, 219, 0.14), rgba(255, 200, 221, 0.14));
  border: 1px solid rgba(205, 180, 219, 0.28);
  border-radius: 14px;
  padding: 1rem;
  box-shadow: 0 8px 18px rgba(205, 180, 219, 0.18);
}

.report-label {
  font-weight: 700;
  color: #6a4a88;
  margin-bottom: 0.25rem;
}

.report-value {
  font-family: "Dongle", sans-serif;
  font-size: 2.4rem;
  font-weight: 700;
  color: #4c2b7b;
  line-height: 1.1;
}

.report-hint {
  color: #7f7f9b;
  font-size: 0.85rem;
}

.report-letter {
  margin-top: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 16px;
  background: #faf7ff;
  border: 1px solid rgba(205, 180, 219, 0.35);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6), 0 14px 30px rgba(205, 180, 219, 0.14);
  position: relative;
  overflow: hidden;
}

.report-letter::before {
  content: "";
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(to bottom, transparent, transparent 26px, rgba(205, 180, 219, 0.12) 27px, rgba(205, 180, 219, 0.12) 28px);
  pointer-events: none;
}

.letter-top {
  display: flex;
  gap: 1rem;
  align-items: center;
  position: relative;
  z-index: 1;
  margin-bottom: 1rem;
  justify-content: space-between;
}

.letter-stamp {
  padding: 0.55rem 0.75rem;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(205, 180, 219, 0.35), rgba(162, 210, 255, 0.35));
  color: #5a3d7a;
  font-family: "Dongle", sans-serif;
  font-weight: 700;
  font-size: 1.3rem;
  box-shadow: 0 6px 14px rgba(205, 180, 219, 0.26);
}

.letter-tofrom {
  display: flex;
  flex-direction: column;
  font-family: "Dongle", sans-serif;
  font-weight: 700;
  font-size: 1.35rem;
  color: #5c4c79;
}

.letter-line span {
  color: #a26fb8;
  font-weight: 800;
  margin-right: 0.4rem;
}

.letter-body {
  position: relative;
  z-index: 1;
  background: rgba(250, 247, 255, 0.9);
  border-radius: 12px;
  border: 1px dashed rgba(205, 180, 219, 0.35);
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
}

.letter-footer {
  margin-top: 0.9rem;
  text-align: right;
  font-family: "Dongle", sans-serif;
  font-weight: 700;
  font-size: 1.4rem;
  color: #7a5fa3;
  position: relative;
  z-index: 1;
}

.footer-icon {
  vertical-align: middle;
  color: #7a5fa3;
}

.letter-placeholder {
  min-height: 160px;
  padding: 1rem;
  background: rgba(252, 249, 255, 0.96);
  border-radius: 10px;
  color: #5c4c79;
  font-weight: 700;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.letter-content {
  min-height: 160px;
  padding: 1rem;
  white-space: pre-wrap;
  line-height: 1.6;
  color: #4b3b67;
}

.letter-error {
  min-height: 160px;
  padding: 1rem;
  background: rgba(255, 235, 230, 0.9);
  border-radius: 10px;
  color: #c0392b;
  font-weight: 700;
  line-height: 1.6;
}

.report-section {
  margin-bottom: 1.2rem;
  padding: 1rem;
  border: 1px solid rgba(205, 180, 219, 0.24);
  border-radius: 16px;
  background: #fbf9ff;
}

.report-section-head {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.8rem;
}

.report-section-head h4 {
  margin: 0;
  color: #4c2b7b;
}

.section-badge {
  padding: 0.25rem 0.55rem;
  background: rgba(205, 180, 219, 0.2);
  border-radius: 10px;
  color: #6a4a88;
  font-size: 0.85rem;
  font-weight: 700;
}

.timeline {
  display: grid;
  gap: 0.65rem;
}

.timeline-item {
  display: flex;
  gap: 0.6rem;
  align-items: center;
}

.timeline-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
}

.timeline-dot.start {
  background: linear-gradient(135deg, #ffc8dd, #ffb3cc);
}

.timeline-dot.end {
  background: linear-gradient(135deg, #a2d2ff, #8bb8e8);
}

.timeline-title {
  font-weight: 700;
  color: #5a3d7a;
}

.timeline-text {
  color: #666;
  font-size: 0.9rem;
}

.report-empty {
  color: #9a9aab;
  font-style: italic;
}

.report-memo-list {
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
}

.report-memo {
  background: white;
  border: 1px solid rgba(205, 180, 219, 0.24);
  border-radius: 12px;
  padding: 0.9rem;
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.05);
}

.report-memo p {
  margin: 0.35rem 0 0 0;
  color: #444;
  white-space: pre-wrap;
  word-break: break-word;
}

.more-hint {
  color: #7c689f;
  font-weight: 700;
}

.report-modal::-webkit-scrollbar {
  width: 8px;
}

.report-modal::-webkit-scrollbar-thumb {
  background: rgba(205, 180, 219, 0.6);
  border-radius: 4px;
}

.dream-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: white;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid rgba(205, 180, 219, 0.15);
}

.dream-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 16px rgba(205, 180, 219, 0.2);
  border-color: rgba(205, 180, 219, 0.3);
}

.dream-item svg {
  color: #ccc;
  transition: color 0.2s;
}

.dream-item:hover svg {
  color: #b799c7;
}

.dream-date-badge {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.5rem;
  color: white;
  flex-shrink: 0;
  font-family: "Dongle", sans-serif;
}

.dream-date-badge.color-purple {
  background: linear-gradient(135deg, #cdb4db, #b799c7);
}

.dream-date-badge.color-pink {
  background: linear-gradient(135deg, #ffc8dd, #ffb3cc);
}

.dream-date-badge.color-blue {
  background: linear-gradient(135deg, #a2d2ff, #8bb8e8);
}

.dream-info {
  flex: 1;
  min-width: 0;
}

.dream-info h4 {
  margin: 0 0 0.25rem 0;
  font-size: 1rem;
  color: #333;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dream-info p {
  margin: 0;
  font-size: 0.85rem;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.empty-state {
  text-align: center;
  padding: 2.5rem 1rem;
  color: #aaa;
}

.empty-icon {
  display: flex;
  justify-content: center;
  margin-bottom: 0.75rem;
}

.empty-icon svg {
  width: 48px;
  height: 48px;
  stroke: #cdb4db;
}

.empty-state p {
  margin: 0;
  font-size: 0.95rem;
}

/* 반응형 */
@media (max-width: 1040px) {
  .stat-content {
    flex-direction: column;
  }
}

@media (max-width: 900px) {
  .monthly-analysis-content {
    grid-template-columns: 1fr;
  }

  .right-column {
    padding-left: 0;
    border-left: none;
  }

  .right-column .memo-section {
    max-height: none;
  }

  .stat-content {
    flex-direction: row;
  }
}

@media (max-width: 768px) {
  .monthly-analysis-card {
    border-radius: 24px;
    padding: 1.25rem 1.5rem 1.5rem;
  }

  .page-title {
    font-size: 1.8rem;
    padding: 0.4rem 1rem;
  }

  .title-badge {
    display: none;
  }

  .current-month {
    font-size: 1.6rem;
    min-width: 140px;
  }

  .stats-grid {
    flex-direction: column;
    gap: 0.6rem;
  }

  .stat-card {
    padding: 0.9rem 1rem;
    gap: 2rem;
  }

  .stat-icon-wrap {
    width: 42px;
    height: 42px;
  }

  .stat-icon-wrap svg {
    width: 22px;
    height: 22px;
  }

  .stat-value {
    font-size: 2.5rem;
  }

  .stat-label {
    font-size: 0.9rem;
  }

  .section-card {
    padding: 1.25rem;
  }

  .section-title {
    font-size: 1.5rem;
  }
}
</style>

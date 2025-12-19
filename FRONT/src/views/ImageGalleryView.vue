<template>
  <div class="gallery-card">
    <div class="card-header">
      <button @click="handleBack" class="icon-btn">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7" />
        </svg>
      </button>
      <h2 class="page-title">
        이미지 갤러리
        <span class="title-badge">Gallery</span>
      </h2>
      <div class="header-actions">
        <button @click="handleClose" class="icon-btn" aria-label="닫기">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>

    <div class="gallery-content">
      <!-- 필터 및 검색 -->
      <div class="filter-section">
        <div class="search-box">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"></circle>
            <path d="m21 21-4.35-4.35"></path>
          </svg>
          <input v-model="searchQuery" type="text" placeholder="제목이나 태그로 검색..." class="search-input" />
        </div>

        <div class="filter-buttons">
          <button v-for="filter in filters" :key="filter.id" :class="['filter-btn', { active: activeFilter === filter.id }]" @click="activeFilter = filter.id">
            <span class="filter-icon" v-html="filter.icon"></span>
            <span>{{ filter.label }}</span>
          </button>
        </div>
      </div>

      <!-- 갤러리 통계 -->
      <div class="gallery-stats">
        <div class="stat-item">
          <span class="stat-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
              <circle cx="8.5" cy="8.5" r="1.5"></circle>
              <polyline points="21 15 16 10 5 21"></polyline>
            </svg>
          </span>
          <span class="stat-text">
            전체
            <strong>{{ filteredImages.length }}</strong>
            개
          </span>
        </div>
        <div class="stat-item">
          <span class="stat-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="1">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
            </svg>
          </span>
          <span class="stat-text">
            찜
            <strong>{{ totalLikes }}</strong>
            개
          </span>
        </div>
      </div>

      <!-- 이미지 그리드 -->
      <div v-if="filteredImages.length > 0" class="gallery-grid">
        <div v-for="image in filteredImages" :key="image.id" class="gallery-item" @click="openImageDetail(image)">
          <!-- 실제 이미지가 있는 경우 -->
          <div v-if="image.imageSrc" class="image-container real-image">
            <img :src="resolveImageSrc(image.imageSrc)" :alt="image.caption" class="gallery-image" />
            <div class="image-hover-overlay">
              <span class="hover-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="11" cy="11" r="8"></circle>
                  <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
              </span>
            </div>
          </div>
          <!-- 기존 gradient/emoji 표시 (이전 형식 호환) -->
          <div v-else class="image-container" :style="{ background: image.gradient }">
            <div class="image-overlay">
              <span class="image-emoji">{{ image.emoji }}</span>
            </div>
          </div>
          <div class="image-info">
            <h4 class="image-title">{{ image.title || image.caption }}</h4>
            <div class="image-meta">
              <span class="meta-item date-badge">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                  <line x1="16" y1="2" x2="16" y2="6"></line>
                  <line x1="8" y1="2" x2="8" y2="6"></line>
                  <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
                {{ formatDreamDate(image.dreamDate) || formatDate(image.createdAt) }}
              </span>
            </div>
            <div class="image-actions">
              <button @click.stop="toggleLike(image)" :class="['action-btn', { liked: image.liked }]" :aria-label="image.liked ? '찜 해제' : '찜하기'">
                <svg width="16" height="16" viewBox="0 0 24 24" :fill="image.liked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                  <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                </svg>
              </button>
              <button @click.stop="deleteImage(image)" class="action-btn delete">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="3 6 5 6 21 6"></polyline>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 빈 상태 -->
      <div v-else class="empty-gallery">
        <span class="empty-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M12 19l7-7 3 3-7 7-3-3z"></path>
            <path d="M18 13l-1.5-7.5L2 2l3.5 14.5L13 18l5-5z"></path>
            <path d="M2 2l7.586 7.586"></path>
            <circle cx="11" cy="11" r="2"></circle>
          </svg>
        </span>
        <h3>아직 생성된 이미지가 없습니다</h3>
        <p>캘린더에서 꿈을 기록하고 분석하면 생성된 이미지가 여기 보입니다.</p>
      </div>
    </div>

    <!-- 이미지 상세 모달 -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div v-if="selectedImage" class="modal-overlay" @click="closeImageDetail">
          <!-- 이전 이미지 버튼 -->
          <button v-if="hasPrevImage" @click.stop="goToPrevImage" class="nav-btn nav-prev">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <polyline points="15 18 9 12 15 6"></polyline>
            </svg>
          </button>

          <div class="modal-content" @click.stop>
            <button @click="closeImageDetail" class="modal-close">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>

            <div class="modal-layout">
              <!-- 왼쪽: 액자에 담긴 이미지 -->
              <div class="modal-image-section">
                <div class="frame-container">
                  <div class="frame-outer">
                    <div class="frame-inner">
                      <div v-if="selectedImage.imageSrc" class="framed-image">
                        <img :src="resolveImageSrc(selectedImage.imageSrc)" :alt="selectedImage.caption" />
                      </div>
                      <div v-else class="framed-placeholder" :style="{ background: selectedImage.gradient }">
                        <span class="placeholder-emoji">{{ selectedImage.emoji }}</span>
                      </div>
                    </div>
                  </div>
                  <!-- 액자 장식 -->
                  <div class="frame-shadow"></div>
                </div>
              </div>

              <!-- 오른쪽: 정보 섹션 -->
              <div class="modal-info-section">
                <!-- 제목과 날짜 -->
                <div class="modal-header-info">
                  <h2>{{ selectedImage.title || selectedImage.caption }}</h2>
                  <div class="modal-meta">
                    <span class="meta-badge date">
                      <span class="badge-icon">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                          <line x1="16" y1="2" x2="16" y2="6"></line>
                          <line x1="8" y1="2" x2="8" y2="6"></line>
                          <line x1="3" y1="10" x2="21" y2="10"></line>
                        </svg>
                      </span>
                      {{ formatDreamDate(selectedImage.dreamDate) || formatDate(selectedImage.createdAt) }}
                    </span>
                  </div>
                </div>

                <!-- 스크롤 가능한 컨텐츠 영역 -->
                <div class="modal-scrollable">
                  <!-- 꿈 일기 본문 -->
                  <div v-if="selectedImage.content" class="modal-section dream-content-section">
                    <h3 class="section-title">
                      <span class="section-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                          <polyline points="14 2 14 8 20 8"></polyline>
                          <line x1="16" y1="13" x2="8" y2="13"></line>
                          <line x1="16" y1="17" x2="8" y2="17"></line>
                          <polyline points="10 9 9 9 8 9"></polyline>
                        </svg>
                      </span>
                      꿈 일기 내용
                    </h3>
                    <p class="dream-content-text">{{ selectedImage.content }}</p>
                  </div>

                  <!-- 꿈 해석 -->
                  <div v-if="selectedImage.interpretation" class="modal-section interpretation-section">
                    <h3 class="section-title">
                      <span class="section-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <circle cx="12" cy="12" r="10"></circle>
                          <path d="M12 16v-4"></path>
                          <path d="M12 8h.01"></path>
                        </svg>
                      </span>
                      꿈 해석
                    </h3>
                    <p class="interpretation-text">{{ selectedImage.interpretation }}</p>
                  </div>

                  <!-- 오늘의 운세 요약 -->
                  <div v-if="selectedImage.fortuneSummary" class="modal-section fortune-section">
                    <h3 class="section-title">
                      <span class="section-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                        </svg>
                      </span>
                      오늘의 운세
                    </h3>
                    <p class="fortune-text">{{ selectedImage.fortuneSummary }}</p>
                  </div>

                  <!-- 행운 정보 -->
                  <div v-if="selectedImage.luckyColor || selectedImage.luckyItem" class="modal-section lucky-section">
                    <div class="lucky-items">
                      <div v-if="selectedImage.luckyColor" class="lucky-item">
                        <span class="lucky-label">행운의 색상</span>
                        <span class="lucky-value color-value">
                          <span class="color-dot" :style="{ background: getLuckyColorHex(selectedImage.luckyColor.name) }"></span>
                          {{ selectedImage.luckyColor.name }}
                        </span>
                      </div>
                      <div v-if="selectedImage.luckyItem" class="lucky-item">
                        <span class="lucky-label">행운의 아이템</span>
                        <span class="lucky-value">{{ selectedImage.luckyItem.name }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 액션 버튼 -->
                <div class="modal-actions">
                  <button @click="openOriginalImage(selectedImage)" class="modal-action-btn ghost">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="15 3 21 3 21 9"></polyline>
                      <polyline points="9 21 3 21 3 15"></polyline>
                      <line x1="21" y1="3" x2="14" y2="10"></line>
                      <line x1="3" y1="21" x2="10" y2="14"></line>
                    </svg>
                    원본 보기
                  </button>
                  <button @click="downloadImage(selectedImage)" class="modal-action-btn primary">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                      <polyline points="7 10 12 15 17 10"></polyline>
                      <line x1="12" y1="15" x2="12" y2="3"></line>
                    </svg>
                    다운로드
                  </button>
                  <button @click="deleteImage(selectedImage)" class="modal-action-btn danger">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="3 6 5 6 21 6"></polyline>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    </svg>
                    삭제
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 다음 이미지 버튼 -->
          <button v-if="hasNextImage" @click.stop="goToNextImage" class="nav-btn nav-next">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <polyline points="9 18 15 12 9 6"></polyline>
            </svg>
          </button>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useGalleryStore } from "../stores/galleryStore";
import { imageService } from "../services/imageService";
import { dreamService } from "../services/dreamService";

const router = useRouter();
const galleryStore = useGalleryStore();
const { galleryImages } = storeToRefs(galleryStore);

const searchQuery = ref("");
const activeFilter = ref("all");
const selectedImage = ref(null);
const syncing = ref(false);

const filters = [
  { 
    id: "all", 
    label: "전체", 
    icon: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>`
  },
  { 
    id: "recent", 
    label: "최근", 
    icon: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>`
  },
  { 
    id: "liked", 
    label: "찜", 
    icon: `<svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="1"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>`
  },
];

// 컴포넌트 마운트 시 갤러리 데이터 새로고침
onMounted(async () => {
  galleryStore.hydrateFromLocalStorage();
  await syncFromServer();
  // 키보드 네비게이션 이벤트 추가
  window.addEventListener("keydown", handleKeydown);
});

// 언마운트 시 이벤트 제거
onUnmounted(() => {
  window.removeEventListener("keydown", handleKeydown);
});

// 필터링된 이미지
const filteredImages = computed(() => {
  let result = galleryImages.value;

  // 검색 필터
  if (searchQuery.value) {
    result = result.filter((img) => img.caption.toLowerCase().includes(searchQuery.value.toLowerCase()));
  }

  // 카테고리 필터
  if (activeFilter.value === "recent") {
    result = [...result].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  } else if (activeFilter.value === "liked") {
    result = result.filter((img) => img.liked);
  }

  return result;
});

const totalLikes = computed(() => {
  return galleryImages.value.reduce((sum, img) => sum + (img.likes || 0), 0);
});

// 이미지 경로를 실제 접근 가능한 URL로 정규화
function resolveImageSrc(src) {
  if (!src) return "";
  if (src.startsWith("http://") || src.startsWith("https://")) return src;
  if (src.startsWith("/")) return src;
  // 슬래시가 없는 상대경로로 온 경우 /uploads/... 형태로 접근할 수 있게 보정
  return `/${src}`;
}

function handleBack() {
  router.push({ name: "calendar" });
}

function handleClose() {
  router.push({ name: "calendar" });
}

function formatDate(dateString) {
  if (!dateString) return "";
  const date = new Date(dateString);
  return `${date.getMonth() + 1}월 ${date.getDate()}일`;
}

function formatDreamDate(dateKey) {
  if (!dateKey) return "";
  const [year, month, day] = dateKey.split("-");
  return `${month}월 ${day}일`;
}

// 색상 이름을 HEX 코드로 변환
function getLuckyColorHex(colorName) {
  const colorMap = {
    회색: "#9E9E9E",
    갈색: "#8D6E63",
    주황색: "#FF9800",
    노란색: "#FFEB3B",
    초록색: "#4CAF50",
    파란색: "#2196F3",
    보라색: "#9C27B0",
    분홍색: "#E91E63",
    빨간색: "#F44336",
    하늘색: "#03A9F4",
    청록색: "#00BCD4",
    금색: "#FFD700",
    은색: "#C0C0C0",
    검정색: "#424242",
    흰색: "#FFFFFF",
  };
  return colorMap[colorName] || "#CDB4DB";
}

function toggleLike(image) {
  galleryStore.toggleImageLike(image.id);
}


async function deleteImage(image) {
  if (!confirm("정말 이 이미지를 삭제하시겠습니까?")) {
    return;
  }

  try {
    // 서버에 저장된 이미지인 경우 백엔드에서도 삭제 (절대/상대 경로 모두 허용)
    if (image.imageSrc && image.imageSrc.includes("/uploads/images/")) {
      try {
        await imageService.deleteImage(image.imageSrc);
        console.log("✅ 서버 이미지 삭제 완료");
      } catch (err) {
        console.warn("⚠️ 서버 이미지 삭제 실패 (로컬만 삭제):", err.message);
      }
    }

    // 갤러리에서 제거
    galleryStore.removeFromGallery(image.id);

    // 모달이 열려있으면 닫기
    if (selectedImage.value?.id === image.id) {
      selectedImage.value = null;
    }

    console.log("🗑️ 이미지 삭제 완료");
  } catch (error) {
    console.error("삭제 실패:", error);
    alert("삭제 중 오류가 발생했습니다.");
  }
}

function downloadImage(image) {
  if (!image.imageSrc) {
    alert("다운로드할 수 있는 이미지가 없습니다.");
    return;
  }

  try {
    const link = document.createElement("a");
    link.href = image.imageSrc;

    // 파일명 생성
    const timestamp = new Date().toISOString().slice(0, 10);
    const ext = image.mimeType?.split("/")[1] || "png";
    link.download = `dream_${timestamp}_${image.style || "image"}.${ext}`;

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    console.error("다운로드 실패:", error);
    alert("다운로드 중 오류가 발생했습니다.");
  }
}

function openImageDetail(image) {
  selectedImage.value = image;
  document.body.style.overflow = "hidden";
}

function closeImageDetail() {
  selectedImage.value = null;
  document.body.style.overflow = "";
}

// 현재 이미지 인덱스
const currentImageIndex = computed(() => {
  if (!selectedImage.value) return -1;
  return filteredImages.value.findIndex((img) => img.id === selectedImage.value.id);
});

// 이전/다음 이미지 존재 여부
const hasPrevImage = computed(() => currentImageIndex.value > 0);
const hasNextImage = computed(() => currentImageIndex.value < filteredImages.value.length - 1);

// 이전 이미지로 이동
function goToPrevImage() {
  if (hasPrevImage.value) {
    selectedImage.value = filteredImages.value[currentImageIndex.value - 1];
  }
}

// 다음 이미지로 이동
function goToNextImage() {
  if (hasNextImage.value) {
    selectedImage.value = filteredImages.value[currentImageIndex.value + 1];
  }
}

// 키보드 네비게이션
function handleKeydown(e) {
  if (!selectedImage.value) return;
  if (e.key === "ArrowLeft") goToPrevImage();
  if (e.key === "ArrowRight") goToNextImage();
  if (e.key === "Escape") closeImageDetail();
}

function openOriginalImage(image) {
  if (!image?.imageSrc) return;
  window.open(image.imageSrc, "_blank");
}

// 서버에 저장된 이미지가 있는 꿈을 한 번에 불러오기 (갤러리용)
async function syncFromServer() {
  if (syncing.value) return;
  syncing.value = true;

  try {
    // 백엔드에서 이미지가 있는 모든 꿈을 한 번에 조회
    const response = await dreamService.getDreamsWithImages();

    if (!response?.items) {
      console.log("갤러리에 표시할 이미지가 없습니다.");
      // 서버에 이미지가 없으면 로컬 갤러리도 비우기
      galleryStore.resetGallery();
      return;
    }

    // 서버에서 받은 꿈 ID 목록 (이미지가 있는 꿈만)
    const serverDreamIds = new Set(response.items.map((item) => item.dreamId));

    // 로컬 갤러리에서 서버에 없는 항목 제거 (이미지가 삭제된 경우)
    const localDreamIds = galleryImages.value.map((img) => img.dreamId).filter(Boolean);
    for (const localDreamId of localDreamIds) {
      if (!serverDreamIds.has(localDreamId)) {
        // 서버에 없는 항목은 로컬에서도 삭제
        const imageToRemove = galleryImages.value.find((img) => img.dreamId === localDreamId);
        if (imageToRemove) {
          galleryStore.removeFromGallery(imageToRemove.id);
          console.log(`🗑️ 서버에서 삭제된 항목 제거: dreamId=${localDreamId}`);
        }
      }
    }

    // 서버에서 받은 꿈들을 갤러리에 추가 또는 업데이트
    for (const item of response.items) {
      // 이미지가 있는 경우만 갤러리에 추가/업데이트
      if (item.imageUrl) {
        galleryStore.addToGallery({
          id: item.dreamId, // dreamId를 일관된 식별자로 사용하여 중복 방지
          dreamId: item.dreamId,
          dreamDate: item.dreamDate,
          title: item.title,
          content: item.content,
          interpretation: item.dreamInterpretation,
          fortuneSummary: item.todayFortuneSummary,
          luckyColor: item.luckyColor,
          luckyItem: item.luckyItem,
          style: "꿈 이미지",
          caption: item.title || "꿈 이미지",
          imageSrc: item.imageUrl,
          mimeType: "image/png",
          createdAt: item.createdDate || new Date().toISOString(),
          savedAt: new Date().toISOString(),
        });
      }
    }

    console.log(`✅ 갤러리 동기화 완료: ${response.items.length}개의 이미지를 불러왔습니다.`);
  } catch (error) {
    console.error("갤러리 동기화 실패:", error);
  } finally {
    syncing.value = false;
  }
}
</script>

<style scoped>
/* ===== 몽글몽글 갤러리 스타일 ===== */

.gallery-card {
  background: white;
  border-radius: 40px;
  padding: 2rem;
  width: 100%;
  max-width: 1200px;
  box-shadow: 0 20px 60px rgba(100, 100, 200, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

/* .icon-btn은 global.css에서 정의됨 */

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
  background: var(--gradient-title-badge);
  -webkit-text-fill-color: #4c2b7b;
  line-height: 1.2;
}

.title-badge {
  font-size: 0.7rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  padding: 0.3rem 0.8rem;
  border-radius: 999px;
  background: white;
  color: var(--color-purple-dark);
  font-weight: 600;
}

.header-actions {
  display: inline-flex;
  gap: 0.5rem;
  align-items: center;
}

.gallery-content {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* ===== 필터 섹션 ===== */
.filter-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  background: var(--color-purple-light);
  border: 2px solid var(--border-purple);
  border-radius: 20px;
  transition: all 0.3s;
}

.search-box:focus-within {
  border-color: var(--color-purple);
  background: white;
  box-shadow: 0 0 0 4px var(--color-purple-light);
}

.search-box svg {
  color: var(--color-text-muted);
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 1rem;
  outline: none;
  color: #333;
}

.search-input::placeholder {
  color: #bbb;
}

.filter-buttons {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.7rem 1.25rem;
  border: 2px solid var(--border-purple);
  background: white;
  border-radius: 999px;
  font-weight: 700;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn:hover {
  border-color: var(--color-purple);
  background: var(--color-purple-light);
  transform: translateY(-2px);
}

.filter-btn.active {
  border-color: transparent;
  background: var(--gradient-purple-blue);
  color: white;
  box-shadow: 0 4px 15px var(--shadow-purple);
}

.filter-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.filter-icon svg {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

/* ===== 통계 & 뷰 모드 ===== */
.gallery-stats {
  display: flex;
  gap: 2rem;
  padding: 0.5rem 1.25rem;
  background: var(--gradient-bg-light);
  border-radius: 16px;
  border: 1px solid var(--border-purple);
  font-family: "Dongle", sans-serif;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.6rem;
  color: var(--color-text-secondary);
  font-weight: 600;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: var(--color-purple);
}

.stat-icon svg {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.stat-item strong {
  color: var(--color-text-primary);
}

/* ===== 갤러리 그리드 ===== */
.gallery-grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
}

.gallery-item {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  border: 1px solid var(--border-purple);
  box-shadow: 0 8px 24px var(--shadow-purple);
  transition: all 0.3s;
  cursor: pointer;
}

.gallery-item:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px var(--shadow-purple-md);
  border-color: var(--color-purple);
}

.image-container {
  width: 100%;
  aspect-ratio: 1 / 1;
  position: relative;
  overflow: hidden;
}

.image-container.real-image {
  background: linear-gradient(135deg, var(--color-purple-15), var(--color-blue-15));
}

.gallery-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: linear-gradient(135deg, #454278 0%, #7C78B8 50%, #C4B6DC 100%);
  transition: transform 0.3s ease;
}

.gallery-item:hover .gallery-image {
  transform: scale(1.03);
}

.image-hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(205, 180, 219, 0.4), rgba(162, 210, 255, 0.4));
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.gallery-item:hover .image-hover-overlay {
  opacity: 1;
}

.hover-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3));
}

.hover-icon svg {
  width: 40px;
  height: 40px;
}

.image-overlay {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-emoji {
  font-size: 4rem;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
}

.image-info {
  padding: 1.25rem;
}

.image-title {
  margin: 0 0 0.6rem 0;
  font-size: 1.05rem;
  color: var(--color-text-primary);
  font-weight: 700;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.image-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
  margin-bottom: 0.85rem;
  font-size: 0.8rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.meta-item.date-badge {
  color: var(--color-purple);
  font-weight: 600;
  font-family: 'Dongle';
  font-size: 1rem;
}


.image-actions {
  display: flex;
  gap: 0.4rem;
  justify-content:flex-end;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  padding: 0.5rem;
  border: 1px solid var(--border-purple);
  background: white;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn svg {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.action-btn:hover {
  border-color: var(--color-purple);
  background: var(--color-purple-light);
  color: var(--color-purple);
}

.action-btn.liked {
  border-color: var(--color-pink);
  color: var(--color-pink-dark);
  background: rgba(255, 200, 221, 0.15);
}

.action-btn.delete:hover {
  border-color: #ff8a80;
  color: #ff5252;
  background: rgba(255, 138, 128, 0.1);
}

/* ===== 빈 상태 ===== */
.empty-gallery {
  text-align: center;
  padding: 4rem 2rem;
  color: var(--color-text-muted);
}

.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.5rem;
  color: var(--color-purple);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.empty-gallery h3 {
  color: var(--color-text-primary);
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
}

.empty-gallery p {
  max-width: 300px;
  margin: 0 auto;
  line-height: 1.6;
}

/* ===== 모달 - 몽글몽글 스타일 ===== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(69, 66, 120, 0.85), rgba(124, 120, 184, 0.85));
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 1.5rem;
  gap: 1rem;
}

/* 네비게이션 버튼 */
.nav-btn {
  position: relative;
  z-index: 10;
  width: 52px;
  height: 52px;
  border: none;
  border-radius: 50%;
  background: white;
  color: var(--color-purple);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  box-shadow: 0 4px 20px var(--shadow-purple);
  flex-shrink: 0;
}

.nav-btn:hover {
  background: var(--color-purple);
  color: white;
  transform: scale(1.1);
  box-shadow: 0 6px 25px var(--shadow-purple-md);
}

.nav-btn:active {
  transform: scale(0.95);
}

.modal-content {
  background: transparent;
  border-radius: 0;
  max-width: 1000px;
  width: 100%;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
  flex-shrink: 1;
}

.modal-close {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  width: 40px;
  height: 40px;
  border: none;
  background: white;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-muted);
  transition: all 0.3s;
  z-index: 10;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-close:hover {
  background: var(--color-pink);
  color: white;
  transform: scale(1.1);
}

/* 모달 레이아웃 */
.modal-layout {
  display: flex;
  flex: 1;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
  gap: 0;
  border-radius: 32px;
  box-shadow: 0 25px 80px rgba(100, 100, 200, 0.4);
}

/* 왼쪽: 이미지 섹션 - 밤하늘 테마 */
.modal-image-section {
  flex: 0 0 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2.5rem 2rem;
  position: relative;
  min-width: 0;
  background: linear-gradient(180deg, #454278 0%, #7C78B8 60%, #C4B6DC 100%);
  border-radius: 32px 0 0 32px;
  overflow: hidden;
}

/* 별 효과 */
.modal-image-section::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(2px 2px at 20px 30px, white, transparent),
    radial-gradient(2px 2px at 40px 70px, rgba(255,255,255,0.8), transparent),
    radial-gradient(1px 1px at 90px 40px, white, transparent),
    radial-gradient(2px 2px at 130px 80px, rgba(255,255,255,0.6), transparent),
    radial-gradient(1px 1px at 160px 30px, white, transparent),
    radial-gradient(2px 2px at 200px 60px, rgba(255,255,255,0.7), transparent),
    radial-gradient(1px 1px at 50px 120px, white, transparent),
    radial-gradient(2px 2px at 100px 150px, rgba(255,255,255,0.5), transparent),
    radial-gradient(1px 1px at 180px 140px, white, transparent);
  pointer-events: none;
  animation: twinkle-stars 4s ease-in-out infinite alternate;
}

@keyframes twinkle-stars {
  0% { opacity: 0.5; }
  100% { opacity: 1; }
}

/* 액자 컨테이너 - 부드러운 스타일 */
.frame-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 380px;
}

/* 부드러운 파스텔 액자 */
.frame-outer {
  background: linear-gradient(145deg, rgba(255,255,255,0.9), rgba(255,255,255,0.7));
  padding: 12px;
  border-radius: 20px;
  box-shadow: 
    0 20px 50px rgba(0, 0, 0, 0.2),
    0 8px 20px rgba(0, 0, 0, 0.1),
    inset 0 1px 2px rgba(255, 255, 255, 0.8);
}

/* 액자 내부 그라데이션 테두리 */
.frame-inner {
  background: var(--gradient-primary);
  padding: 4px;
  border-radius: 16px;
}

.framed-image {
  width: 100%;
  aspect-ratio: 1 / 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 14px;
  overflow: hidden;
}

.framed-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.framed-placeholder {
  width: 100%;
  aspect-ratio: 1 / 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--color-purple-15), var(--color-pink-15));
}

.placeholder-emoji {
  font-size: 5rem;
  filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.15));
}

/* 액자 그림자 제거 (새 스타일에서 불필요) */
.frame-shadow {
  display: none;
}


/* 오른쪽: 정보 섹션 */
.modal-info-section {
  flex: 0 0 50%;
  display: flex;
  flex-direction: column;
  padding: 2rem;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
  background: white;
  border-radius: 0 32px 32px 0;
}

.modal-header-info {
  padding-bottom: 1.25rem;
  border-bottom: 2px dashed var(--border-purple);
  margin-bottom: 1.25rem;
  flex-shrink: 0;
}

.modal-header-info h2 {
  margin: 0 0 0.85rem 0;
  font-family: "Dongle", sans-serif;
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1.2;
  word-break: keep-all;
}

.modal-meta {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.meta-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.45rem 0.95rem;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 700;
}

.meta-badge.date {
  background: var(--gradient-bg-15);
  color: var(--color-text-secondary);
  border: 1px solid var(--border-purple);
}

.badge-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-icon svg {
  width: 14px;
  height: 14px;
}

/* 스크롤 영역 */
.modal-scrollable {
  flex: 1;
  overflow-y: auto;
  padding-right: 0.5rem;
  margin-bottom: 1rem;
  min-height: 0;
}

.modal-scrollable::-webkit-scrollbar {
  width: 5px;
}

.modal-scrollable::-webkit-scrollbar-track {
  background: transparent;
}

.modal-scrollable::-webkit-scrollbar-thumb {
  background: var(--color-purple-40);
  border-radius: 5px;
}

.modal-scrollable::-webkit-scrollbar-thumb:hover {
  background: var(--color-purple-60);
}

/* 모달 섹션 */
.modal-section {
  margin-bottom: 0.85rem;
  padding: 1rem 1.1rem;
  border-radius: 16px;
  transition: transform 0.2s;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.95rem;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 0.6rem 0;
}

.section-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: inherit;
}

.section-icon svg {
  width: 16px;
  height: 16px;
}

/* 꿈 일기 내용 섹션 */
.dream-content-section {
  background: linear-gradient(135deg, var(--color-purple-15), var(--color-pink-15));
  border-left: 3px solid var(--color-purple);
}

.dream-content-text {
  margin: 0;
  color: #555;
  line-height: 1.75;
  font-size: 0.9rem;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 꿈 해석 섹션 */
.interpretation-section {
  background: linear-gradient(135deg, var(--color-blue-15), var(--color-purple-15));
  border-left: 3px solid var(--color-blue);
}

.interpretation-text {
  margin: 0;
  color: #4a6a8a;
  line-height: 1.75;
  font-size: 0.9rem;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 오늘의 운세 섹션 */
.fortune-section {
  background: linear-gradient(135deg, var(--color-pink-15), rgba(255, 224, 130, 0.15));
  border-left: 3px solid var(--color-pink);
}

.fortune-text {
  margin: 0;
  color: #8a5a6a;
  line-height: 1.75;
  font-size: 0.9rem;
  word-break: break-word;
}

/* 행운 정보 섹션 */
.lucky-section {
  background: var(--gradient-bg-15);
  border-left: 3px solid var(--color-blue);
  padding: 0.85rem 1.1rem;
}

.lucky-items {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.lucky-item {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.lucky-label {
  font-size: 0.7rem;
  color: var(--color-text-muted);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.lucky-value {
  font-size: 0.95rem;
  font-weight: 700;
  color: var(--color-text-primary);
}

.lucky-value.color-value {
  display: flex;
  align-items: center;
  gap: 0.4rem;
}

.color-dot {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 액션 버튼 */
.modal-actions {
  display: flex;
  gap: 0.75rem;
  padding-top: 1rem;
  border-top: 2px dashed var(--border-purple);
  flex-shrink: 0;
}

.modal-action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  padding: 0.85rem 1rem;
  flex: 1;
  border: none;
  border-radius: 14px;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
}

.modal-action-btn.ghost {
  background: var(--color-purple-light);
  color: var(--color-text-secondary);
  border: 1px solid var(--border-purple);
}

.modal-action-btn.ghost:hover {
  background: var(--color-purple-20);
  border-color: var(--color-purple);
}

.modal-action-btn.primary {
  background: var(--gradient-primary);
  color: white;
  box-shadow: 0 4px 15px var(--shadow-purple);
}

.modal-action-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px var(--shadow-purple-md);
}

.modal-action-btn.danger {
  background: linear-gradient(135deg, #ff8a80, #ff5252);
  color: white;
  box-shadow: 0 4px 15px rgba(255, 138, 128, 0.3);
}

.modal-action-btn.danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 138, 128, 0.4);
}

/* 모달 애니메이션 */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: all 0.35s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-from .modal-content,
.modal-fade-leave-to .modal-content {
  transform: scale(0.95) translateY(20px);
}

.modal-fade-enter-active .modal-content,
.modal-fade-leave-active .modal-content {
  transition: transform 0.35s ease;
}

/* ===== 반응형 - 태블릿 ===== */
@media (max-width: 950px) {
  .modal-overlay {
    padding: 1rem;
    flex-direction: row;
  }

  .nav-btn {
    width: 46px;
    height: 46px;
  }

  .modal-layout {
    flex-direction: column;
    overflow-y: auto;
    border-radius: 28px;
  }

  .modal-image-section {
    flex: none;
    padding: 2rem 1.5rem;
    border-radius: 28px 28px 0 0;
  }

  .frame-container {
    max-width: 280px;
  }

  .modal-info-section {
    flex: none;
    padding: 1.5rem;
    max-height: none;
    overflow: visible;
    border-radius: 0 0 28px 28px;
  }

  .modal-scrollable {
    overflow: visible;
    max-height: none;
  }

  .modal-header-info h2 {
    font-size: 1.75rem;
  }
}

/* ===== 반응형 - 모바일 ===== */
@media (max-width: 600px) {
  .modal-overlay {
    padding: 0.75rem;
    flex-direction: row;
    gap: 0.5rem;
  }

  .nav-btn {
    width: 40px;
    height: 40px;
  }

  .nav-btn svg {
    width: 20px;
    height: 20px;
  }

  .modal-content {
    max-height: 90vh;
  }

  .modal-close {
    top: 0.5rem;
    right: 0.5rem;
    width: 36px;
    height: 36px;
  }

  .modal-layout {
    flex-direction: column;
    overflow-y: auto;
    max-height: calc(90vh - 20px);
    border-radius: 24px;
  }

  .modal-image-section {
    padding: 1.5rem 1rem;
    flex-shrink: 0;
    border-radius: 24px 24px 0 0;
  }

  .frame-container {
    max-width: 220px;
  }

  .frame-outer {
    padding: 8px;
    border-radius: 16px;
  }

  .frame-inner {
    padding: 3px;
    border-radius: 14px;
  }

  .framed-image {
    border-radius: 12px;
  }

  .placeholder-emoji {
    font-size: 3.5rem;
  }

  .modal-info-section {
    padding: 1.25rem;
    border-radius: 0 0 24px 24px;
  }

  .modal-header-info {
    padding-bottom: 0.85rem;
    margin-bottom: 0.85rem;
  }

  .modal-header-info h2 {
    font-size: 1.5rem;
    margin-bottom: 0.6rem;
  }

  .meta-badge {
    padding: 0.35rem 0.7rem;
    font-size: 0.8rem;
  }

  .modal-section {
    padding: 0.9rem 1rem;
    margin-bottom: 0.75rem;
    border-radius: 14px;
  }

  .section-title {
    font-size: 0.85rem;
    margin-bottom: 0.5rem;
  }

  .dream-content-text,
  .interpretation-text,
  .fortune-text {
    font-size: 0.85rem;
    line-height: 1.7;
  }

  .lucky-section {
    padding: 0.75rem 1rem;
  }

  .lucky-items {
    gap: 1.25rem;
  }

  .lucky-label {
    font-size: 0.65rem;
  }

  .lucky-value {
    font-size: 0.9rem;
  }

  .color-dot {
    width: 14px;
    height: 14px;
  }

  .modal-actions {
    gap: 0.6rem;
    padding-top: 0.85rem;
    flex-wrap: wrap;
  }

  .modal-action-btn {
    padding: 0.75rem 0.85rem;
    font-size: 0.8rem;
    gap: 0.3rem;
    border-radius: 12px;
  }

  .modal-action-btn svg {
    width: 16px;
    height: 16px;
  }
}

/* ===== 반응형 - 작은 모바일 ===== */
@media (max-width: 400px) {
  .nav-btn {
    width: 36px;
    height: 36px;
  }

  .nav-btn svg {
    width: 18px;
    height: 18px;
  }

  .frame-container {
    max-width: 180px;
  }

  .frame-outer {
    padding: 6px;
  }

  .frame-inner {
    padding: 2px;
  }

  .modal-info-section {
    padding: 1rem;
  }

  .modal-header-info h2 {
    font-size: 1.3rem;
  }

  .modal-section {
    padding: 0.75rem 0.85rem;
  }

  .dream-content-text,
  .interpretation-text,
  .fortune-text {
    font-size: 0.8rem;
  }

  .modal-actions {
    flex-direction: column;
    gap: 0.5rem;
  }

  .modal-action-btn {
    width: 100%;
    justify-content: center;
    padding: 0.7rem 0.75rem;
    font-size: 0.78rem;
  }
}

/* ===== 갤러리 카드 반응형 ===== */
@media (max-width: 768px) {
  .gallery-card {
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
}

@media (max-width: 480px) {
  .gallery-card {
    padding: 1.25rem;
    border-radius: 24px;
  }

  .page-title {
    font-size: 1.6rem;
    padding: 0.35rem 0.85rem;
  }

  .gallery-content {
    gap: 1.25rem;
  }

  .gallery-grid {
    gap: 1.25rem;
  }

  .gallery-grid.grid {
    grid-template-columns: 1fr;
  }

  .filter-buttons {
    gap: 0.5rem;
  }

  .filter-btn {
    padding: 0.55rem 1rem;
    font-size: 0.85rem;
  }

  .gallery-stats {
    gap: 1rem;
    padding: 0.85rem 1rem;
  }

  .stat-item {
    font-size: 0.9rem;
  }
}
</style>

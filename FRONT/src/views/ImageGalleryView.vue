<template>
  <div class="gallery-card">
    <div class="card-header">
      <button @click="handleBack" class="back-btn">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
      </button>
      <h2 class="page-title">이미지 갤러리</h2>
      <div class="spacer"></div>
    </div>

    <div class="gallery-content">
      <!-- 필터 및 검색 -->
      <div class="filter-section">
        <div class="search-box">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"></circle>
            <path d="m21 21-4.35-4.35"></path>
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="제목이나 태그로 검색..."
            class="search-input"
          />
        </div>
        
        <div class="filter-buttons">
          <button 
            v-for="filter in filters" 
            :key="filter.id"
            :class="['filter-btn', { active: activeFilter === filter.id }]"
            @click="activeFilter = filter.id"
          >
            <span>{{ filter.emoji }}</span>
            <span>{{ filter.label }}</span>
          </button>
        </div>
      </div>

      <!-- 갤러리 통계 -->
      <div class="gallery-stats">
        <div class="stat-item">
          <span class="stat-icon">🖼️</span>
          <span class="stat-text">전체 <strong>{{ filteredImages.length }}</strong>개</span>
        </div>
        <div class="stat-item">
          <span class="stat-icon">❤️</span>
          <span class="stat-text">좋아요 <strong>{{ totalLikes }}</strong>개</span>
        </div>
      </div>

      <!-- 뷰 모드 선택 -->
      <div class="view-mode-selector">
        <button 
          :class="['view-mode-btn', { active: viewMode === 'grid' }]"
          @click="viewMode = 'grid'"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="7" height="7"></rect>
            <rect x="14" y="3" width="7" height="7"></rect>
            <rect x="14" y="14" width="7" height="7"></rect>
            <rect x="3" y="14" width="7" height="7"></rect>
          </svg>
        </button>
        <button 
          :class="['view-mode-btn', { active: viewMode === 'masonry' }]"
          @click="viewMode = 'masonry'"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="8" y1="6" x2="21" y2="6"></line>
            <line x1="8" y1="12" x2="21" y2="12"></line>
            <line x1="8" y1="18" x2="21" y2="18"></line>
            <line x1="3" y1="6" x2="3.01" y2="6"></line>
            <line x1="3" y1="12" x2="3.01" y2="12"></line>
            <line x1="3" y1="18" x2="3.01" y2="18"></line>
          </svg>
        </button>
      </div>

      <!-- 이미지 그리드 -->
      <div v-if="filteredImages.length > 0" :class="['gallery-grid', viewMode]">
        <div 
          v-for="image in filteredImages" 
          :key="image.id"
          :class="['gallery-item', { tall: viewMode === 'masonry' && image.id % 3 === 0 }]"
          @click="openImageDetail(image)"
        >
          <div class="image-container" :style="{ background: image.gradient }">
            <div class="image-overlay">
              <span class="image-emoji">{{ image.emoji }}</span>
            </div>
          </div>
          <div class="image-info">
            <h4 class="image-title">{{ image.caption }}</h4>
            <div class="image-meta">
              <span class="meta-item">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                  <line x1="16" y1="2" x2="16" y2="6"></line>
                  <line x1="8" y1="2" x2="8" y2="6"></line>
                  <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
                {{ formatDate(image.createdAt) }}
              </span>
              <span class="meta-item">
                {{ image.style }}
              </span>
            </div>
            <div class="image-actions">
              <button 
                @click.stop="toggleLike(image)" 
                :class="['action-btn', { liked: image.liked }]"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" :fill="image.liked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                </svg>
                {{ image.likes }}
              </button>
              <button @click.stop="shareImage(image)" class="action-btn">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="18" cy="5" r="3"></circle>
                  <circle cx="6" cy="12" r="3"></circle>
                  <circle cx="18" cy="19" r="3"></circle>
                  <line x1="8.59" y1="13.51" x2="15.42" y2="17.49"></line>
                  <line x1="15.41" y1="6.51" x2="8.59" y2="10.49"></line>
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
        <span class="empty-emoji">🎨</span>
        <h3>아직 생성된 이미지가 없습니다</h3>
        <p>꿈 시각화 페이지에서 꿈을 이미지로 만들어보세요!</p>
        <button @click="goToVisualization" class="create-btn">
          ✨ 이미지 생성하러 가기
        </button>
      </div>
    </div>

    <!-- 이미지 상세 모달 -->
    <div v-if="selectedImage" class="modal-overlay" @click="closeImageDetail">
      <div class="modal-content" @click.stop>
        <button @click="closeImageDetail" class="modal-close">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
        <div class="modal-image" :style="{ background: selectedImage.gradient }">
          <span class="modal-emoji">{{ selectedImage.emoji }}</span>
        </div>
        <div class="modal-info">
          <h2>{{ selectedImage.caption }}</h2>
          <div class="modal-meta">
            <span>스타일: {{ selectedImage.style }}</span>
            <span>생성일: {{ formatDate(selectedImage.createdAt) }}</span>
          </div>
          <p v-if="selectedImage.prompt" class="modal-prompt">{{ selectedImage.prompt }}</p>
          <div class="modal-actions">
            <button @click="downloadImage(selectedImage)" class="modal-action-btn primary">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="7 10 12 15 17 10"></polyline>
                <line x1="12" y1="15" x2="12" y2="3"></line>
              </svg>
              다운로드
            </button>
            <button @click="shareImage(selectedImage)" class="modal-action-btn">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="18" cy="5" r="3"></circle>
                <circle cx="6" cy="12" r="3"></circle>
                <circle cx="18" cy="19" r="3"></circle>
                <line x1="8.59" y1="13.51" x2="15.42" y2="17.49"></line>
                <line x1="15.41" y1="6.51" x2="8.59" y2="10.49"></line>
              </svg>
              공유
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useGalleryStore } from '../stores/galleryStore';

const router = useRouter();
const galleryStore = useGalleryStore();
const { galleryImages } = storeToRefs(galleryStore);

const searchQuery = ref('');
const activeFilter = ref('all');
const viewMode = ref('grid');
const selectedImage = ref(null);

const filters = [
  { id: 'all', label: '전체', emoji: '🎨' },
  { id: 'recent', label: '최근', emoji: '🕐' },
  { id: 'liked', label: '좋아요', emoji: '❤️' },
  { id: 'dreamy', label: '몽환적', emoji: '🌙' },
];

// 필터링된 이미지
const filteredImages = computed(() => {
  let result = galleryImages.value;

  // 검색 필터
  if (searchQuery.value) {
    result = result.filter(img => 
      img.caption.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      img.style.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }

  // 카테고리 필터
  if (activeFilter.value === 'recent') {
    result = [...result].sort((a, b) => 
      new Date(b.createdAt) - new Date(a.createdAt)
    );
  } else if (activeFilter.value === 'liked') {
    result = result.filter(img => img.liked);
  } else if (activeFilter.value === 'dreamy') {
    result = result.filter(img => img.style === '몽환적');
  }

  return result;
});

const totalLikes = computed(() => {
  return galleryImages.value.reduce((sum, img) => sum + (img.likes || 0), 0);
});

function handleBack() {
  router.push({ name: 'calendar' });
}

function goToVisualization() {
  router.push({ name: 'visualization' });
}

function formatDate(dateString) {
  const date = new Date(dateString);
  return `${date.getMonth() + 1}월 ${date.getDate()}일`;
}

function toggleLike(image) {
  galleryStore.toggleImageLike(image.id);
}

function shareImage(image) {
  alert('이미지 공유 기능은 준비 중입니다!');
}

function deleteImage(image) {
  if (confirm('정말 이 이미지를 삭제하시겠습니까?')) {
    galleryStore.removeFromGallery(image.id);
    if (selectedImage.value?.id === image.id) {
      selectedImage.value = null;
    }
  }
}

function downloadImage(image) {
  alert('이미지 다운로드 기능은 준비 중입니다!');
}

function openImageDetail(image) {
  selectedImage.value = image;
}

function closeImageDetail() {
  selectedImage.value = null;
}

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;600;700;800&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Dongle:wght@300;400;700&display=swap');

.gallery-card {
  background: white;
  border-radius: 40px;
  padding: 2rem;
  width: 100%;
  max-width: 1200px;
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

.gallery-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

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
  background: #F8FAFF;
  border: 2px solid #E8F0FE;
  border-radius: 20px;
  transition: border-color 0.3s;
}

.search-box:focus-within {
  border-color: #A2D2FF;
}

.search-box svg {
  color: #999;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 1rem;
  font-family: 'Nunito', sans-serif;
  outline: none;
  color: #333;
}

.filter-buttons {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: 2px solid #E8F0FE;
  background: white;
  border-radius: 15px;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn:hover {
  border-color: #A2D2FF;
  background: #F8F9FF;
}

.filter-btn.active {
  border-color: #667eea;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.gallery-stats {
  display: flex;
  gap: 2rem;
  padding: 1rem;
  background: #F8FAFF;
  border-radius: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1rem;
  color: #666;
}

.stat-icon {
  font-size: 1.5rem;
}

.view-mode-selector {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.view-mode-btn {
  padding: 0.75rem;
  border: 2px solid #E8F0FE;
  background: white;
  border-radius: 10px;
  cursor: pointer;
  color: #666;
  transition: all 0.2s;
}

.view-mode-btn:hover {
  border-color: #A2D2FF;
  background: #F8F9FF;
}

.view-mode-btn.active {
  border-color: #667eea;
  background: #667eea;
  color: white;
}

.gallery-grid {
  display: grid;
  gap: 2rem;
}

.gallery-grid.grid {
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
}

.gallery-grid.masonry {
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  grid-auto-rows: 250px;
}

.gallery-item {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  cursor: pointer;
}

.gallery-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

.gallery-item.tall {
  grid-row: span 2;
}

.image-container {
  width: 100%;
  height: 200px;
  position: relative;
  overflow: hidden;
}

.gallery-item.tall .image-container {
  height: 100%;
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
  padding: 1.5rem;
}

.image-title {
  margin: 0 0 0.75rem 0;
  font-size: 1.1rem;
  color: #333;
  font-weight: 700;
}

.image-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1rem;
  font-size: 0.85rem;
  color: #999;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.image-actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.5rem 1rem;
  border: 2px solid #E8F0FE;
  background: white;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  border-color: #A2D2FF;
  background: #F8F9FF;
}

.action-btn.liked {
  border-color: #ff8a80;
  color: #ff8a80;
}

.action-btn.delete:hover {
  border-color: #ff8a80;
  color: #ff8a80;
}

.empty-gallery {
  text-align: center;
  padding: 5rem 2rem;
  color: #999;
}

.empty-emoji {
  font-size: 5rem;
  display: block;
  margin-bottom: 1rem;
}

.empty-gallery h3 {
  color: #333;
  margin-bottom: 0.5rem;
}

.create-btn {
  margin-top: 2rem;
  padding: 1rem 2rem;
  border: none;
  border-radius: 20px;
  font-size: 1.1rem;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, #667eea, #764ba2);
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 35px rgba(102, 126, 234, 0.4);
}

/* 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 2rem;
}

.modal-content {
  background: white;
  border-radius: 30px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow: auto;
  position: relative;
}

.modal-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: all 0.2s;
  z-index: 1;
}

.modal-close:hover {
  background: white;
  transform: scale(1.1);
}

.modal-image {
  width: 100%;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 30px 30px 0 0;
}

.modal-emoji {
  font-size: 8rem;
  filter: drop-shadow(0 8px 16px rgba(0, 0, 0, 0.2));
}

.modal-info {
  padding: 2rem;
}

.modal-info h2 {
  margin: 0 0 1rem 0;
  color: #333;
}

.modal-meta {
  display: flex;
  gap: 2rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  color: #999;
}

.modal-prompt {
  padding: 1rem;
  background: #F8FAFF;
  border-radius: 15px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.modal-actions {
  display: flex;
  gap: 1rem;
}

.modal-action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  border: 2px solid #E8F0FE;
  background: white;
  border-radius: 15px;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.modal-action-btn:hover {
  border-color: #A2D2FF;
  background: #F8F9FF;
  transform: translateY(-2px);
}

.modal-action-btn.primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
}

.modal-action-btn.primary:hover {
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}
</style>


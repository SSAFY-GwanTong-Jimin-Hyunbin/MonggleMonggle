<template>
  <div class="visualization-card">
    <div class="card-header">
      <button @click="handleBack" class="back-btn">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
      </button>
      <h2 class="page-title">꿈 시각화</h2>
      <div class="spacer"></div>
    </div>

    <div class="visualization-content">
      <!-- 꿈 선택 섹션 -->
      <div class="dream-select-section">
        <label>시각화할 꿈 선택</label>
        <select v-model="selectedDreamKey" class="dream-selector" @change="loadDreamContent">
          <option value="">꿈을 선택하세요</option>
          <option v-for="(dream, key) in postedDates.value" :key="key" :value="key">
            {{ formatDateDisplay(key) }} - {{ dream.title }}
          </option>
        </select>
      </div>

      <!-- 선택된 꿈 미리보기 -->
      <div v-if="selectedDream" class="dream-preview">
        <h3>{{ selectedDream.title }}</h3>
        <p>{{ selectedDream.content }}</p>
      </div>

      <!-- 프롬프트 입력 -->
      <div class="prompt-section">
        <label>이미지 스타일 선택</label>
        <div class="style-grid">
          <button 
            v-for="style in imageStyles" 
            :key="style.id"
            :class="['style-btn', { active: selectedStyle === style.id }]"
            @click="selectedStyle = style.id"
          >
            <span class="style-emoji">{{ style.emoji }}</span>
            <span class="style-name">{{ style.name }}</span>
          </button>
        </div>
      </div>

      <!-- 생성 버튼 -->
      <button 
        class="generate-btn"
        :disabled="!selectedDreamKey || isGenerating"
        @click="generateImage"
      >
        <span v-if="!isGenerating">✨ 이미지 생성하기</span>
        <span v-else>
          <span class="spinner"></span>
          생성 중...
        </span>
      </button>

      <!-- 생성된 이미지 -->
      <div v-if="generatedImages.length > 0" class="generated-images">
        <h3>생성된 이미지</h3>
        <div class="image-grid">
          <div 
            v-for="(img, index) in generatedImages" 
            :key="index"
            class="image-card"
          >
            <div class="image-placeholder" :style="{ background: img.gradient }">
              <div class="image-content">
                <span class="image-emoji">{{ img.emoji }}</span>
                <p class="image-caption">{{ img.caption }}</p>
              </div>
            </div>
            <div class="image-actions">
              <button @click="saveToGallery(img)" class="action-icon-btn">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                  <polyline points="17 21 17 13 7 13 7 21"></polyline>
                  <polyline points="7 3 7 8 15 8"></polyline>
                </svg>
              </button>
              <button @click="downloadImage(img)" class="action-icon-btn">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                  <polyline points="7 10 12 15 17 10"></polyline>
                  <line x1="12" y1="15" x2="12" y2="3"></line>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useDreamEntriesStore } from '../stores/dreamEntriesStore';
import { useGalleryStore } from '../stores/galleryStore';

const router = useRouter();
const dreamEntriesStore = useDreamEntriesStore();
const { postedDates } = storeToRefs(dreamEntriesStore);
const galleryStore = useGalleryStore();

const selectedDreamKey = ref('');
const selectedDream = ref(null);
const selectedStyle = ref('dreamy');
const customPrompt = ref('');
const isGenerating = ref(false);
const generatedImages = ref([]);

const imageStyles = [
  { id: 'dreamy', name: '몽환적', emoji: '🌙' },
  { id: 'watercolor', name: '수채화', emoji: '🎨' },
  { id: 'anime', name: '애니메이션', emoji: '✨' },
  { id: 'fantasy', name: '판타지', emoji: '🦄' }
];

const gradients = [
  'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
  'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
  'linear-gradient(135deg, #30cfd0 0%, #330867 100%)',
];

const emojis = ['🌟', '✨', '💫', '🌙', '⭐', '🌠', '🦋', '🌸', '🌺'];

function handleBack() {
  router.push({ name: 'calendar' });
}

function formatDateDisplay(dateKey) {
  const [year, month, day] = dateKey.split('-');
  return `${year}년 ${month}월 ${day}일`;
}

function loadDreamContent() {
  if (selectedDreamKey.value) {
    selectedDream.value = postedDates.value[selectedDreamKey.value];
  } else {
    selectedDream.value = null;
  }
}

async function generateImage() {
  if (!selectedDreamKey.value) return;
  
  isGenerating.value = true;
  
  // 이미지 생성 시뮬레이션 (실제로는 AI API 호출)
  await new Promise(resolve => setTimeout(resolve, 2000));
  
  const styleInfo = imageStyles.find(s => s.id === selectedStyle.value);
  const randomGradient = gradients[Math.floor(Math.random() * gradients.length)];
  const randomEmoji = emojis[Math.floor(Math.random() * emojis.length)];
  
  generatedImages.value.unshift({
    id: Date.now(),
    dreamKey: selectedDreamKey.value,
    style: styleInfo.name,
    gradient: randomGradient,
    emoji: randomEmoji,
    caption: `${selectedDream.value.title} - ${styleInfo.name} 스타일`,
    prompt: customPrompt.value,
    createdAt: new Date().toISOString()
  });
  
  isGenerating.value = false;
}

function saveToGallery(image) {
  galleryStore.addToGallery(image);
  alert('갤러리에 저장되었습니다!');
}

function downloadImage(image) {
  alert('이미지 다운로드 기능은 준비 중입니다.');
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;600;700;800&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Dongle:wght@300;400;700&display=swap');

.visualization-card {
  background: white;
  border-radius: 40px;
  padding: 2rem;
  width: 100%;
  max-width: 800px;
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

.visualization-content {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.dream-select-section label,
.prompt-section label,
.custom-prompt-section label {
  display: block;
  font-size: 0.9rem;
  font-weight: 700;
  color: #666;
  margin-bottom: 0.75rem;
}

.dream-selector {
  width: 100%;
  padding: 1rem;
  border: 2px solid #E8F0FE;
  border-radius: 15px;
  font-size: 1rem;
  font-family: 'Nunito', sans-serif;
  background: #FAFCFF;
  cursor: pointer;
  transition: border-color 0.3s;
}

.dream-selector:focus {
  outline: none;
  border-color: #A2D2FF;
}

.dream-preview {
  background: linear-gradient(135deg, #F8F9FF, #E8F0FE);
  padding: 1.5rem;
  border-radius: 20px;
  border-left: 4px solid #A2D2FF;
}

.dream-preview h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.2rem;
}

.dream-preview p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.style-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
}

.style-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 1.5rem 1rem;
  border: 2px solid #E8F0FE;
  border-radius: 15px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
}

.style-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(162, 210, 255, 0.2);
}

.style-btn.active {
  border-color: #A2D2FF;
  background: linear-gradient(135deg, #F8F9FF, #E8F0FE);
  box-shadow: 0 8px 20px rgba(162, 210, 255, 0.3);
}

.style-emoji {
  font-size: 2rem;
}

.style-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #666;
}

.custom-prompt-input {
  width: 100%;
  padding: 1rem;
  border: 2px solid #E8F0FE;
  border-radius: 15px;
  font-size: 1rem;
  font-family: 'Nunito', sans-serif;
  background: #FAFCFF;
  resize: vertical;
  min-height: 100px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.custom-prompt-input:focus {
  outline: none;
  border-color: #A2D2FF;
  box-shadow: 0 0 0 4px rgba(162, 210, 255, 0.1);
  background: white;
}

.generate-btn {
  width: 100%;
  padding: 1.25rem;
  border: none;
  border-radius: 20px;
  font-size: 1.2rem;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
}

.generate-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 15px 35px rgba(102, 126, 234, 0.4);
}

.generate-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 0.5rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.generated-images {
  margin-top: 2rem;
}

.generated-images h3 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #333;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

.image-card {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.image-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

.image-placeholder {
  width: 100%;
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.image-content {
  text-align: center;
  color: white;
  z-index: 1;
}

.image-emoji {
  font-size: 4rem;
  display: block;
  margin-bottom: 1rem;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
}

.image-caption {
  font-size: 1rem;
  font-weight: 600;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  padding: 0 1rem;
}

.image-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  padding: 1rem;
  background: white;
}

.action-icon-btn {
  padding: 0.5rem;
  border: none;
  background: #F0F4F8;
  border-radius: 10px;
  cursor: pointer;
  color: #666;
  transition: all 0.2s;
}

.action-icon-btn:hover {
  background: #E8F0FE;
  color: #667eea;
  transform: scale(1.1);
}
</style>


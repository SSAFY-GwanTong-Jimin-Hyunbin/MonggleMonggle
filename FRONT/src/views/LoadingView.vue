<template>
  <div class="loading-container">
    <div class="animation-wrapper">
      <div class="cloud cloud-1">
        <svg viewBox="0 0 24 24" fill="currentColor">
          <path d="M17,10c-0.6,0-1.1,0.1-1.6,0.3c-0.7-2.3-2.8-3.9-5.4-3.9c-3.1,0-5.6,2.3-6,5.3C1.8,12.2,0.3,14,0.3,16 c0,2.8,2.2,5,5,5h11.7c3.9,0,7-3.1,7-7C24,10.6,20.9,7.8,17,10z"/>
        </svg>
      </div>
      <div class="moon">
        <svg viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 3c.132 0 .263 0 .393 0a7.5 7.5 0 0 0 7.92 12.446a9 9 0 1 1 -8.313 -12.454z" />
        </svg>
      </div>
    </div>
    
    <div class="text-container">
      <transition name="fade" mode="out-in">
        <p :key="currentMessageIndex" class="loading-message">
          {{ messages[currentMessageIndex] }}
        </p>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const messages = [
  '꿈의 조각들을 모으고 있어요... 🧩',
  '별들에게 당신의 이야기를 들려주는 중입니다... ✨',
  '무의식의 거울을 닦고 있어요... 🪞',
  '달빛으로 꿈을 해석하는 중... 🌙',
  '오늘 밤, 당신에게 행운이 깃들기를... 🍀'
];

const currentMessageIndex = ref(0);
let intervalId = null;
let analysisTimeout = null;

onMounted(() => {
  intervalId = setInterval(() => {
    currentMessageIndex.value = (currentMessageIndex.value + 1) % messages.length;
  }, 2500);

  analysisTimeout = setTimeout(() => {
    router.push({ name: 'analysis' });
  }, 4000);
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
  if (analysisTimeout) clearTimeout(analysisTimeout);
});
</script>

<style scoped>
.loading-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
  color: white;
  text-align: center;
}

.animation-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  margin-bottom: 2rem;
}

.cloud {
  position: absolute;
  color: rgba(255, 255, 255, 0.8);
  width: 80px;
  height: 80px;
  bottom: 0;
  left: 0;
  filter: drop-shadow(0 0 10px rgba(255, 255, 255, 0.5));
  animation: floatCloud 3s ease-in-out infinite;
}

.moon {
  position: absolute;
  color: #FFEB3B;
  width: 60px;
  height: 60px;
  top: 0;
  right: 0;
  filter: drop-shadow(0 0 15px rgba(255, 235, 59, 0.6));
  animation: floatMoon 4s ease-in-out infinite alternate;
}

.loading-message {
  font-size: 1.4rem;
  font-weight: 600;
  color: white;
  text-shadow: 0 2px 4px rgba(0,0,0,0.2);
  min-height: 2em; /* Prevent layout shift */
  font-family: 'Nunito', sans-serif;
  padding: 0 1rem;
}

@keyframes floatCloud {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes floatMoon {
  0% { transform: rotate(-5deg) translateY(0); }
  100% { transform: rotate(5deg) translateY(-15px); }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>


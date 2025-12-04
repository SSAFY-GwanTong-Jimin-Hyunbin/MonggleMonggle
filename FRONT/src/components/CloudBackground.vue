<template>
  <div class="cloud-background">
    <!-- Night Sky Gradient -->
    <div class="sky-gradient"></div>

    <!-- Stars -->
    <div class="stars-container">
      <div 
        v-for="star in stars" 
        :key="star.id"
        class="star"
        :style="{
          top: star.top,
          left: star.left,
          width: star.size,
          height: star.size,
          animationDuration: star.animationDuration,
          animationDelay: star.animationDelay,
          opacity: star.opacity
        }"
      ></div>
    </div>

    <!-- Moon (Original Yellow Crescent) -->
    <div class="moon">
      <svg width="100" height="100" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <linearGradient id="moonGradient" x1="20%" y1="20%" x2="80%" y2="80%">
            <stop offset="0%" stop-color="#FFFDE7"/>
            <stop offset="100%" stop-color="#FFD54F"/>
          </linearGradient>
          <filter id="glow">
            <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
            <feMerge>
              <feMergeNode in="coloredBlur"/>
              <feMergeNode in="SourceGraphic"/>
            </feMerge>
          </filter>
          <!-- Mask for crescent shape -->
          <mask id="crescentMask">
            <rect x="0" y="0" width="100" height="100" fill="white"/>
            <circle cx="62" cy="45" r="32" fill="black"/>
          </mask>
        </defs>
        <!-- Glow -->
        <circle cx="50" cy="50" r="40" fill="rgba(255, 241, 118, 0.2)" filter="url(#glow)"/>
        <!-- Moon Body -->
        <circle 
          cx="50" 
          cy="50" 
          r="36" 
          fill="url(#moonGradient)" 
          mask="url(#crescentMask)"
          filter="url(#glow)"
          transform="rotate(-10, 50, 50)"
        />
      </svg>
    </div>
  </div>
</template>

<script setup>
const stars = Array.from({ length: 60 }, (_, i) => ({
  id: i,
  top: Math.random() * 100 + '%',
  left: Math.random() * 100 + '%',
  size: Math.random() * 3 + 2 + 'px',
  animationDuration: Math.random() * 3 + 2 + 's',
  animationDelay: Math.random() * 2 + 's',
  opacity: Math.random() * 0.5 + 0.3
}));
</script>

<style scoped>
.cloud-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  z-index: -1;
  overflow: hidden;
  background-color: #4A5080;
}

.sky-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* Deep Purple -> Soft Lavender/Pinkish Gradient */
  background: linear-gradient(180deg, #454278 0%, #7C78B8 60%, #C4B6DC 100%);
}

.moon {
  position: absolute;
  top: 8%;
  left: 8%;
  animation: float 6s ease-in-out infinite;
  z-index: 2;
}

.stars-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.star {
  position: absolute;
  background-color: #FFFDE7;
  border-radius: 50%;
  box-shadow: 0 0 5px rgba(255, 255, 255, 0.6);
  animation-name: twinkle;
  animation-timing-function: ease-in-out;
  animation-iteration-count: infinite;
}

/* Animations */
@keyframes twinkle {
  0%, 100% { opacity: 0.4; transform: scale(0.9); }
  50% { opacity: 1; transform: scale(1.1); }
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(-10deg); }
  50% { transform: translateY(-8px) rotate(-8deg); }
}
</style>

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useGalleryStore } from './galleryStore';
import { useMonthlyMemoStore } from './monthlyMemoStore';
import { luckyColorPalette, getLuckyColorById } from '../constants/luckyColors';

const STORAGE_KEY = 'dreamEntriesStore';
const LEGACY_KEY = 'dreamStore';

function formatDateKey(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function getRandomLuckyColorId() {
  return Math.floor(Math.random() * luckyColorPalette.length);
}

export const useDreamEntriesStore = defineStore('dreamEntries', () => {
  const selectedDate = ref(null);
  const dreamTitle = ref('');
  const dreamContent = ref('');
  const selectedEmotion = ref(null);
  const posts = ref({});
  const showAnalysisOption = ref(false);
  const currentLuckyColorId = ref(getRandomLuckyColorId());

  function setSelectedDate(date) {
    selectedDate.value = date;
    if (!date) {
      resetWriteFields();
      return;
    }

    const dateKey = formatDateKey(date);
    const existingPost = posts.value[dateKey];

    if (existingPost) {
      dreamTitle.value = existingPost.title;
      dreamContent.value = existingPost.content;
      selectedEmotion.value = existingPost.emotion ?? null;
      showAnalysisOption.value = true;
      if (typeof existingPost.luckyColorId === 'number') {
        currentLuckyColorId.value = existingPost.luckyColorId;
      }
    } else {
      resetWriteFields();
      showAnalysisOption.value = false;
      currentLuckyColorId.value = getRandomLuckyColorId();
    }
  }

  function resetWriteFields() {
    dreamTitle.value = '';
    dreamContent.value = '';
    selectedEmotion.value = null;
  }

  function saveDream() {
    if (!selectedDate.value) return false;

    const dateKey = formatDateKey(selectedDate.value);
    const luckyColor = getLuckyColorById(currentLuckyColorId.value);
    posts.value = {
      ...posts.value,
      [dateKey]: {
        title: dreamTitle.value,
        content: dreamContent.value,
        emotion: selectedEmotion.value,
        color: luckyColor.hex,
        luckyColorId: luckyColor.id,
        luckyColorName: luckyColor.name
      }
    };

    showAnalysisOption.value = true;
    persistEntries();
    return true;
  }

  function deleteDream() {
    if (!selectedDate.value) return;
    const dateKey = formatDateKey(selectedDate.value);

    if (!posts.value[dateKey]) return;

    const updatedPosts = { ...posts.value };
    delete updatedPosts[dateKey];
    posts.value = updatedPosts;

    resetWriteFields();
    selectedEmotion.value = null;
    showAnalysisOption.value = false;
    persistEntries();
  }

  function enableEditMode() {
    showAnalysisOption.value = false;
  }

  function resetWriteState() {
    showAnalysisOption.value = false;
  }

  function clearSelectedDate() {
    selectedDate.value = null;
    resetWriteFields();
    showAnalysisOption.value = false;
    currentLuckyColorId.value = getRandomLuckyColorId();
  }

  function resetAll() {
    clearSelectedDate();
    posts.value = {};
    currentLuckyColorId.value = getRandomLuckyColorId();
    persistEntries();

    // cascade reset to other domains
    const galleryStore = useGalleryStore();
    galleryStore.resetGallery();

    const memoStore = useMonthlyMemoStore();
    memoStore.resetMemos();
  }

  function setEmotion(emotion) {
    selectedEmotion.value = emotion;
  }

  function getMonthlyStats(year, month) {
    const yearMonth = `${year}-${String(month).padStart(2, '0')}`;
    const monthlyDreams = Object.entries(posts.value).filter(([dateKey]) =>
      dateKey.startsWith(yearMonth)
    );

    return {
      totalDreams: monthlyDreams.length,
      dreams: monthlyDreams.map(([dateKey, dream]) => ({
        date: dateKey,
        ...dream
      }))
    };
  }

  const formattedSelectedDate = computed(() => {
    if (!selectedDate.value) return '';
    return selectedDate.value.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  });

  const postedDates = computed(() => posts.value);
  const currentLuckyColor = computed(() => getLuckyColorById(currentLuckyColorId.value));

  function setLuckyColorId(id) {
    if (typeof id === 'number' && luckyColorPalette.some(color => color.id === id)) {
      currentLuckyColorId.value = id;
    } else {
      currentLuckyColorId.value = getRandomLuckyColorId();
    }

    persistEntries();
  }

  function persistEntries() {
    if (typeof window === 'undefined') return;
    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify({
        posts: posts.value,
        luckyColorId: currentLuckyColorId.value
      })
    );
  }

  function hydrateFromLocalStorage() {
    if (typeof window === 'undefined') return;

    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      const data = JSON.parse(saved);
      posts.value = data.posts || {};
      currentLuckyColorId.value =
        typeof data.luckyColorId === 'number' ? data.luckyColorId : 0;
      return;
    }

    const legacy = localStorage.getItem(LEGACY_KEY);
    if (legacy) {
      const data = JSON.parse(legacy);
      posts.value = data.posts || {};
    }
  }

  hydrateFromLocalStorage();

  return {
    selectedDate,
    dreamTitle,
    dreamContent,
    selectedEmotion,
    posts,
    showAnalysisOption,
    formattedSelectedDate,
    postedDates,
    currentLuckyColor,
    setSelectedDate,
    saveDream,
    deleteDream,
    setEmotion,
    enableEditMode,
    resetWriteState,
    clearSelectedDate,
    resetAll,
    setLuckyColorId,
    getMonthlyStats,
    persistEntries
  };
});


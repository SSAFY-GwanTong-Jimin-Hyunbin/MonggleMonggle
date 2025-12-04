import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

const STORAGE_KEY = 'dreamGalleryStore';
const LEGACY_KEY = 'dreamStore';

export const useGalleryStore = defineStore('gallery', () => {
  const gallery = ref([]);

  const galleryImages = computed(() => gallery.value);

  function addToGallery(image) {
    const entry = {
      id: image.id ?? Date.now(),
      likes: typeof image.likes === 'number' ? image.likes : 0,
      liked: image.liked ?? false,
      createdAt: image.createdAt ?? new Date().toISOString(),
      ...image
    };

    gallery.value = [...gallery.value, entry];
    persistGallery();
  }

  function removeFromGallery(imageId) {
    gallery.value = gallery.value.filter(img => img.id !== imageId);
    persistGallery();
  }

  function toggleImageLike(imageId) {
    const image = gallery.value.find(img => img.id === imageId);
    if (!image) return;

    image.liked = !image.liked;
    const delta = image.liked ? 1 : -1;
    image.likes = Math.max(0, (image.likes || 0) + delta);
    persistGallery();
  }

  function resetGallery() {
    gallery.value = [];
    persistGallery();
  }

  function persistGallery() {
    if (typeof window === 'undefined') return;
    localStorage.setItem(STORAGE_KEY, JSON.stringify(gallery.value));
  }

  function hydrateFromLocalStorage() {
    if (typeof window === 'undefined') return;

    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      gallery.value = JSON.parse(saved);
      return;
    }

    const legacy = localStorage.getItem(LEGACY_KEY);
    if (legacy) {
      const data = JSON.parse(legacy);
      gallery.value = (data.gallery || []).map(img => ({
        ...img,
        likes: typeof img.likes === 'number' ? img.likes : Math.floor(Math.random() * 20),
        liked: img.liked ?? false
      }));
    }
  }

  hydrateFromLocalStorage();

  return {
    gallery,
    galleryImages,
    addToGallery,
    removeFromGallery,
    toggleImageLike,
    resetGallery,
    persistGallery
  };
});


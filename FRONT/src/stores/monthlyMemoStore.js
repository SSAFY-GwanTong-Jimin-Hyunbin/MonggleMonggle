import { defineStore } from 'pinia';
import { ref } from 'vue';

const STORAGE_KEY = 'dreamMonthlyMemoStore';
const LEGACY_KEY = 'dreamStore';

function getMemoKey(year, month) {
  return `${year}-${String(month).padStart(2, '0')}`;
}

export const useMonthlyMemoStore = defineStore('monthlyMemos', () => {
  const monthlyMemos = ref({});

  function saveMonthlyMemo(year, month, memo) {
    const key = getMemoKey(year, month);
    monthlyMemos.value = {
      ...monthlyMemos.value,
      [key]: memo
    };
    persistMemos();
  }

  function getMonthlyMemo(year, month) {
    const key = getMemoKey(year, month);
    return monthlyMemos.value[key] || '';
  }

  function resetMemos() {
    monthlyMemos.value = {};
    persistMemos();
  }

  function persistMemos() {
    if (typeof window === 'undefined') return;
    localStorage.setItem(STORAGE_KEY, JSON.stringify(monthlyMemos.value));
  }

  function hydrateFromLocalStorage() {
    if (typeof window === 'undefined') return;

    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      monthlyMemos.value = JSON.parse(saved);
      return;
    }

    const legacy = localStorage.getItem(LEGACY_KEY);
    if (legacy) {
      const data = JSON.parse(legacy);
      monthlyMemos.value = data.monthlyMemos || {};
    }
  }

  hydrateFromLocalStorage();

  return {
    monthlyMemos,
    saveMonthlyMemo,
    getMonthlyMemo,
    resetMemos,
    persistMemos
  };
});


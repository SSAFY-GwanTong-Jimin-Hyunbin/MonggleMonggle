<template>
  <div class="common-card">
    <div class="page-header">
      <h2 class="page-title">마이페이지</h2>
      <p class="page-subtitle">내 정보를 수정해보세요</p>
    </div>

    <div class="common-form">
      <!-- 이름 -->
      <div class="input-group labeled">
        <label class="input-label">이름</label>
        <input 
          v-model="formData.name" 
          type="text" 
          class="custom-input"
          placeholder="이름"
          @blur="validation.validateName(formData.name)"
        />
        <p v-if="validation.errors.name" class="error-text">{{ validation.errors.name }}</p>
      </div>

      <!-- 생년월일 -->
      <div class="input-group labeled">
        <label class="input-label">생년월일</label>
        <input 
          v-model="formData.birthDate" 
          type="date" 
          data-placeholder="YYYY-MM-DD"
          class="custom-input date-input"
          :max="today"
          @click="showDatePicker"
          @blur="validation.validateBirthDate(formData.birthDate)"
        />
        <p v-if="validation.errors.birthDate" class="error-text">{{ validation.errors.birthDate }}</p>
      </div>

      <!-- 달력 유형 (양력/음력) -->
      <div class="input-group">
        <div class="radio-group">
          <label class="radio-label">
            <input 
              type="radio" 
              v-model="formData.calendarType" 
              value="solar" 
              class="custom-radio"
            />
            <span class="radio-text">양력</span>
          </label>
          <label class="radio-label">
            <input 
              type="radio" 
              v-model="formData.calendarType" 
              value="lunar" 
              class="custom-radio"
            />
            <span class="radio-text">음력</span>
          </label>
        </div>
        <p v-if="validation.errors.calendarType" class="error-text">{{ validation.errors.calendarType }}</p>
      </div>

      <!-- 성별 -->
      <div class="input-group labeled">
        <label class="input-label">성별</label>
        <div class="radio-group">
          <label class="radio-label">
            <input 
              type="radio" 
              v-model="formData.gender" 
              value="male" 
              class="custom-radio"
            />
            <span class="radio-text">남</span>
          </label>
          <label class="radio-label">
            <input 
              type="radio" 
              v-model="formData.gender" 
              value="female" 
              class="custom-radio"
            />
            <span class="radio-text">여</span>
          </label>
        </div>
        <p v-if="validation.errors.gender" class="error-text">{{ validation.errors.gender }}</p>
      </div>

      <!-- 아이디 (변경 불가) -->
      <div class="input-group labeled">
        <label class="input-label">아이디</label>
        <input 
          v-model="formData.loginId" 
          type="text" 
          class="custom-input"
          placeholder="아이디"
          disabled
        />
      </div>

      <!-- 비밀번호 (변경 시에만 입력) -->
      <div class="input-group labeled">
        <label class="input-label">비밀번호 변경</label>
        <input 
          v-model="formData.password" 
          type="password" 
          class="custom-input"
          placeholder="새 비밀번호 (영문, 숫자, 특수문자 8~20자)"
          @input="validation.filterPassword"
          @blur="formData.password && validation.validatePassword(formData.password)"
        />
        <p v-if="validation.errors.password" class="error-text">{{ validation.errors.password }}</p>
      </div>

      <!-- 비밀번호 확인 -->
      <div class="input-group">
        <input 
          v-model="formData.confirmPassword" 
          type="password" 
          class="custom-input"
          placeholder="새 비밀번호 확인"
          @input="validation.filterPassword"
          @blur="formData.password && validation.validateConfirmPassword(formData.password, formData.confirmPassword)"
        />
        <p v-if="validation.errors.confirmPassword" class="error-text">{{ validation.errors.confirmPassword }}</p>
      </div>

      <button class="submit-btn" @click="saveUserInfo" :disabled="isLoading">
        {{ isLoading ? '처리 중...' : '수정 완료' }}
      </button>

      <div class="footer-actions">
        <span class="withdraw-text" @click="handleWithdraw">서비스 탈퇴</span>
        <span class="back-text" @click="handleBack">뒤로가기</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useDreamEntriesStore } from '../stores/dreamEntriesStore';
import { useAuthStore } from '../stores/authStore';
import { useUserStorage } from '../composables/useUserStorage';
import { useFormValidation } from '../composables/useFormValidation';

const router = useRouter();
const dreamEntriesStore = useDreamEntriesStore();
const authStore = useAuthStore();
const { resetAll } = dreamEntriesStore;
const { clearSessionUser } = useUserStorage();
const validation = useFormValidation();

const formData = reactive({
  loginId: '',
  password: '',
  confirmPassword: '',
  name: '',
  birthDate: '',
  gender: '',
  calendarType: ''
});

const isLoading = ref(false);

// 오늘 날짜 (생년월일 최대값)
const today = computed(() => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
});

function showDatePicker(event) {
  try {
    event.target.showPicker();
  } catch (error) {
    // ignore
  }
}

function handleBack() {
  router.push({ name: 'calendar' });
}

async function saveUserInfo() {
  const hasPasswordChange = !!formData.password;
  
  // 유효성 검사
  if (!validation.validateUpdateForm(formData, hasPasswordChange)) {
    return;
  }

  isLoading.value = true;
  
  try {
    // 백엔드 API 호출을 위한 데이터 준비
    const updateData = {
      name: formData.name,
      birthDate: formData.birthDate,
      gender: validation.genderToBackend(formData.gender),
      calendarType: validation.calendarTypeToBackend(formData.calendarType),
    };
    
    // 비밀번호가 입력된 경우에만 포함
    if (formData.password) {
      updateData.password = formData.password;
    }
    
    // 백엔드 API를 통해 정보 수정
    await authStore.updateUser(updateData);
    
    // 비밀번호 필드 초기화
    formData.password = '';
    formData.confirmPassword = '';
    validation.clearError('password');
    validation.clearError('confirmPassword');

    alert('정보가 수정되었습니다.');
    router.push({ name: 'calendar' });
  } catch (error) {
    alert(authStore.error || '정보 수정에 실패했습니다.');
  } finally {
    isLoading.value = false;
  }
}

async function handleWithdraw() {
  if (confirm('정말로 탈퇴하시겠습니까? 모든 데이터가 삭제됩니다.')) {
    try {
      await authStore.deleteAccount();
      resetAll();
      clearSessionUser();
      alert('서비스 탈퇴가 완료되었습니다.');
      router.push({ name: 'auth' });
    } catch (err) {
      alert(authStore.error || '탈퇴 처리 중 오류가 발생했습니다.');
    }
  }
}

onMounted(async () => {
  isLoading.value = true;
  
  try {
    // 백엔드에서 최신 사용자 정보 가져오기
    const response = await authStore.fetchCurrentUser();
    
    if (response) {
      formData.loginId = response.loginId || '';
      formData.name = response.name || '';
      formData.birthDate = response.birthDate || '';
      formData.gender = validation.genderToFrontend(response.gender);
      formData.calendarType = validation.calendarTypeToFrontend(response.calendarType);
      
      // 비밀번호 필드는 비워둠 (보안 및 UX)
      formData.password = '';
      formData.confirmPassword = '';
    }
  } catch (error) {
    console.error('사용자 정보 조회 실패:', error);
    router.push({ name: 'auth' });
  } finally {
    isLoading.value = false;
  }
});
</script>

<style scoped>
.input-group.labeled {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
</style>

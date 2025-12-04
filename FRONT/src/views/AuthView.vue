<template>
  <div class="auth-wrapper">
    <div class="split-left">
      <h1 class="service-title">몽글몽글</h1>
    </div>
    <div class="split-right">
      <div class="common-card">
        <div class="page-header">
      <h2 class="page-title">{{ isLogin ? '환영합니다' : '회원가입' }}</h2>
      <p class="page-subtitle">{{ isLogin ? '꿈 기록을 시작하려면 로그인하세요' : '꿈의 여정을 시작해보세요' }}</p>
    </div>

    <form @submit.prevent="handleSubmit" class="common-form">
      <div v-if="!isLogin" class="input-group">
        <input 
          v-model="name" 
          type="text" 
          placeholder="이름" 
          class="auth-input"
          required
        />
      </div>

      <div v-if="!isLogin" class="input-group">
        <label class="input-label">생년월일</label>
        <input 
          v-model="birthDate" 
          type="date" 
          data-placeholder="YYYY-MM-DD"
          class="auth-input date-input"
          required
          @click="showDatePicker"
        />
      </div>

      <div v-if="!isLogin" class="input-group">
        <label class="input-label">성별</label>
        <div class="radio-group">
          <label class="radio-label">
            <input 
              type="radio" 
              v-model="gender" 
              value="male" 
              class="custom-radio"
              required 
            />
            <span class="radio-label-text">남</span>
          </label>
          <label class="radio-label">
            <input 
              type="radio" 
              v-model="gender" 
              value="female" 
              class="custom-radio"
              required 
            />
            <span class="radio-label-text">여</span>
          </label>
        </div>
      </div>

      <div v-if="isLogin" class="input-group">
        <input 
          v-model="loginId" 
          type="text" 
          placeholder="아이디" 
          class="auth-input"
          required
          @input="allowOnlyAlphaNumeric"
        />
      </div>

      <div v-else class="input-group">
        <input 
          v-model="signupId" 
          type="text" 
          placeholder="아이디" 
          class="auth-input"
          required
          @input="allowOnlyAlphaNumeric"
        />
        <p v-if="!isLogin && loginError" class="error-text">{{ loginError }}</p>
      </div>
      
      <div v-if="isLogin" class="input-group">
        <input 
          v-model="loginPassword" 
          type="password" 
          placeholder="비밀번호" 
          class="auth-input"
          required
          @input="allowOnlyAlphaNumeric"
        />
        <p v-if="isLogin && loginError" class="error-text">{{ loginError }}</p>
      </div>

      <div v-else class="input-group">
        <input 
          v-model="signupPassword" 
          type="password" 
          placeholder="비밀번호" 
          class="auth-input"
          required
          @input="allowOnlyAlphaNumeric"
        />
      </div>

      <div v-if="!isLogin" class="input-group">
        <input 
          v-model="confirmPassword" 
          type="password" 
          placeholder="비밀번호 확인" 
          class="auth-input"
          required
          @input="allowOnlyAlphaNumeric"
        />
        <p v-if="passwordError" class="error-text">{{ passwordError }}</p>
      </div>

      <button type="submit" class="submit-btn">
        {{ isLogin ? '로그인' : '회원가입' }}
      </button>
    </form>

    <div class="auth-footer">
      <p class="auth-footer-text">
        {{ isLogin ? "계정이 없으신가요?" : "이미 계정이 있으신가요?" }}
        <button @click="toggleAuthMode" class="toggle-btn">
          {{ isLogin ? '회원가입' : '로그인' }}
        </button>
      </p>
    </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useDreamEntriesStore } from '../stores/dreamEntriesStore';
import { useUserStorage } from '../composables/useUserStorage';
import { usePasswordValidation } from '../composables/usePasswordValidation';
import { useInputValidation } from '../composables/useInputValidation';

const router = useRouter();
const dreamEntriesStore = useDreamEntriesStore();
const { resetAll } = dreamEntriesStore;
const { loadAllUsers, saveAllUsers, checkIdExists, setSessionUser } = useUserStorage();
const { allowOnlyAlphaNumeric } = useInputValidation();

const isLogin = ref(true);
const loginId = ref('');
const loginPassword = ref('');
const signupId = ref('');
const signupPassword = ref('');
const confirmPassword = ref('');
const name = ref('');
const birthDate = ref('');
const gender = ref('');
const loginError = ref('');

const { passwordError, validateMatch } = usePasswordValidation(signupPassword, confirmPassword);

function toggleAuthMode() {
  isLogin.value = !isLogin.value;
  // 모드 전환 시 상태 초기화
  loginError.value = '';
  loginId.value = '';
  loginPassword.value = '';
  signupId.value = '';
  signupPassword.value = '';
  confirmPassword.value = '';
  name.value = '';
  birthDate.value = '';
  gender.value = '';
}

function showDatePicker(event) {
  try {
    event.target.showPicker();
  } catch (error) {
    // showPicker가 지원되지 않는 브라우저일 경우 무시
    console.log('Date picker not supported programmatically');
  }
}

function handleSubmit() {
  const users = loadAllUsers();

  if (!isLogin.value) {
    if (!validateMatch()) {
      alert('비밀번호가 일치하지 않습니다!');
      return;
    }

    if (checkIdExists(signupId.value)) {
      loginError.value = '이미 사용 중인 아이디입니다.';
      alert('이미 사용 중인 아이디입니다.');
      return;
    }

    const newUser = {
      loginId: signupId.value,
      password: signupPassword.value,
      name: name.value,
      birthDate: birthDate.value,
      gender: gender.value
    };
    
    // 데이터가 모두 있는지 확인
    if (!newUser.loginId || !newUser.password || !newUser.name || !newUser.birthDate || !newUser.gender) {
      // 어떤 항목이 누락되었는지 확인 (디버깅용)
      let missing = [];
      if (!newUser.loginId) missing.push('아이디');
      if (!newUser.password) missing.push('비밀번호');
      if (!newUser.name) missing.push('이름');
      if (!newUser.birthDate) missing.push('생년월일');
      if (!newUser.gender) missing.push('성별');
      
      alert(`다음 항목을 입력해주세요: ${missing.join(', ')}`);
      return;
    }

    users.push(newUser);
    saveAllUsers(users);
    
    // 회원가입 후 자동 로그인 처리 (세션 저장)
    setSessionUser(newUser);

    // 성공 메시지 표시
    alert('회원가입이 완료되었습니다.');
  } else {
    const existingUser = users.find(user => user.loginId === loginId.value);
    if (!existingUser) {
      loginError.value = '유효한 계정이 아닙니다.';
      return;
    }

    if (existingUser.password !== loginPassword.value) {
      loginError.value = '비밀번호가 일치하지 않습니다.';
      return;
    }

    // 로그인 성공 처리 (세션 저장)
    setSessionUser(existingUser);
  }

  loginError.value = '';

  resetAll();
  router.push({ name: 'calendar' });
}

watch([signupId, isLogin], ([currentId, loginState]) => {
  if (loginState) {
    loginError.value = '';
    return;
  }

  if (!currentId) {
    loginError.value = '';
    return;
  }

  loginError.value = checkIdExists(currentId)
    ? '이미 사용 중인 아이디입니다.'
    : '';
});

watch([loginId, loginPassword], () => {
  if (isLogin.value && loginError.value) {
    loginError.value = '';
  }
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Dongle:wght@300;400;700&display=swap');

.auth-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  min-height: 100%;
}

.split-right {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.split-left {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.service-title {
  font-family: 'Dongle', sans-serif;
  font-size: 5rem;
  font-weight: 700;
  /* Text Gradient: Pastel Purple -> Pink -> Blue */
  background: linear-gradient(to right, #CDB4DB, #FFC8DD, #A2D2FF);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  color: transparent;
  
  margin: 0 0 3rem 0;
  line-height: 1.2;
  letter-spacing: 2px;
  text-align: center;
  
  filter: drop-shadow(0 2px 4px rgba(255, 255, 255, 0.3));
}

/* 데스크톱 레이아웃 */
@media (min-width: 1024px) {
  .auth-wrapper {
    flex-direction: row;
    align-items: center;
    justify-content: center;
    max-width: 100%;
    gap: 0;
  }

  .split-left {
    width: 50%;
    height: 100%;
  }

  .split-right {
    width: 50%;
    height: 100%;
    justify-content: flex-start;
    padding-left: 30px;
  }

  .service-title {
    margin: 0;
    font-size: 8rem;
  }
  
  .common-card {
    width: 100%;
    max-width: 480px;
  }
}

.auth-footer {
  margin-top: 2rem;
  font-size: 0.9rem;
  color: #999;
}

.auth-footer-text {
  font-weight: 600;
}

.toggle-btn {
  background: none;
  border: none;
  color: #8EC5FC;
  font-weight: 700;
  cursor: pointer;
  padding: 0 5px;
  font-size: 0.9rem;
}

.toggle-btn:hover {
  text-decoration: underline;
}
</style>

import { ref, watch } from 'vue';

export function usePasswordValidation(password, confirmPassword) {
  const passwordError = ref('');

  const validateMatch = () => {
    if (!confirmPassword.value) {
      passwordError.value = '';
      return true;
    }
    
    if (password.value !== confirmPassword.value) {
      passwordError.value = '비밀번호가 일치하지 않습니다.';
      return false;
    }
    
    passwordError.value = '';
    return true;
  };

  // Watch for changes if refs are provided
  if (password && confirmPassword) {
    watch([password, confirmPassword], () => {
      validateMatch();
    });
  }

  return {
    passwordError,
    validateMatch
  };
}


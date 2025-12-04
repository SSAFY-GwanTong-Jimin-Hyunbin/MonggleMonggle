export function useInputValidation() {
  
  function allowOnlyAlphaNumeric(event) {
    const value = event.target.value;
    // 영어와 숫자만 허용하는 정규식
    const newValue = value.replace(/[^A-Za-z0-9]/g, '');
    
    if (value !== newValue) {
      event.target.value = newValue;
      // v-model 업데이트를 위해 input 이벤트 수동 트리거
      event.target.dispatchEvent(new Event('input'));
    }
  }

  return {
    allowOnlyAlphaNumeric
  };
}


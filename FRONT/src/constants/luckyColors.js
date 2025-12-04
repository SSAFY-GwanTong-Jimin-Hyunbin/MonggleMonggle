export const luckyColorPalette = [
  {
    id: 0,
    name: '회색',
    hex: '#B0BEC5',
    reason: '차분한 회색은 마음을 정리하며, 감정 기복 대신 균형 감각을 선사합니다.'
  },
  {
    id: 1,
    name: '갈색',
    hex: '#A1887F',
    reason: '견고한 갈색은 현실감을 되찾게 해주고, 책임감 있는 선택을 돕습니다.'
  },
  {
    id: 2,
    name: '주황색',
    hex: '#FFAB91',
    reason: '따뜻한 주황빛이 에너지를 북돋아 새로운 도전의 추진력을 줍니다.'
  },
  {
    id: 3,
    name: '노란색',
    hex: '#FFE082',
    reason: '밝은 노란색은 낙관적인 사고를 불러와 창의적인 아이디어를 끌어냅니다.'
  },
  {
    id: 4,
    name: '초록색',
    hex: '#A5D6A7',
    reason: '싱그러운 초록은 회복력을 주고, 장기적인 계획을 지탱해 줍니다.'
  },
  {
    id: 5,
    name: '파란색',
    hex: '#90CAF9',
    reason: '맑은 파랑은 집중력을 높여 복잡한 상황도 냉정하게 풀어가게 합니다.'
  },
  {
    id: 6,
    name: '보라색',
    hex: '#B39DDB',
    reason: '신비로운 보라는 직관을 깨워 숨은 기회를 포착하도록 도와줍니다.'
  },
  {
    id: 7,
    name: '분홍색',
    hex: '#F48FB1',
    reason: '부드러운 분홍색은 인간관계를 유연하게 만들어 갈등을 완화합니다.'
  },
  {
    id: 8,
    name: '빨간색',
    hex: '#EF9A9A',
    reason: '강렬한 빨강은 용기를 북돋아 결단력 있는 행동을 이끌어 냅니다.'
  }
];

export function getLuckyColorById(id) {
  return luckyColorPalette.find(color => color.id === id) ?? luckyColorPalette[0];
}


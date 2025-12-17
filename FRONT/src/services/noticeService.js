import api from './api';

export const noticeService = {
  // 전체 공지사항 조회
  async getAllNotices() {
    const response = await api.get('/notices');
    return response.data;
  },

  // 상세 공지사항 조회
  async getNoticeById(noticeId) {
    const response = await api.get(`/notices/${noticeId}`);
    return response.data;
  },
};

export default noticeService;


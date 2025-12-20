import api from "./api";

/**
 * 꿈 일기 개수 랭킹 조회
 * @returns {Promise<{rankings: Array<{rank: number, maskedName: string, dreamCount: number}>, totalUsers: number}>}
 */
export async function getDreamCountRanking() {
  const response = await api.get("/ranking");
  return response.data;
}


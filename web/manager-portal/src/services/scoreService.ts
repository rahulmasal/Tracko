import api from './api';

export interface ScoreCard {
  id: number;
  userId: number;
  month: number;
  year: number;
  attendanceScore: number;
  punctualityScore: number;
  visitCompletionScore: number;
  reportQualityScore: number;
  enquiryResolutionScore: number;
  customerFeedbackScore: number;
  totalScore: number;
  grade: string;
  managerReviewScore: number | null;
  managerReviewNotes: string | null;
  scoreData: Record<string, unknown>;
  user: {
    id: number;
    firstName: string;
    lastName: string;
    employeeId: string;
    designation: string;
  };
}

export async function getScorecards(params: {
  month?: number;
  year?: number;
  userId?: number;
  page?: number;
  size?: number;
}) {
  const response = await api.get('/scorecards', { params });
  return response.data;
}

export async function getScorecardById(id: number): Promise<ScoreCard> {
  const response = await api.get(`/scorecards/${id}`);
  return response.data;
}

export async function getTeamRanking(params: { month: number; year: number }) {
  const response = await api.get('/scorecards/team-ranking', { params });
  return response.data;
}

export async function saveManagerReview(id: number, data: {
  managerReviewScore: number;
  managerReviewNotes: string;
}) {
  const response = await api.put(`/scorecards/${id}/review`, data);
  return response.data;
}

export async function getPreviousMonthComparison(userId: number, month: number, year: number) {
  const response = await api.get('/scorecards/comparison', { params: { userId, month, year } });
  return response.data;
}

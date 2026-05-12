import api from './api';

export interface DashboardData {
  attendanceSummary: {
    present: number;
    late: number;
    absent: number;
    leave: number;
    total: number;
  };
  missedCheckins: Array<{
    id: number;
    userId: number;
    userName: string;
    date: string;
    minutes: number;
  }>;
  pendingReports: number;
  urgentReports: number;
  pendingLeaveApprovals: number;
  enquiriesPipeline: {
    new: number;
    quoted: number;
    won: number;
    lost: number;
    total: number;
  };
  quotationsPendingApproval: number;
  teamRanking: Array<{
    userId: number;
    userName: string;
    totalScore: number;
    grade: string;
    trend: 'up' | 'down' | 'stable';
  }>;
  dueFollowUps: Array<{
    id: number;
    enquiryId: number;
    customerName: string;
    notes: string;
    nextFollowupAt: string;
  }>;
  liveLocations: Array<{
    userId: number;
    userName: string;
    latitude: number;
    longitude: number;
    status: string;
    batteryLevel: number;
    lastPing: string;
  }>;
}

export async function getDashboardData(): Promise<DashboardData> {
  const response = await api.get('/dashboard');
  return response.data;
}

export async function getAttendanceTrend(days: number = 30) {
  const response = await api.get('/dashboard/attendance-trend', { params: { days } });
  return response.data;
}

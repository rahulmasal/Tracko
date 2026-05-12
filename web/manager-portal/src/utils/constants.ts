export const APP_NAME = 'Tracko';
export const APP_VERSION = '1.0.0';

export const STATUS_COLORS: Record<string, string> = {
  present: '#22c55e',
  late: '#f59e0b',
  absent: '#ef4444',
  half_day: '#f97316',
  pending: '#f59e0b',
  approved: '#22c55e',
  rejected: '#ef4444',
  submitted: '#3b82f6',
  draft: '#6b7280',
  sent: '#8b5cf6',
  accepted: '#22c55e',
  expired: '#ef4444',
  new: '#3b82f6',
  won: '#22c55e',
  lost: '#ef4444',
  in_progress: '#3b82f6',
  completed: '#22c55e',
  scheduled: '#8b5cf6',
  cancelled: '#ef4444',
  rescheduled: '#f97316',
};

export const LEAVE_TYPE_LABELS: Record<string, string> = {
  casual: 'Casual Leave',
  sick: 'Sick Leave',
  earned: 'Earned Leave',
  unpaid: 'Unpaid Leave',
  maternity: 'Maternity Leave',
  paternity: 'Paternity Leave',
  other: 'Other',
};

export const LEAVE_TYPE_COLORS: Record<string, string> = {
  casual: '#22c55e',
  sick: '#ef4444',
  earned: '#3b82f6',
  unpaid: '#6b7280',
  maternity: '#ec4899',
  paternity: '#8b5cf6',
  other: '#f59e0b',
};

export const ENQUIRY_PRIORITY_COLORS: Record<string, string> = {
  low: '#6b7280',
  medium: '#3b82f6',
  high: '#f59e0b',
  urgent: '#ef4444',
};

export const SIDEBAR_WIDTH = 260;
export const SIDEBAR_COLLAPSED_WIDTH = 64;

export const PAGINATION = {
  pageSize: 20,
  pageSizeOptions: [10, 20, 50, 100],
};

export const MAP_CONFIG = {
  defaultZoom: 12,
  defaultCenter: [19.07609, 72.87743] as [number, number],
  tileUrl: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
  attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
};

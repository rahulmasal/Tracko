import { format, formatDistanceToNow, parseISO } from 'date-fns';

export function formatDate(date: string | Date | null | undefined, fmt: string = 'dd MMM yyyy'): string {
  if (!date) return '-';
  const d = typeof date === 'string' ? parseISO(date) : date;
  return format(d, fmt);
}

export function formatDateTime(date: string | Date | null | undefined): string {
  if (!date) return '-';
  const d = typeof date === 'string' ? parseISO(date) : date;
  return format(d, 'dd MMM yyyy, hh:mm a');
}

export function formatTime(date: string | Date | null | undefined): string {
  if (!date) return '-';
  const d = typeof date === 'string' ? parseISO(date) : date;
  return format(d, 'hh:mm a');
}

export function timeAgo(date: string | Date | null | undefined): string {
  if (!date) return '-';
  const d = typeof date === 'string' ? parseISO(date) : date;
  return formatDistanceToNow(d, { addSuffix: true });
}

export function formatCurrency(amount: number | null | undefined): string {
  if (amount == null) return '₹0.00';
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(amount);
}

export function formatPhone(phone: string | null | undefined): string {
  if (!phone) return '-';
  const cleaned = phone.replace(/\D/g, '');
  if (cleaned.length === 10) {
    return `${cleaned.slice(0, 5)} ${cleaned.slice(5)}`;
  }
  if (cleaned.length === 12) {
    return `+${cleaned.slice(0, 2)} ${cleaned.slice(2, 7)} ${cleaned.slice(7)}`;
  }
  return phone;
}

export function formatPercentage(value: number | null | undefined): string {
  if (value == null) return '-';
  return `${value.toFixed(1)}%`;
}

export function truncate(str: string | null | undefined, length: number = 50): string {
  if (!str) return '-';
  return str.length > length ? `${str.substring(0, length)}...` : str;
}

export function getStatusColor(status: string): 'success' | 'warning' | 'error' | 'info' | 'default' {
  const statusMap: Record<string, 'success' | 'warning' | 'error' | 'info' | 'default'> = {
    present: 'success',
    late: 'warning',
    absent: 'error',
    half_day: 'warning',
    completed: 'success',
    in_progress: 'info',
    scheduled: 'info',
    cancelled: 'error',
    pending: 'warning',
    approved: 'success',
    rejected: 'error',
    submitted: 'info',
    draft: 'default',
    sent: 'info',
    accepted: 'success',
    expired: 'error',
    new: 'info',
    won: 'success',
    lost: 'error',
    active: 'success',
    inactive: 'default',
  };
  return statusMap[status] || 'default';
}

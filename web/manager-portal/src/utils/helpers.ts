import { format, parseISO, differenceInMinutes, startOfMonth, endOfMonth } from 'date-fns';
import { STATUS_COLORS, LEAVE_TYPE_LABELS, LEAVE_TYPE_COLORS, ENQUIRY_PRIORITY_COLORS } from './constants';

export function getStatusColor(status: string): string {
  return STATUS_COLORS[status.toLowerCase()] || '#6b7280';
}

export function getLeaveTypeLabel(type: string): string {
  return LEAVE_TYPE_LABELS[type] || type;
}

export function getLeaveTypeColor(type: string): string {
  return LEAVE_TYPE_COLORS[type] || '#6b7280';
}

export function getPriorityColor(priority: string): string {
  return ENQUIRY_PRIORITY_COLORS[priority.toLowerCase()] || '#6b7280';
}

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

export function formatCurrency(amount: number | null | undefined): string {
  if (amount == null) return '₹0.00';
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(amount);
}

export function getMonthRange(date: Date = new Date()) {
  return {
    start: startOfMonth(date),
    end: endOfMonth(date),
  };
}

export function calculateDuration(minutes: number): string {
  const hrs = Math.floor(minutes / 60);
  const mins = minutes % 60;
  if (hrs > 0) return `${hrs}h ${mins}m`;
  return `${mins}m`;
}

export function getInitials(firstName: string, lastName?: string): string {
  return `${firstName.charAt(0)}${lastName ? lastName.charAt(0) : ''}`.toUpperCase();
}

export function getFullName(firstName: string, lastName?: string): string {
  return `${firstName}${lastName ? ` ${lastName}` : ''}`;
}

export function daysBetween(start: string, end: string): number {
  const s = parseISO(start);
  const e = parseISO(end);
  return differenceInMinutes(e, s) / (60 * 24) + 1;
}

export function calculateAge(days: number): string {
  if (days < 1) return 'Today';
  if (days === 1) return '1 day';
  if (days < 7) return `${days} days`;
  if (days < 30) return `${Math.floor(days / 7)}w`;
  return `${Math.floor(days / 30)}mo`;
}

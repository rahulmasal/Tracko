import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

export const userSchema = z.object({
  employeeId: z.string().min(1, 'Employee ID is required'),
  email: z.string().email('Invalid email address'),
  phone: z.string().optional(),
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().optional(),
  designation: z.string().optional(),
  departmentId: z.number().optional(),
  branchId: z.number().optional(),
  shiftId: z.number().optional(),
  reportingManagerId: z.number().optional(),
  isActive: z.boolean().default(true),
});

export const branchSchema = z.object({
  name: z.string().min(1, 'Branch name is required'),
  code: z.string().min(1, 'Branch code is required'),
  address: z.string().optional(),
  city: z.string().optional(),
  state: z.string().optional(),
  pincode: z.string().optional(),
  latitude: z.number().min(-90).max(90).optional(),
  longitude: z.number().min(-180).max(180).optional(),
  radiusMeters: z.number().min(10).default(100),
});

export const shiftSchema = z.object({
  name: z.string().min(1, 'Shift name is required'),
  startTime: z.string().min(1, 'Start time is required'),
  endTime: z.string().min(1, 'End time is required'),
  graceMinutes: z.number().min(0).default(15),
  halfDayMinutes: z.number().min(0).default(240),
  isNightShift: z.boolean().default(false),
});

export const enquirySchema = z.object({
  customerId: z.number().min(1, 'Customer is required'),
  assignedTo: z.number().optional(),
  subject: z.string().min(1, 'Subject is required'),
  description: z.string().optional(),
  priority: z.enum(['low', 'medium', 'high', 'urgent']).default('medium'),
  source: z.string().optional(),
});

export const quotationSchema = z.object({
  customerId: z.number().min(1, 'Customer is required'),
  enquiryId: z.number().optional(),
  subject: z.string().min(1, 'Subject is required'),
  commercialTerms: z.string().optional(),
  validityDays: z.number().min(1).default(30),
  remarks: z.string().optional(),
  items: z.array(z.object({
    itemDescription: z.string().min(1, 'Description is required'),
    quantity: z.number().min(0.01, 'Quantity must be > 0'),
    unitPrice: z.number().min(0, 'Unit price must be >= 0'),
    discountPercent: z.number().min(0).max(100).default(0),
    taxPercent: z.number().min(0).max(100).default(0),
  })).min(1, 'At least one item is required'),
});

export const leaveRequestSchema = z.object({
  leaveType: z.enum(['casual', 'sick', 'earned', 'unpaid', 'maternity', 'paternity', 'other']),
  startDate: z.string().min(1, 'Start date is required'),
  endDate: z.string().min(1, 'End date is required'),
  reason: z.string().min(1, 'Reason is required'),
  isHalfDay: z.boolean().default(false),
  emergencyContact: z.string().optional(),
}).refine((data) => data.endDate >= data.startDate, {
  message: 'End date must be after start date',
  path: ['endDate'],
});

export const reportReviewSchema = z.object({
  status: z.enum(['approved', 'rework_requested']),
  managerRemarks: z.string().min(1, 'Remarks are required'),
});

export const passwordChangeSchema = z.object({
  currentPassword: z.string().min(1, 'Current password is required'),
  newPassword: z.string().min(6, 'New password must be at least 6 characters'),
  confirmPassword: z.string().min(1, 'Please confirm your password'),
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: 'Passwords do not match',
  path: ['confirmPassword'],
});

export type LoginFormData = z.infer<typeof loginSchema>;
export type UserFormData = z.infer<typeof userSchema>;
export type BranchFormData = z.infer<typeof branchSchema>;
export type ShiftFormData = z.infer<typeof shiftSchema>;
export type EnquiryFormData = z.infer<typeof enquirySchema>;
export type QuotationFormData = z.infer<typeof quotationSchema>;
export type LeaveRequestFormData = z.infer<typeof leaveRequestSchema>;
export type ReportReviewFormData = z.infer<typeof reportReviewSchema>;
export type PasswordChangeFormData = z.infer<typeof passwordChangeSchema>;

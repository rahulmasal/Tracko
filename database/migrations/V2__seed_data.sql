-- V2: Seed Data for Tracko
-- Default roles, permissions, admin user, shifts, departments, config

-- =============================================================
-- DEFAULT ROLES
-- =============================================================

INSERT INTO roles (name, description, is_system) VALUES
('ADMIN', 'System Administrator with full access', TRUE),
('MANAGER', 'Branch/Department Manager', TRUE),
('EMPLOYEE', 'Field Engineer', TRUE);

-- =============================================================
-- PERMISSIONS
-- =============================================================

INSERT INTO permissions (code, name, module, description) VALUES
-- Attendance
('ATTENDANCE_READ', 'View Attendance', 'ATTENDANCE', 'View attendance records'),
('ATTENDANCE_WRITE', 'Mark Attendance', 'ATTENDANCE', 'Mark check-in/out'),
('ATTENDANCE_APPROVE', 'Approve Corrections', 'ATTENDANCE', 'Approve attendance correction requests'),
('ATTENDANCE_MANAGE', 'Manage Attendance Config', 'ATTENDANCE', 'Configure attendance policies'),
-- Users
('USER_READ', 'View Users', 'USER', 'View user profiles'),
('USER_CREATE', 'Create Users', 'USER', 'Create new users'),
('USER_UPDATE', 'Update Users', 'USER', 'Edit user details'),
('USER_DELETE', 'Delete Users', 'USER', 'Delete deactivated users'),
('USER_MANAGE_ROLES', 'Manage Roles', 'USER', 'Assign roles and permissions'),
-- Visits
('VISIT_READ', 'View Visits', 'VISIT', 'View visit records'),
('VISIT_WRITE', 'Create Visits', 'VISIT', 'Create and manage visits'),
('VISIT_APPROVE', 'Approve Visits', 'VISIT', 'Approve visit schedules'),
-- Reports
('REPORT_READ', 'View Reports', 'REPORT', 'View call reports'),
('REPORT_WRITE', 'Create Reports', 'REPORT', 'Create and submit reports'),
('REPORT_APPROVE', 'Approve Reports', 'REPORT', 'Approve or request rework on reports'),
-- Enquiries
('ENQUIRY_READ', 'View Enquiries', 'ENQUIRY', 'View enquiries'),
('ENQUIRY_WRITE', 'Create/Edit Enquiries', 'ENQUIRY', 'Create and edit enquiries'),
('ENQUIRY_ASSIGN', 'Assign Enquiries', 'ENQUIRY', 'Reassign enquiries to engineers'),
('ENQUIRY_CLOSE', 'Close Enquiries', 'ENQUIRY', 'Mark enquiries as won/lost'),
-- Quotations
('QUOTATION_READ', 'View Quotations', 'QUOTATION', 'View quotations'),
('QUOTATION_WRITE', 'Create Quotations', 'QUOTATION', 'Create and edit quotations'),
('QUOTATION_APPROVE', 'Approve Quotations', 'QUOTATION', 'Approve quotations for sending'),
('QUOTATION_SEND', 'Send Quotations', 'QUOTATION', 'Send quotations to customer'),
-- Leaves
('LEAVE_READ', 'View Leaves', 'LEAVE', 'View leave requests'),
('LEAVE_WRITE', 'Apply Leave', 'LEAVE', 'Apply for leave'),
('LEAVE_APPROVE', 'Approve Leaves', 'LEAVE', 'Approve or reject leave requests'),
('LEAVE_MANAGE', 'Manage Leave Policy', 'LEAVE', 'Configure leave policies'),
-- Dashboard
('DASHBOARD_READ', 'View Dashboard', 'DASHBOARD', 'View dashboard analytics'),
('DASHBOARD_MANAGE', 'Manage Dashboard', 'DASHBOARD', 'Configure dashboard widgets'),
-- Config
('CONFIG_READ', 'View Configuration', 'CONFIG', 'View system configuration'),
('CONFIG_WRITE', 'Update Configuration', 'CONFIG', 'Update system configuration'),
-- Audit
('AUDIT_READ', 'View Audit Logs', 'AUDIT', 'View audit trail'),
-- Security
('SECURITY_READ', 'View Security Events', 'SECURITY', 'View security event logs'),
('SECURITY_MANAGE', 'Manage Security', 'SECURITY', 'Configure security policies and resolve events'),
-- Tracking
('TRACKING_READ', 'View Tracking', 'TRACKING', 'View real-time location tracking'),
-- Scorecard
('SCORE_READ', 'View Scorecards', 'SCORE', 'View performance scorecards'),
('SCORE_MANAGE', 'Manage Scorecards', 'SCORE', 'Review and input manager scores'),
-- Branches
('BRANCH_READ', 'View Branches', 'BRANCH', 'View branch details'),
('BRANCH_WRITE', 'Manage Branches', 'BRANCH', 'Create/edit branches'),
-- Shifts
('SHIFT_READ', 'View Shifts', 'SHIFT', 'View shift definitions'),
('SHIFT_WRITE', 'Manage Shifts', 'SHIFT', 'Create/edit shifts'),
-- Reports (analytics)
('REPORTS_VIEW', 'View Analytics Reports', 'REPORTS', 'View analytical reports'),
('REPORTS_EXPORT', 'Export Reports', 'REPORTS', 'Export reports to Excel/PDF');

-- =============================================================
-- ROLE PERMISSIONS: ADMIN (all permissions)
-- =============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'ADMIN'), id FROM permissions;

-- =============================================================
-- ROLE PERMISSIONS: MANAGER
-- =============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'MANAGER'), id FROM permissions
WHERE code IN (
    'ATTENDANCE_READ', 'ATTENDANCE_APPROVE',
    'USER_READ',
    'VISIT_READ', 'VISIT_APPROVE',
    'REPORT_READ', 'REPORT_APPROVE',
    'ENQUIRY_READ', 'ENQUIRY_WRITE', 'ENQUIRY_ASSIGN', 'ENQUIRY_CLOSE',
    'QUOTATION_READ', 'QUOTATION_WRITE', 'QUOTATION_APPROVE', 'QUOTATION_SEND',
    'LEAVE_READ', 'LEAVE_APPROVE',
    'DASHBOARD_READ',
    'TRACKING_READ',
    'SCORE_READ', 'SCORE_MANAGE',
    'BRANCH_READ',
    'SHIFT_READ',
    'REPORTS_VIEW', 'REPORTS_EXPORT'
);

-- =============================================================
-- ROLE PERMISSIONS: EMPLOYEE
-- =============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'EMPLOYEE'), id FROM permissions
WHERE code IN (
    'ATTENDANCE_READ', 'ATTENDANCE_WRITE',
    'VISIT_READ', 'VISIT_WRITE',
    'REPORT_READ', 'REPORT_WRITE',
    'ENQUIRY_READ',
    'QUOTATION_READ',
    'LEAVE_READ', 'LEAVE_WRITE',
    'DASHBOARD_READ'
);

-- =============================================================
-- DEFAULT ADMIN USER
-- Password: Admin@123 (BCrypt hash)
-- =============================================================

INSERT INTO users (employee_id, email, phone, password_hash, first_name, last_name, designation, is_active, is_phone_verified, is_email_verified)
VALUES (
    'ADM-001',
    'admin@tracko.com',
    '+91-9876543210',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'System',
    'Admin',
    'System Administrator',
    TRUE,
    TRUE,
    TRUE
);

-- Assign ADMIN role to admin user
INSERT INTO user_role_map (user_id, role_id)
VALUES (
    (SELECT id FROM users WHERE email = 'admin@tracko.com'),
    (SELECT id FROM roles WHERE name = 'ADMIN')
);

-- =============================================================
-- DEFAULT SHIFTS
-- =============================================================

INSERT INTO shifts (name, start_time, end_time, grace_minutes, half_day_minutes, description) VALUES
('General', '09:00', '18:00', 15, 240, 'General day shift 9 AM to 6 PM'),
('Morning', '08:00', '17:00', 15, 240, 'Morning shift 8 AM to 5 PM'),
('Evening', '14:00', '23:00', 15, 240, 'Evening shift 2 PM to 11 PM');

-- =============================================================
-- DEFAULT DEPARTMENTS
-- =============================================================

INSERT INTO departments (name, code, description) VALUES
('IT Services', 'IT-SVC', 'IT support and services department'),
('Network', 'NET', 'Network infrastructure and support'),
('Hardware', 'HW', 'Hardware installation and maintenance'),
('Software', 'SW', 'Software development and support');

-- =============================================================
-- DEFAULT BRANCH
-- =============================================================

INSERT INTO branches (name, code, address, city, state, latitude, longitude, radius_meters) VALUES
('Head Office', 'HO', '123 Business Park, Main Street', 'Mumbai', 'Maharashtra', 19.076090, 72.877426, 200);

-- =============================================================
-- APP CONFIG
-- =============================================================

INSERT INTO app_config (config_key, config_value, config_type, description, module) VALUES
-- Attendance
('attendance.grace_minutes', '15', 'number', 'Default grace period in minutes for check-in', 'ATTENDANCE'),
('attendance.half_day_threshold_minutes', '240', 'number', 'Minutes late after which marked as half day', 'ATTENDANCE'),
('attendance.auto_missed_checkout', 'true', 'boolean', 'Auto-mark missed checkout as absent', 'ATTENDANCE'),
('attendance.missed_checkout_auto_minutes', '120', 'number', 'Minutes after shift end to auto-flag missed checkout', 'ATTENDANCE'),
('attendance.require_photo', 'true', 'boolean', 'Require selfie for check-in/out', 'ATTENDANCE'),
-- Tracking
('tracking.ping_interval_seconds', '30', 'number', 'Location ping interval in seconds', 'TRACKING'),
('tracking.missed_ping_threshold_minutes', '5', 'number', 'Minutes without ping to trigger alert', 'TRACKING'),
('tracking.enable_geofence', 'true', 'boolean', 'Enable geofence validation for attendance', 'TRACKING'),
-- Security
('security.risk_threshold_low', '30', 'number', 'Low risk score threshold', 'SECURITY'),
('security.risk_threshold_medium', '60', 'number', 'Medium risk score threshold', 'SECURITY'),
('security.risk_threshold_high', '80', 'number', 'High risk score threshold', 'SECURITY'),
('security.rooted_device_action', 'warn', 'string', 'Action for rooted devices: warn/block/log', 'SECURITY'),
('security.mock_location_action', 'block', 'string', 'Action for mock location: warn/block/log', 'SECURITY'),
('security.dev_options_action', 'warn', 'string', 'Action for developer options enabled', 'SECURITY'),
-- Scorecard
('score.weight_attendance', '20', 'number', 'Weight for attendance score parameter', 'SCORE'),
('score.weight_punctuality', '15', 'number', 'Weight for punctuality score parameter', 'SCORE'),
('score.weight_visits', '25', 'number', 'Weight for visit completion score parameter', 'SCORE'),
('score.weight_report_quality', '20', 'number', 'Weight for report quality score parameter', 'SCORE'),
('score.weight_enquiry_resolution', '10', 'number', 'Weight for enquiry resolution score parameter', 'SCORE'),
('score.weight_customer_feedback', '10', 'number', 'Weight for customer feedback score parameter', 'SCORE'),
-- Quotation
('quotation.sla_hours', '48', 'number', 'SLA hours for quotation creation', 'QUOTATION'),
('quotation.requires_approval', 'true', 'boolean', 'Quotations above threshold require approval', 'QUOTATION'),
('quotation.approval_threshold', '50000', 'number', 'Amount above which approval is required', 'QUOTATION'),
('quotation.auto_expire_days', '30', 'number', 'Days after which sent quotation auto-expires', 'QUOTATION'),
-- Leaves
('leave.policy_requires_attachment', 'false', 'boolean', 'Require attachment for leave applications', 'LEAVE'),
('leave.max_consecutive_days', '15', 'number', 'Maximum consecutive leave days', 'LEAVE'),
('leave.min_days_notice', '1', 'number', 'Minimum days notice for leave application', 'LEAVE');

-- =============================================================
-- DEFAULT LEAVE BALANCE POLICIES
-- =============================================================

INSERT INTO app_config (config_key, config_value, config_type, description, module) VALUES
('leave.allocation.casual', '12', 'number', 'Annual casual leave allocation', 'LEAVE'),
('leave.allocation.sick', '10', 'number', 'Annual sick leave allocation', 'LEAVE'),
('leave.allocation.earned', '15', 'number', 'Annual earned leave allocation', 'LEAVE'),
('leave.allocation.unpaid', '-1', 'number', 'Unpaid leave (-1 = unlimited)', 'LEAVE'),
('leave.carry_forward.casual', '0', 'number', 'Casual leave carry forward limit', 'LEAVE'),
('leave.carry_forward.sick', '0', 'number', 'Sick leave carry forward limit', 'LEAVE'),
('leave.carry_forward.earned', '15', 'number', 'Earned leave carry forward limit', 'LEAVE');

-- =============================================================
-- DEFAULT GEOFENCE (Head Office)
-- =============================================================

INSERT INTO geofences (name, type, latitude, longitude, radius_meters, branch_id, address)
SELECT 'Head Office Geofence', 'branch', 19.076090, 72.877426, 200, id, '123 Business Park, Main Street, Mumbai'
FROM branches WHERE code = 'HO';

-- =============================================================
-- DEFAULT SCORE CARD GRADES CONFIG
-- =============================================================

INSERT INTO app_config (config_key, config_value, config_type, description, module) VALUES
('score.grade.a_plus_min', '95', 'number', 'Minimum score for A+ grade', 'SCORE'),
('score.grade.a_min', '85', 'number', 'Minimum score for A grade', 'SCORE'),
('score.grade.b_plus_min', '75', 'number', 'Minimum score for B+ grade', 'SCORE'),
('score.grade.b_min', '65', 'number', 'Minimum score for B grade', 'SCORE'),
('score.grade.c_min', '50', 'number', 'Minimum score for C grade', 'SCORE');

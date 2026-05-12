-- V1: Initial Schema for Tracko Field Engineer Management System
-- PostgreSQL 15 Migration

-- =============================================================
-- ENUMS
-- =============================================================

CREATE TYPE attendance_status AS ENUM ('present', 'late', 'absent', 'half_day', 'leave', 'holiday');
CREATE TYPE visit_status AS ENUM ('scheduled', 'in_progress', 'completed', 'cancelled', 'rescheduled');
CREATE TYPE report_status AS ENUM ('draft', 'submitted', 'approved', 'rework_requested', 'rework_submitted');
CREATE TYPE enquiry_status AS ENUM ('new', 'contacted', 'quoted', 'negotiation', 'won', 'lost', 'on_hold');
CREATE TYPE quote_status AS ENUM ('draft', 'pending_approval', 'approved', 'sent', 'accepted', 'rejected', 'expired', 'revised');
CREATE TYPE leave_type AS ENUM ('casual', 'sick', 'earned', 'unpaid', 'maternity', 'paternity', 'other');
CREATE TYPE leave_status AS ENUM ('pending', 'approved', 'rejected', 'cancelled');
CREATE TYPE notification_type AS ENUM ('attendance', 'visit', 'report', 'leave', 'enquiry', 'quotation', 'system', 'approval');
CREATE TYPE security_event_type AS ENUM ('root_detected', 'mock_location', 'developer_options', 'emulator', 'device_tampered', 'suspicious_app', 'unknown');
CREATE TYPE geofence_type AS ENUM ('branch', 'site', 'restricted');

-- =============================================================
-- FUNCTION: updated_at trigger
-- =============================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =============================================================
-- TABLE: roles
-- =============================================================

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE roles IS 'System roles for RBAC';
COMMENT ON COLUMN roles.name IS 'Role name (ADMIN, MANAGER, EMPLOYEE)';

CREATE TRIGGER trg_roles_updated_at BEFORE UPDATE ON roles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: permissions
-- =============================================================

CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    module VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE permissions IS 'Individual permission flags for each action';
COMMENT ON COLUMN permissions.code IS 'Permission code like ATTENDANCE_READ, USER_CREATE';
COMMENT ON COLUMN permissions.module IS 'Module grouping: ATTENDANCE, USER, REPORT, ENQUIRY, QUOTATION, LEAVE, CONFIG';

CREATE INDEX idx_permissions_module ON permissions(module);

-- =============================================================
-- TABLE: role_permissions
-- =============================================================

CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (role_id, permission_id)
);
COMMENT ON TABLE role_permissions IS 'Many-to-many mapping of roles to permissions';

CREATE INDEX idx_role_permissions_permission ON role_permissions(permission_id);

-- =============================================================
-- TABLE: departments
-- =============================================================

CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE departments IS 'Organization departments';

CREATE TRIGGER trg_departments_updated_at BEFORE UPDATE ON departments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: branches
-- =============================================================

CREATE TABLE branches (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100) DEFAULT 'India',
    pincode VARCHAR(10),
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    radius_meters INTEGER DEFAULT 100,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE branches IS 'Physical branch/office locations with geofence data';
COMMENT ON COLUMN branches.latitude IS 'Branch center latitude for geofencing';
COMMENT ON COLUMN branches.radius_meters IS 'Geofence radius in meters';

CREATE INDEX idx_branches_city ON branches(city);
CREATE INDEX idx_branches_is_active ON branches(is_active);

CREATE TRIGGER trg_branches_updated_at BEFORE UPDATE ON branches FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: shifts
-- =============================================================

CREATE TABLE shifts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    grace_minutes INTEGER NOT NULL DEFAULT 15,
    half_day_minutes INTEGER NOT NULL DEFAULT 240,
    is_night_shift BOOLEAN NOT NULL DEFAULT FALSE,
    description VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE shifts IS 'Work shift definitions';

CREATE TRIGGER trg_shifts_updated_at BEFORE UPDATE ON shifts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: holidays
-- =============================================================

CREATE TABLE holidays (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    date DATE NOT NULL,
    is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
    branch_id BIGINT REFERENCES branches(id) ON DELETE CASCADE,
    is_optional BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(date, name)
);
COMMENT ON TABLE holidays is 'Public holidays and optional holidays';

CREATE INDEX idx_holidays_date ON holidays(date);

-- =============================================================
-- TABLE: users
-- =============================================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    designation VARCHAR(100),
    department_id BIGINT REFERENCES departments(id) ON DELETE SET NULL,
    branch_id BIGINT REFERENCES branches(id) ON DELETE SET NULL,
    shift_id BIGINT REFERENCES shifts(id) ON DELETE SET NULL,
    reporting_manager_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    profile_picture_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    fcm_token VARCHAR(500),
    last_login_at TIMESTAMP,
    last_ping_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE users IS 'All system users (employees, managers, admins)';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hash of password';
COMMENT ON COLUMN users.reporting_manager_id IS 'Direct manager for hierarchical reporting';

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_employee_id ON users(employee_id);
CREATE INDEX idx_users_branch ON users(branch_id);
CREATE INDEX idx_users_department ON users(department_id);
CREATE INDEX idx_users_manager ON users(reporting_manager_id);
CREATE INDEX idx_users_is_active ON users(is_active);

CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: user_role_map
-- =============================================================

CREATE TABLE user_role_map (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, role_id)
);
COMMENT ON TABLE user_role_map IS 'Many-to-many mapping of users to roles';

CREATE INDEX idx_user_role_map_role ON user_role_map(role_id);

-- =============================================================
-- TABLE: attendance
-- =============================================================

CREATE TABLE attendance (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    check_in_latitude DECIMAL(10,7),
    check_in_longitude DECIMAL(10,7),
    check_out_latitude DECIMAL(10,7),
    check_out_longitude DECIMAL(10,7),
    check_in_address TEXT,
    check_out_address TEXT,
    status attendance_status NOT NULL DEFAULT 'absent',
    late_minutes INTEGER DEFAULT 0,
    early_exit_minutes INTEGER DEFAULT 0,
    work_hours DECIMAL(5,2),
    overtime_hours DECIMAL(5,2),
    is_manual_correction BOOLEAN NOT NULL DEFAULT FALSE,
    remarks TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, date)
);
COMMENT ON TABLE attendance is 'Daily attendance records for each user';
COMMENT ON COLUMN attendance.late_minutes IS 'Minutes late beyond grace period';
COMMENT ON COLUMN attendance.work_hours IS 'Total work hours calculated from check-in/out';

CREATE INDEX idx_attendance_user_date ON attendance(user_id, date);
CREATE INDEX idx_attendance_date ON attendance(date);
CREATE INDEX idx_attendance_status ON attendance(status);
CREATE INDEX idx_attendance_user_status ON attendance(user_id, status);

CREATE TRIGGER trg_attendance_updated_at BEFORE UPDATE ON attendance FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: attendance_photos
-- =============================================================

CREATE TABLE attendance_photos (
    id BIGSERIAL PRIMARY KEY,
    attendance_id BIGINT NOT NULL REFERENCES attendance(id) ON DELETE CASCADE,
    photo_url VARCHAR(500) NOT NULL,
    photo_type VARCHAR(20) NOT NULL CHECK (photo_type IN ('check_in', 'check_out')),
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    captured_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE attendance_photos IS 'Check-in/out selfies with geotag';

CREATE INDEX idx_attendance_photos_attendance ON attendance_photos(attendance_id);

-- =============================================================
-- TABLE: attendance_corrections
-- =============================================================

CREATE TABLE attendance_corrections (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    attendance_id BIGINT REFERENCES attendance(id) ON DELETE CASCADE,
    correction_type VARCHAR(20) NOT NULL CHECK (correction_type IN ('missed_checkin', 'missed_checkout', 'wrong_status', 'other')),
    requested_date DATE NOT NULL,
    expected_check_in TIME,
    expected_check_out TIME,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected')),
    reviewed_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    review_remarks TEXT,
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE attendance_corrections IS 'Correction requests for missed/faulty attendance';

CREATE INDEX idx_attendance_corrections_user ON attendance_corrections(user_id);
CREATE INDEX idx_attendance_corrections_status ON attendance_corrections(status);

CREATE TRIGGER trg_attendance_corrections_updated_at BEFORE UPDATE ON attendance_corrections FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: tracking_logs
-- =============================================================

CREATE TABLE tracking_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    accuracy DECIMAL(5,2),
    battery_level INTEGER,
    speed DECIMAL(5,2),
    is_mock_location BOOLEAN NOT NULL DEFAULT FALSE,
    is_within_geofence BOOLEAN,
    location_address TEXT,
    device_info JSONB,
    pinged_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE tracking_logs IS 'Real-time location tracking pings from field engineers';
COMMENT ON COLUMN tracking_logs.device_info IS 'JSON with device model, OS version, app version, network type';

CREATE INDEX idx_tracking_logs_user_time ON tracking_logs(user_id, pinged_at DESC);
CREATE INDEX idx_tracking_logs_pinged_at ON tracking_logs(pinged_at);
CREATE INDEX idx_tracking_logs_mock ON tracking_logs(is_mock_location);

-- =============================================================
-- TABLE: missed_pings
-- =============================================================

CREATE TABLE missed_pings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expected_ping_time TIMESTAMP NOT NULL,
    last_known_latitude DECIMAL(10,7),
    last_known_longitude DECIMAL(10,7),
    duration_minutes INTEGER NOT NULL DEFAULT 0,
    is_alert_sent BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE missed_pings IS 'Detected missed location pings for safety monitoring';

CREATE INDEX idx_missed_pings_user ON missed_pings(user_id);
CREATE INDEX idx_missed_pings_resolved ON missed_pings(resolved_at);

-- =============================================================
-- TABLE: customer_master
-- =============================================================

CREATE TABLE customer_master (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(200) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(20),
    mobile VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    gst_number VARCHAR(20),
    website VARCHAR(255),
    industry VARCHAR(100),
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE customer_master IS 'Customer master data';

CREATE INDEX idx_customer_master_company ON customer_master(company_name);
CREATE INDEX idx_customer_master_city ON customer_master(city);
CREATE INDEX idx_customer_master_phone ON customer_master(phone);
CREATE INDEX idx_customer_master_active ON customer_master(is_active);

CREATE TRIGGER trg_customer_master_updated_at BEFORE UPDATE ON customer_master FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: visits
-- =============================================================

CREATE TABLE visits (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    customer_id BIGINT NOT NULL REFERENCES customer_master(id) ON DELETE CASCADE,
    visit_type VARCHAR(50) NOT NULL,
    status visit_status NOT NULL DEFAULT 'scheduled',
    scheduled_at TIMESTAMP,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    check_in_latitude DECIMAL(10,7),
    check_in_longitude DECIMAL(10,7),
    check_out_latitude DECIMAL(10,7),
    check_out_longitude DECIMAL(10,7),
    duration_minutes INTEGER,
    purpose TEXT,
    remarks TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE visits IS 'Field visit records for customer site visits';
COMMENT ON COLUMN visits.visit_type IS 'e.g., Installation, Maintenance, Repair, Survey, Support';

CREATE INDEX idx_visits_user ON visits(user_id);
CREATE INDEX idx_visits_customer ON visits(customer_id);
CREATE INDEX idx_visits_status ON visits(status);
CREATE INDEX idx_visits_scheduled ON visits(scheduled_at);
CREATE INDEX idx_visits_user_date ON visits(user_id, scheduled_at);

CREATE TRIGGER trg_visits_updated_at BEFORE UPDATE ON visits FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: visit_photos
-- =============================================================

CREATE TABLE visit_photos (
    id BIGSERIAL PRIMARY KEY,
    visit_id BIGINT NOT NULL REFERENCES visits(id) ON DELETE CASCADE,
    photo_url VARCHAR(500) NOT NULL,
    photo_type VARCHAR(20) NOT NULL CHECK (photo_type IN ('check_in', 'check_out', 'work_image', 'signature')),
    caption VARCHAR(255),
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    captured_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE visit_photos IS 'Photos captured during field visits';

CREATE INDEX idx_visit_photos_visit ON visit_photos(visit_id);

-- =============================================================
-- TABLE: call_reports
-- =============================================================

CREATE TABLE call_reports (
    id BIGSERIAL PRIMARY KEY,
    visit_id BIGINT REFERENCES visits(id) ON DELETE SET NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    customer_id BIGINT NOT NULL REFERENCES customer_master(id) ON DELETE CASCADE,
    report_date DATE NOT NULL,
    visit_type VARCHAR(50),
    work_description TEXT NOT NULL,
    findings TEXT,
    recommendations TEXT,
    materials_used JSONB,
    is_chargeable BOOLEAN NOT NULL DEFAULT FALSE,
    charge_amount DECIMAL(12,2),
    customer_remarks TEXT,
    customer_signature_url VARCHAR(500),
    status report_status NOT NULL DEFAULT 'draft',
    submitted_at TIMESTAMP,
    approved_at TIMESTAMP,
    approved_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    manager_remarks TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE call_reports IS 'Service call reports / job completion reports';
COMMENT ON COLUMN call_reports.materials_used IS 'JSON array of materials used: [{name, qty, unit, cost}]';

CREATE INDEX idx_call_reports_user ON call_reports(user_id);
CREATE INDEX idx_call_reports_customer ON call_reports(customer_id);
CREATE INDEX idx_call_reports_status ON call_reports(status);
CREATE INDEX idx_call_reports_date ON call_reports(report_date);
CREATE INDEX idx_call_reports_visit ON call_reports(visit_id);

CREATE TRIGGER trg_call_reports_updated_at BEFORE UPDATE ON call_reports FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: call_report_photos
-- =============================================================

CREATE TABLE call_report_photos (
    id BIGSERIAL PRIMARY KEY,
    call_report_id BIGINT NOT NULL REFERENCES call_reports(id) ON DELETE CASCADE,
    photo_url VARCHAR(500) NOT NULL,
    caption VARCHAR(255),
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    captured_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE call_report_photos IS 'Photos attached to call reports';

CREATE INDEX idx_call_report_photos_report ON call_report_photos(call_report_id);

-- =============================================================
-- TABLE: enquiries
-- =============================================================

CREATE TABLE enquiries (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customer_master(id) ON DELETE CASCADE,
    assigned_to BIGINT REFERENCES users(id) ON DELETE SET NULL,
    subject VARCHAR(300) NOT NULL,
    description TEXT,
    priority VARCHAR(20) NOT NULL DEFAULT 'medium' CHECK (priority IN ('low', 'medium', 'high', 'urgent')),
    source VARCHAR(50),
    status enquiry_status NOT NULL DEFAULT 'new',
    expected_close_date DATE,
    closed_at TIMESTAMP,
    closed_reason VARCHAR(100),
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE enquiries IS 'Customer enquiries/support tickets';
COMMENT ON COLUMN enquiries.source IS 'Phone, Email, Website, Walk-in, Referral';

CREATE INDEX idx_enquiries_customer ON enquiries(customer_id);
CREATE INDEX idx_enquiries_assigned ON enquiries(assigned_to);
CREATE INDEX idx_enquiries_status ON enquiries(status);
CREATE INDEX idx_enquiries_priority ON enquiries(priority);
CREATE INDEX idx_enquiries_created ON enquiries(created_at DESC);

CREATE TRIGGER trg_enquiries_updated_at BEFORE UPDATE ON enquiries FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: enquiry_followups
-- =============================================================

CREATE TABLE enquiry_followups (
    id BIGSERIAL PRIMARY KEY,
    enquiry_id BIGINT NOT NULL REFERENCES enquiries(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    followup_type VARCHAR(50) NOT NULL DEFAULT 'call' CHECK (followup_type IN ('call', 'email', 'visit', 'meeting', 'whatsapp', 'other')),
    notes TEXT NOT NULL,
    next_followup_at TIMESTAMP,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE enquiry_followups IS 'Follow-up actions and notes on enquiries';

CREATE INDEX idx_enquiry_followups_enquiry ON enquiry_followups(enquiry_id);
CREATE INDEX idx_enquiry_followups_next ON enquiry_followups(next_followup_at);
CREATE INDEX idx_enquiry_followups_user ON enquiry_followups(user_id);

-- =============================================================
-- TABLE: quotations
-- =============================================================

CREATE TABLE quotations (
    id BIGSERIAL PRIMARY KEY,
    quote_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES customer_master(id) ON DELETE CASCADE,
    enquiry_id BIGINT REFERENCES enquiries(id) ON DELETE SET NULL,
    created_by BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    subject VARCHAR(300) NOT NULL,
    status quote_status NOT NULL DEFAULT 'draft',
    subtotal DECIMAL(14,2) NOT NULL DEFAULT 0,
    discount_percent DECIMAL(5,2) DEFAULT 0,
    discount_amount DECIMAL(14,2) DEFAULT 0,
    tax_percent DECIMAL(5,2) DEFAULT 0,
    tax_amount DECIMAL(14,2) DEFAULT 0,
    total_amount DECIMAL(14,2) NOT NULL DEFAULT 0,
    commercial_terms TEXT,
    validity_days INTEGER DEFAULT 30,
    valid_until DATE,
    remarks TEXT,
    customer_response VARCHAR(50) CHECK (customer_response IN ('accepted', 'rejected', 'negotiating', 'no_response')),
    customer_response_at TIMESTAMP,
    pdf_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE quotations IS 'Customer quotations with line items';
COMMENT ON COLUMN quotations.quote_number IS 'Auto-generated quote number like QTE-YYYY-XXXX';

CREATE INDEX idx_quotations_customer ON quotations(customer_id);
CREATE INDEX idx_quotations_status ON quotations(status);
CREATE INDEX idx_quotations_created_by ON quotations(created_by);
CREATE INDEX idx_quotations_created ON quotations(created_at DESC);
CREATE INDEX idx_quotations_valid_until ON quotations(valid_until);

CREATE TRIGGER trg_quotations_updated_at BEFORE UPDATE ON quotations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: quotation_items
-- =============================================================

CREATE TABLE quotation_items (
    id BIGSERIAL PRIMARY KEY,
    quotation_id BIGINT NOT NULL REFERENCES quotations(id) ON DELETE CASCADE,
    item_description TEXT NOT NULL,
    quantity DECIMAL(10,2) NOT NULL DEFAULT 1,
    unit_price DECIMAL(14,2) NOT NULL DEFAULT 0,
    discount_percent DECIMAL(5,2) DEFAULT 0,
    discount_amount DECIMAL(14,2) DEFAULT 0,
    tax_percent DECIMAL(5,2) DEFAULT 0,
    tax_amount DECIMAL(14,2) DEFAULT 0,
    net_amount DECIMAL(14,2) NOT NULL DEFAULT 0,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE quotation_items IS 'Line items within a quotation';

CREATE INDEX idx_quotation_items_quote ON quotation_items(quotation_id);

-- =============================================================
-- TABLE: quotation_approvals
-- =============================================================

CREATE TABLE quotation_approvals (
    id BIGSERIAL PRIMARY KEY,
    quotation_id BIGINT NOT NULL REFERENCES quotations(id) ON DELETE CASCADE,
    approved_by BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('pending', 'approved', 'rejected')),
    remarks TEXT,
    decided_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE quotation_approvals IS 'Approval chain for quotations requiring manager approval';

CREATE INDEX idx_quotation_approvals_quote ON quotation_approvals(quotation_id);

-- =============================================================
-- TABLE: leave_requests
-- =============================================================

CREATE TABLE leave_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    leave_type leave_type NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days DECIMAL(4,1) NOT NULL,
    reason TEXT NOT NULL,
    status leave_status NOT NULL DEFAULT 'pending',
    is_half_day BOOLEAN NOT NULL DEFAULT FALSE,
    emergency_contact VARCHAR(20),
    attachment_url VARCHAR(500),
    reviewed_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    review_remarks TEXT,
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_leave_dates CHECK (end_date >= start_date)
);
COMMENT ON TABLE leave_requests IS 'Employee leave/absence requests';

CREATE INDEX idx_leave_requests_user ON leave_requests(user_id);
CREATE INDEX idx_leave_requests_status ON leave_requests(status);
CREATE INDEX idx_leave_requests_dates ON leave_requests(start_date, end_date);
CREATE INDEX idx_leave_requests_reviewer ON leave_requests(reviewed_by);

CREATE TRIGGER trg_leave_requests_updated_at BEFORE UPDATE ON leave_requests FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: leave_balances
-- =============================================================

CREATE TABLE leave_balances (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    leave_type leave_type NOT NULL,
    total_allocated DECIMAL(4,1) NOT NULL DEFAULT 0,
    used DECIMAL(4,1) NOT NULL DEFAULT 0,
    pending DECIMAL(4,1) NOT NULL DEFAULT 0,
    remaining DECIMAL(4,1) NOT NULL DEFAULT 0,
    carried_forward DECIMAL(4,1) DEFAULT 0,
    year INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, leave_type, year)
);
COMMENT ON TABLE leave_balances IS 'Annual leave balance tracking per user per year';

CREATE INDEX idx_leave_balances_user_year ON leave_balances(user_id, year);

CREATE TRIGGER trg_leave_balances_updated_at BEFORE UPDATE ON leave_balances FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: notifications
-- =============================================================

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type notification_type NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT,
    data JSONB,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP,
    reference_type VARCHAR(50),
    reference_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE notifications IS 'In-app and push notifications';
COMMENT ON COLUMN notifications.data IS 'Additional JSON payload for deep linking';

CREATE INDEX idx_notifications_user ON notifications(user_id, is_read, created_at DESC);
CREATE INDEX idx_notifications_unread ON notifications(user_id) WHERE is_read = FALSE;

-- =============================================================
-- TABLE: device_security_logs
-- =============================================================

CREATE TABLE device_security_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_type security_event_type NOT NULL,
    risk_score INTEGER NOT NULL DEFAULT 0 CHECK (risk_score BETWEEN 0 AND 100),
    device_info JSONB,
    is_rooted BOOLEAN NOT NULL DEFAULT FALSE,
    is_mock_location BOOLEAN NOT NULL DEFAULT FALSE,
    is_dev_options_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    is_emulator BOOLEAN NOT NULL DEFAULT FALSE,
    action_taken VARCHAR(100),
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE device_security_logs IS 'Device security event logs for threat detection';
COMMENT ON COLUMN device_security_logs.device_info IS 'Device model, OS, app version, installed apps';

CREATE INDEX idx_device_security_logs_user ON device_security_logs(user_id);
CREATE INDEX idx_device_security_logs_event ON device_security_logs(event_type);
CREATE INDEX idx_device_security_logs_risk ON device_security_logs(risk_score);
CREATE INDEX idx_device_security_logs_created ON device_security_logs(created_at DESC);

-- =============================================================
-- TABLE: audit_logs
-- =============================================================

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    changes JSONB,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE audit_logs IS 'Comprehensive audit trail for all data changes';
COMMENT ON COLUMN audit_logs.changes IS 'JSON diff of before/after values';

CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created ON audit_logs(created_at DESC);

-- =============================================================
-- TABLE: geofences
-- =============================================================

CREATE TABLE geofences (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type geofence_type NOT NULL DEFAULT 'site',
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    radius_meters INTEGER NOT NULL DEFAULT 100,
    branch_id BIGINT REFERENCES branches(id) ON DELETE CASCADE,
    customer_id BIGINT REFERENCES customer_master(id) ON DELETE CASCADE,
    address TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE geofences IS 'Geographic fence definitions for attendance and visit validation';

CREATE INDEX idx_geofences_type ON geofences(type);
CREATE INDEX idx_geofences_branch ON geofences(branch_id);

CREATE TRIGGER trg_geofences_updated_at BEFORE UPDATE ON geofences FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: score_cards
-- =============================================================

CREATE TABLE score_cards (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    month INTEGER NOT NULL CHECK (month BETWEEN 1 AND 12),
    year INTEGER NOT NULL,
    attendance_score DECIMAL(5,2) DEFAULT 0,
    punctuality_score DECIMAL(5,2) DEFAULT 0,
    visit_completion_score DECIMAL(5,2) DEFAULT 0,
    report_quality_score DECIMAL(5,2) DEFAULT 0,
    enquiry_resolution_score DECIMAL(5,2) DEFAULT 0,
    customer_feedback_score DECIMAL(5,2) DEFAULT 0,
    total_score DECIMAL(5,2) DEFAULT 0,
    grade VARCHAR(2),
    manager_review_score DECIMAL(5,2),
    manager_review_notes TEXT,
    score_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, month, year)
);
COMMENT ON TABLE score_cards IS 'Monthly performance score cards for field engineers';
COMMENT ON COLUMN score_cards.score_data IS 'Detailed breakdown JSON for parameter-wise scores';

CREATE INDEX idx_score_cards_user ON score_cards(user_id);
CREATE INDEX idx_score_cards_month_year ON score_cards(month, year);
CREATE INDEX idx_score_cards_total ON score_cards(total_score DESC);

CREATE TRIGGER trg_score_cards_updated_at BEFORE UPDATE ON score_cards FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- TABLE: app_config
-- =============================================================

CREATE TABLE app_config (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    config_type VARCHAR(50) NOT NULL DEFAULT 'string' CHECK (config_type IN ('string', 'number', 'boolean', 'json', 'array')),
    description VARCHAR(500),
    module VARCHAR(50),
    is_encrypted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE app_config IS 'Application configuration key-value store';

CREATE INDEX idx_app_config_module ON app_config(module);

CREATE TRIGGER trg_app_config_updated_at BEFORE UPDATE ON app_config FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================
-- FULL-TEXT SEARCH INDEXES
-- =============================================================

CREATE INDEX idx_fts_customer_name ON customer_master USING gin(to_tsvector('english', company_name));
CREATE INDEX idx_fts_enquiry_subject ON enquiries USING gin(to_tsvector('english', subject));
CREATE INDEX idx_fts_visit_purpose ON visits USING gin(to_tsvector('english', purpose));
CREATE INDEX idx_fts_call_report_work ON call_reports USING gin(to_tsvector('english', work_description));
CREATE INDEX idx_fts_quotation_subject ON quotations USING gin(to_tsvector('english', subject));

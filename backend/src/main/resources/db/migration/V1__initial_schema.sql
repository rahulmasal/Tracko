-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(20) UNIQUE NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mobile VARCHAR(15) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    profile_photo_url VARCHAR(500),
    designation VARCHAR(100),
    department_id BIGINT,
    branch_id BIGINT,
    manager_id BIGINT,
    shift_id BIGINT,
    device_binding_id VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    is_locked BOOLEAN DEFAULT FALSE,
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR(45),
    failed_attempts INTEGER DEFAULT 0,
    otp_secret VARCHAR(255),
    refresh_token_hash VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    is_system BOOLEAN DEFAULT FALSE
);

-- Permissions table
CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(150) NOT NULL,
    module VARCHAR(100)
);

-- User-Role mapping
CREATE TABLE IF NOT EXISTS user_role_map (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_by BIGINT,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Role-Permission mapping
CREATE TABLE IF NOT EXISTS role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Attendance table
CREATE TABLE IF NOT EXISTS attendance (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    attendance_date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    check_in_lat DOUBLE PRECISION,
    check_in_lng DOUBLE PRECISION,
    check_out_lat DOUBLE PRECISION,
    check_out_lng DOUBLE PRECISION,
    check_in_location VARCHAR(255),
    check_out_location VARCHAR(255),
    check_in_photo_url VARCHAR(500),
    check_out_photo_url VARCHAR(500),
    status VARCHAR(20),
    status_reason VARCHAR(255),
    is_late BOOLEAN DEFAULT FALSE,
    late_minutes INTEGER DEFAULT 0,
    is_overtime BOOLEAN DEFAULT FALSE,
    overtime_minutes INTEGER DEFAULT 0,
    total_working_hours DOUBLE PRECISION,
    ip_address VARCHAR(45),
    device_info JSONB,
    geofence_verified BOOLEAN DEFAULT FALSE,
    shift_start_time TIME,
    shift_end_time TIME,
    grace_minutes INTEGER DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, attendance_date)
);

-- Visits table
CREATE TABLE IF NOT EXISTS visits (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    customer_id BIGINT,
    planned_date DATE NOT NULL,
    planned_start_time TIME,
    planned_end_time TIME,
    status VARCHAR(50) NOT NULL,
    type VARCHAR(50),
    visit_purpose VARCHAR(500),
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    check_in_lat DOUBLE PRECISION,
    check_in_lng DOUBLE PRECISION,
    check_out_lat DOUBLE PRECISION,
    check_out_lng DOUBLE PRECISION,
    check_in_photo_url VARCHAR(500),
    check_out_photo_url VARCHAR(500),
    time_on_site_minutes INTEGER,
    no_visit_reason VARCHAR(500),
    is_revisit BOOLEAN DEFAULT FALSE,
    original_visit_id BIGINT,
    is_adhoc BOOLEAN DEFAULT FALSE,
    customer_signature_url VARCHAR(500),
    feedback_rating INTEGER,
    feedback_notes VARCHAR(1000),
    geofence_verified BOOLEAN DEFAULT FALSE,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customer master table
CREATE TABLE IF NOT EXISTS customer_master (
    id BIGSERIAL PRIMARY KEY,
    customer_code VARCHAR(50) UNIQUE,
    name VARCHAR(200) NOT NULL,
    company VARCHAR(200),
    email VARCHAR(100),
    phone VARCHAR(15),
    mobile VARCHAR(15),
    address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(20),
    gst_number VARCHAR(20),
    contact_person VARCHAR(150),
    contact_person_phone VARCHAR(15),
    category VARCHAR(100),
    branch_id BIGINT,
    assigned_to BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    lat DOUBLE PRECISION,
    lng DOUBLE PRECISION,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- Call reports table
CREATE TABLE IF NOT EXISTS call_reports (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    customer_id BIGINT REFERENCES customer_master(id),
    visit_id BIGINT REFERENCES visits(id),
    report_date TIMESTAMP NOT NULL,
    description TEXT,
    work_done TEXT,
    parts_used TEXT,
    recommendations TEXT,
    status VARCHAR(20),
    submission_status VARCHAR(20),
    submitted_at TIMESTAMP,
    review_status VARCHAR(20),
    reviewed_by BIGINT,
    reviewed_at TIMESTAMP,
    review_comments TEXT,
    customer_signature_url VARCHAR(500),
    customer_name VARCHAR(150),
    customer_phone VARCHAR(15),
    job_start_time TIMESTAMP,
    job_end_time TIMESTAMP,
    total_hours DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Call report photos table
CREATE TABLE IF NOT EXISTS call_report_photos (
    id BIGSERIAL PRIMARY KEY,
    call_report_id BIGINT NOT NULL REFERENCES call_reports(id),
    photo_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    type VARCHAR(50),
    file_size BIGINT,
    content_type VARCHAR(50),
    taken_at TIMESTAMP,
    lat DOUBLE PRECISION,
    lng DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Enquiries table
CREATE TABLE IF NOT EXISTS enquiries (
    id BIGSERIAL PRIMARY KEY,
    enquiry_code VARCHAR(50) UNIQUE,
    customer_id BIGINT REFERENCES customer_master(id),
    customer_name VARCHAR(200),
    customer_phone VARCHAR(15),
    customer_email VARCHAR(100),
    enquiry_type VARCHAR(100),
    description TEXT,
    status VARCHAR(50) DEFAULT 'NEW',
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    assigned_to BIGINT REFERENCES users(id),
    created_by BIGINT REFERENCES users(id),
    assigned_at TIMESTAMP,
    expected_closure_date TIMESTAMP,
    closed_at TIMESTAMP,
    closure_notes TEXT,
    branch_id BIGINT,
    source VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Enquiry followups table
CREATE TABLE IF NOT EXISTS enquiry_followups (
    id BIGSERIAL PRIMARY KEY,
    enquiry_id BIGINT NOT NULL REFERENCES enquiries(id),
    notes TEXT,
    type VARCHAR(50) DEFAULT 'NOTE',
    followup_date TIMESTAMP,
    next_followup_date TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Quotations table
CREATE TABLE IF NOT EXISTS quotations (
    id BIGSERIAL PRIMARY KEY,
    quotation_number VARCHAR(50) UNIQUE,
    quote_version INTEGER DEFAULT 1,
    customer_id BIGINT REFERENCES customer_master(id),
    customer_name VARCHAR(200),
    customer_email VARCHAR(100),
    customer_phone VARCHAR(15),
    customer_address VARCHAR(500),
    created_by BIGINT NOT NULL REFERENCES users(id),
    quote_date TIMESTAMP NOT NULL,
    valid_until TIMESTAMP,
    status VARCHAR(50) DEFAULT 'DRAFT',
    sub_total DOUBLE PRECISION,
    tax_percentage DOUBLE PRECISION,
    tax_amount DOUBLE PRECISION,
    discount_percentage DOUBLE PRECISION,
    discount_amount DOUBLE PRECISION,
    grand_total DOUBLE PRECISION,
    terms TEXT,
    notes TEXT,
    approval_status VARCHAR(20) DEFAULT 'DRAFT',
    approved_by BIGINT,
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    customer_response VARCHAR(20),
    customer_response_at TIMESTAMP,
    customer_response_notes TEXT,
    sent_to_customer_at TIMESTAMP,
    sent_channel VARCHAR(20),
    sla_deadline TIMESTAMP,
    sla_breached BOOLEAN DEFAULT FALSE,
    original_quotation_id BIGINT,
    branch_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Quotation items table
CREATE TABLE IF NOT EXISTS quotation_items (
    id BIGSERIAL PRIMARY KEY,
    quotation_id BIGINT NOT NULL REFERENCES quotations(id),
    item_name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DOUBLE PRECISION NOT NULL,
    total DOUBLE PRECISION NOT NULL,
    hsn_code VARCHAR(20),
    tax_rate DOUBLE PRECISION,
    tax_amount DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Quotation approvals table
CREATE TABLE IF NOT EXISTS quotation_approvals (
    id BIGSERIAL PRIMARY KEY,
    quotation_id BIGINT NOT NULL REFERENCES quotations(id),
    approver_id BIGINT NOT NULL REFERENCES users(id),
    action VARCHAR(20),
    comments TEXT,
    action_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Leave requests table
CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    type VARCHAR(50),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days DOUBLE PRECISION,
    reason TEXT,
    is_half_day BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'PENDING',
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Leave balances table
CREATE TABLE IF NOT EXISTS leave_balances (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    leave_type VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    total_allocated DOUBLE PRECISION,
    used DOUBLE PRECISION DEFAULT 0,
    pending DOUBLE PRECISION DEFAULT 0,
    remaining DOUBLE PRECISION,
    carried_forward DOUBLE PRECISION DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, leave_type, year)
);

-- Tracking logs table
CREATE TABLE IF NOT EXISTS tracking_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    altitude DOUBLE PRECISION,
    accuracy REAL,
    speed REAL,
    bearing REAL,
    recorded_at TIMESTAMP NOT NULL,
    received_at TIMESTAMP,
    battery_level INTEGER,
    provider VARCHAR(20),
    is_mocked BOOLEAN DEFAULT FALSE,
    activity_type VARCHAR(50),
    activity_confidence INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_tracking_user_recorded ON tracking_logs(user_id, recorded_at DESC);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50),
    title VARCHAR(200) NOT NULL,
    body TEXT,
    reference_type VARCHAR(50),
    reference_id BIGINT,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    channel VARCHAR(20),
    delivery_status VARCHAR(20),
    image_url VARCHAR(500),
    action_deep_link VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Device security logs table
CREATE TABLE IF NOT EXISTS device_security_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_id VARCHAR(255),
    os_version VARCHAR(50),
    app_version VARCHAR(20),
    device_model VARCHAR(100),
    is_rooted BOOLEAN DEFAULT FALSE,
    is_mock_location_enabled BOOLEAN DEFAULT FALSE,
    is_developer_mode BOOLEAN DEFAULT FALSE,
    is_screen_lock_enabled BOOLEAN DEFAULT TRUE,
    is_play_protect_enabled BOOLEAN DEFAULT TRUE,
    is_unknown_sources BOOLEAN DEFAULT FALSE,
    risk_score INTEGER,
    risk_level VARCHAR(20),
    policy_action VARCHAR(30),
    checked_at TIMESTAMP,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Audit logs table (append-only)
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    before_snapshot JSONB,
    after_snapshot JSONB,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    request_path VARCHAR(500),
    http_method VARCHAR(10),
    response_status INTEGER,
    execution_time_ms BIGINT,
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Geofences table
CREATE TABLE IF NOT EXISTS geofences (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    radius_meters DOUBLE PRECISION NOT NULL,
    type VARCHAR(50),
    branch_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT
);

-- Shifts table
CREATE TABLE IF NOT EXISTS shifts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    grace_minutes INTEGER DEFAULT 15,
    late_threshold_minutes INTEGER DEFAULT 30,
    half_day_threshold_minutes INTEGER DEFAULT 120,
    overtime_threshold_minutes INTEGER DEFAULT 60,
    is_night_shift BOOLEAN DEFAULT FALSE,
    applicable_days VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    branch_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Holidays table
CREATE TABLE IF NOT EXISTS holidays (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    holiday_date DATE NOT NULL,
    type VARCHAR(50),
    is_recurring BOOLEAN DEFAULT FALSE,
    branch_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Score cards table
CREATE TABLE IF NOT EXISTS score_cards (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    score_month INTEGER NOT NULL,
    score_year INTEGER NOT NULL,
    visit_completion_score DOUBLE PRECISION,
    on_time_attendance_score DOUBLE PRECISION,
    report_completeness_score DOUBLE PRECISION,
    tracking_compliance_score DOUBLE PRECISION,
    photo_proof_score DOUBLE PRECISION,
    customer_feedback_score DOUBLE PRECISION,
    repeat_visit_score DOUBLE PRECISION,
    enquiry_gen_score DOUBLE PRECISION,
    quote_followup_score DOUBLE PRECISION,
    job_closure_score DOUBLE PRECISION,
    manager_score DOUBLE PRECISION,
    total_score DOUBLE PRECISION,
    rating VARCHAR(10),
    is_manual BOOLEAN DEFAULT FALSE,
    adjusted_by BIGINT,
    adjustment_reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, score_month, score_year)
);

-- Attendance corrections table
CREATE TABLE IF NOT EXISTS attendance_corrections (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    attendance_id BIGINT NOT NULL REFERENCES attendance(id),
    correction_type VARCHAR(50) NOT NULL,
    reason TEXT,
    requested_time TIMESTAMP,
    original_value VARCHAR(255),
    requested_value VARCHAR(255),
    status VARCHAR(20) DEFAULT 'PENDING',
    reviewed_by BIGINT REFERENCES users(id),
    reviewed_at TIMESTAMP,
    review_comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Missed pings table
CREATE TABLE IF NOT EXISTS missed_pings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    expected_time TIMESTAMP,
    last_known_lat DOUBLE PRECISION,
    last_known_lng DOUBLE PRECISION,
    last_known_time TIMESTAMP,
    consecutive_misses INTEGER DEFAULT 1,
    severity VARCHAR(20),
    is_resolved BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP,
    escalation_level INTEGER DEFAULT 0,
    notified_manager BOOLEAN DEFAULT FALSE,
    notified_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- App config table
CREATE TABLE IF NOT EXISTS app_config (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value JSONB NOT NULL,
    description VARCHAR(500),
    module VARCHAR(100),
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default roles
INSERT INTO roles (name, description, is_system) VALUES
    ('ADMIN', 'System Administrator', true),
    ('MANAGER', 'Field Manager', true),
    ('ENGINEER', 'Field Engineer', true)
ON CONFLICT (name) DO NOTHING;

-- Insert default permissions
INSERT INTO permissions (code, name, module) VALUES
    ('USER_VIEW', 'View Users', 'User Management'),
    ('USER_CREATE', 'Create Users', 'User Management'),
    ('USER_EDIT', 'Edit Users', 'User Management'),
    ('USER_DELETE', 'Delete Users', 'User Management'),
    ('ATTENDANCE_VIEW', 'View Attendance', 'Attendance'),
    ('ATTENDANCE_EDIT', 'Edit Attendance', 'Attendance'),
    ('VISIT_VIEW', 'View Visits', 'Visit Management'),
    ('VISIT_CREATE', 'Create Visits', 'Visit Management'),
    ('VISIT_EDIT', 'Edit Visits', 'Visit Management'),
    ('REPORT_VIEW', 'View Reports', 'Reports'),
    ('REPORT_CREATE', 'Create Reports', 'Reports'),
    ('REPORT_APPROVE', 'Approve Reports', 'Reports'),
    ('QUOTATION_VIEW', 'View Quotations', 'Quotations'),
    ('QUOTATION_CREATE', 'Create Quotations', 'Quotations'),
    ('QUOTATION_APPROVE', 'Approve Quotations', 'Quotations'),
    ('ENQUIRY_VIEW', 'View Enquiries', 'Enquiries'),
    ('ENQUIRY_CREATE', 'Create Enquiries', 'Enquiries'),
    ('ENQUIRY_ASSIGN', 'Assign Enquiries', 'Enquiries'),
    ('LEAVE_VIEW', 'View Leaves', 'Leave Management'),
    ('LEAVE_APPROVE', 'Approve Leaves', 'Leave Management'),
    ('SCORE_VIEW', 'View Scores', 'Performance'),
    ('SCORE_EDIT', 'Edit Scores', 'Performance'),
    ('CONFIG_EDIT', 'Edit Configuration', 'System'),
    ('AUDIT_VIEW', 'View Audit Logs', 'System')
ON CONFLICT (code) DO NOTHING;

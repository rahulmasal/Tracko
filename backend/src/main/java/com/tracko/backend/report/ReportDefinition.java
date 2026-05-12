package com.tracko.backend.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportDefinition {

    ATTENDANCE_DAILY(
        "Daily Attendance Report",
        "SELECT a.attendance_date, u.full_name, a.check_in_time, a.check_out_time, a.status, a.late_minutes, a.total_working_hours " +
        "FROM attendance a JOIN users u ON a.user_id = u.id WHERE a.attendance_date = :date",
        new String[]{"Date", "Employee", "Check In", "Check Out", "Status", "Late Mins", "Hours"},
        new String[]{"PDF", "EXCEL", "CSV"}
    ),
    ATTENDANCE_MONTHLY(
        "Monthly Attendance Summary",
        "SELECT u.full_name, COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) as present, " +
        "COUNT(CASE WHEN a.status = 'LATE' THEN 1 END) as late, " +
        "COUNT(CASE WHEN a.status = 'ABSENT' THEN 1 END) as absent " +
        "FROM attendance a JOIN users u ON a.user_id = u.id " +
        "WHERE EXTRACT(MONTH FROM a.attendance_date) = :month AND EXTRACT(YEAR FROM a.attendance_date) = :year " +
        "GROUP BY u.full_name",
        new String[]{"Employee", "Present", "Late", "Absent"},
        new String[]{"PDF", "EXCEL", "CSV"}
    ),
    VISIT_COMPLETION(
        "Visit Completion Report",
        "SELECT u.full_name, COUNT(v.id) as total, " +
        "COUNT(CASE WHEN v.status = 'COMPLETED' THEN 1 END) as completed, " +
        "ROUND(COUNT(CASE WHEN v.status = 'COMPLETED' THEN 1 END) * 100.0 / COUNT(v.id), 1) as rate " +
        "FROM visits v JOIN users u ON v.user_id = u.id " +
        "WHERE v.planned_date BETWEEN :startDate AND :endDate GROUP BY u.full_name",
        new String[]{"Employee", "Total", "Completed", "Rate"},
        new String[]{"PDF", "EXCEL", "CSV"}
    ),
    SCORE_CARD(
        "Engineer Score Card",
        "SELECT u.full_name, sc.score_month, sc.score_year, sc.total_score, sc.rating " +
        "FROM score_cards sc JOIN users u ON sc.user_id = u.id " +
        "WHERE sc.score_month = :month AND sc.score_year = :year",
        new String[]{"Employee", "Month", "Year", "Score", "Rating"},
        new String[]{"PDF", "EXCEL", "CSV"}
    ),
    QUOTATION_STATUS(
        "Quotation Status Report",
        "SELECT q.quotation_number, u.full_name, q.customer_name, q.grand_total, q.status, q.created_at " +
        "FROM quotations q JOIN users u ON q.created_by = u.id ORDER BY q.created_at DESC",
        new String[]{"Quotation#", "Created By", "Customer", "Amount", "Status", "Date"},
        new String[]{"PDF", "EXCEL", "CSV"}
    ),
    ENQUIRY_FUNNEL(
        "Enquiry Funnel Report",
        "SELECT e.status, COUNT(e.id) as count FROM enquiries e GROUP BY e.status",
        new String[]{"Status", "Count"},
        new String[]{"PDF", "EXCEL", "CSV"}
    );

    private final String displayName;
    private final String sql;
    private final String[] columns;
    private final String[] exportFormats;
}

package com.tracko.backend.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.tracko.backend.model.Attendance;
import com.tracko.backend.model.Visit;
import com.tracko.backend.repository.AttendanceRepository;
import com.tracko.backend.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportExportService {

    private final AttendanceRepository attendanceRepository;
    private final VisitRepository visitRepository;

    public byte[] generateExcelAttendanceReport(LocalDate startDate, LocalDate endDate, Long branchId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance Report");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] columns = {"Date", "Employee", "Check In", "Check Out", "Status", "Late Mins", "Working Hours"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Attendance> records = attendanceRepository.findByAttendanceDateBetween(
                startDate, endDate);

            int rowNum = 1;
            for (Attendance att : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(att.getAttendanceDate().toString());
                row.createCell(1).setCellValue(att.getUser().getFullName());
                row.createCell(2).setCellValue(att.getCheckInTime() != null ? att.getCheckInTime().toString() : "");
                row.createCell(3).setCellValue(att.getCheckOutTime() != null ? att.getCheckOutTime().toString() : "");
                row.createCell(4).setCellValue(att.getStatus());
                row.createCell(5).setCellValue(att.getLateMinutes() != null ? att.getLateMinutes() : 0);
                row.createCell(6).setCellValue(att.getTotalWorkingHours() != null ? att.getTotalWorkingHours() : 0);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    public byte[] generatePdfAttendanceReport(LocalDate startDate, LocalDate endDate, Long branchId) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Attendance Report")
                .setFontSize(20).setBold());
            document.add(new Paragraph("Period: " + startDate + " to " + endDate));

            float[] columnWidths = {2, 3, 2, 2, 2, 1, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.addHeaderCell(new Cell().add(new Paragraph("Date")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Employee")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Check In")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Check Out")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Status")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Late")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Hours")).setBold());

            List<Attendance> records = attendanceRepository.findByAttendanceDateBetween(startDate, endDate);
            for (Attendance att : records) {
                table.addCell(att.getAttendanceDate().toString());
                table.addCell(att.getUser().getFullName());
                table.addCell(att.getCheckInTime() != null ? att.getCheckInTime().toString() : "");
                table.addCell(att.getCheckOutTime() != null ? att.getCheckOutTime().toString() : "");
                table.addCell(att.getStatus());
                table.addCell(String.valueOf(att.getLateMinutes() != null ? att.getLateMinutes() : 0));
                table.addCell(String.valueOf(att.getTotalWorkingHours() != null ? att.getTotalWorkingHours() : 0));
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    public byte[] generateExcelVisitReport(LocalDate startDate, LocalDate endDate, Long userId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Visit Report");
            // implementation similar to attendance
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    public byte[] exportToCsv(List<String[]> data, String[] headers) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", headers)).append("\n");
        for (String[] row : data) {
            sb.append(String.join(",", row)).append("\n");
        }
        return sb.toString().getBytes();
    }
}

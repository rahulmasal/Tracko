package com.tracko.backend.report;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
@Service
public class ExcelExportService {

    public byte[] generateExcel(String sheetName, String[] headers, List<Object[]> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.getCreationHelper()
                .createDataFormat().getFormat("yyyy-mm-dd"));

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Object[] rowData : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowData.length; i++) {
                    Cell cell = row.createCell(i);
                    Object value = rowData[i];
                    if (value == null) continue;

                    if (value instanceof Number num) {
                        cell.setCellValue(num.doubleValue());
                    } else if (value instanceof java.time.LocalDate ld) {
                        cell.setCellValue(ld.toString());
                        cell.setCellStyle(dateStyle);
                    } else if (value instanceof java.time.LocalDateTime ldt) {
                        cell.setCellValue(ldt.toString());
                    } else if (value instanceof Boolean bool) {
                        cell.setCellValue(bool);
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate Excel", e);
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }

    public byte[] generateAttendanceExcel(String[] headers, List<Object[]> data) {
        return generateExcel("Attendance", headers, data);
    }

    public byte[] generateVisitExcel(String[] headers, List<Object[]> data) {
        return generateExcel("Visits", headers, data);
    }

    public byte[] generateScoreExcel(String[] headers, List<Object[]> data) {
        return generateExcel("Scores", headers, data);
    }
}

package com.tracko.backend.report;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.tracko.backend.model.CallReport;
import com.tracko.backend.model.Quotation;
import com.tracko.backend.model.QuotationItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
public class PdfGeneratorService {

    public byte[] generateCallReportPdf(CallReport report) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Call Report").setFontSize(22).setBold());
            document.add(new Paragraph("Report #" + report.getId()));
            document.add(new Paragraph("Date: " + report.getReportDate()));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Customer: " +
                (report.getCustomerName() != null ? report.getCustomerName() : "N/A")));
            document.add(new Paragraph("Phone: " +
                (report.getCustomerPhone() != null ? report.getCustomerPhone() : "N/A")));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Work Done:").setBold());
            document.add(new Paragraph(report.getWorkDone() != null ? report.getWorkDone() : "N/A"));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Parts Used:").setBold());
            document.add(new Paragraph(report.getPartsUsed() != null ? report.getPartsUsed() : "N/A"));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Recommendations:").setBold());
            document.add(new Paragraph(report.getRecommendations() != null ? report.getRecommendations() : "N/A"));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate call report PDF", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    public byte[] generateQuotationPdf(Quotation quotation) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Quotation").setFontSize(22).setBold());
            document.add(new Paragraph("Quotation #: " + quotation.getQuotationNumber()));
            document.add(new Paragraph("Date: " + quotation.getQuoteDate()));
            document.add(new Paragraph("Valid Until: " + (quotation.getValidUntil() != null ? quotation.getValidUntil() : "N/A")));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Customer: " +
                (quotation.getCustomerName() != null ? quotation.getCustomerName() : "N/A")));
            document.add(new Paragraph("Email: " +
                (quotation.getCustomerEmail() != null ? quotation.getCustomerEmail() : "N/A")));
            document.add(new Paragraph("Phone: " +
                (quotation.getCustomerPhone() != null ? quotation.getCustomerPhone() : "N/A")));
            document.add(new Paragraph(" "));

            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 2, 2}));
            table.addHeaderCell("Item");
            table.addHeaderCell("Qty");
            table.addHeaderCell("Unit Price");
            table.addHeaderCell("Total");

            for (QuotationItem item : quotation.getItems()) {
                table.addCell(item.getItemName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.format("%.2f", item.getUnitPrice()));
                table.addCell(String.format("%.2f", item.getTotal()));
            }
            document.add(table);
            document.add(new Paragraph(" "));

            document.add(new Paragraph(String.format("Sub Total: %.2f", quotation.getSubTotal())));
            if (quotation.getTaxAmount() != null)
                document.add(new Paragraph(String.format("Tax: %.2f", quotation.getTaxAmount())));
            if (quotation.getDiscountAmount() != null && quotation.getDiscountAmount() > 0)
                document.add(new Paragraph(String.format("Discount: %.2f", quotation.getDiscountAmount())));
            document.add(new Paragraph(String.format("Grand Total: %.2f", quotation.getGrandTotal())).setBold());

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate quotation PDF", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}

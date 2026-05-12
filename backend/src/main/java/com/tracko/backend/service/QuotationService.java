package com.tracko.backend.service;

import com.tracko.backend.dto.QuotationRequest;
import com.tracko.backend.exception.BusinessException;
import com.tracko.backend.exception.ResourceNotFoundException;
import com.tracko.backend.model.*;
import com.tracko.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository itemRepository;
    private final QuotationApprovalRepository approvalRepository;
    private final UserRepository userRepository;

    @Transactional
    public Quotation createQuotation(Long userId, QuotationRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Quotation quotation = Quotation.builder()
            .quotationNumber(generateQuotationNumber())
            .quoteVersion(1)
            .customerName(request.getCustomerName())
            .customerEmail(request.getCustomerEmail())
            .customerPhone(request.getCustomerPhone())
            .customerAddress(request.getCustomerAddress())
            .createdBy(user)
            .quoteDate(request.getQuoteDate() != null ? request.getQuoteDate() : LocalDateTime.now())
            .validUntil(request.getValidUntil())
            .status(request.getStatus() != null ? request.getStatus() : "DRAFT")
            .approvalStatus("DRAFT")
            .taxPercentage(request.getTaxPercentage())
            .discountPercentage(request.getDiscountPercentage())
            .terms(request.getTerms())
            .notes(request.getNotes())
            .branchId(request.getBranchId())
            .build();

        quotation = quotationRepository.save(quotation);

        double subTotal = 0;
        List<QuotationItem> items = new ArrayList<>();
        for (QuotationRequest.QuotationItemRequest itemReq : request.getItems()) {
            double total = itemReq.getQuantity() * itemReq.getUnitPrice();
            double taxAmount = total * (itemReq.getTaxRate() != null ? itemReq.getTaxRate() / 100 : 0);

            QuotationItem item = QuotationItem.builder()
                .quotation(quotation)
                .itemName(itemReq.getItemName())
                .description(itemReq.getDescription())
                .quantity(itemReq.getQuantity() != null ? itemReq.getQuantity() : 1)
                .unitPrice(itemReq.getUnitPrice())
                .total(total)
                .hsnCode(itemReq.getHsnCode())
                .taxRate(itemReq.getTaxRate())
                .taxAmount(taxAmount)
                .build();
            items.add(item);
            subTotal += total;
        }
        itemRepository.saveAll(items);
        quotation.setItems(items);

        quotation.setSubTotal(subTotal);
        double taxAmount = subTotal * (request.getTaxPercentage() != null ? request.getTaxPercentage() / 100 : 0);
        double discountAmount = subTotal * (request.getDiscountPercentage() != null ? request.getDiscountPercentage() / 100 : 0);
        quotation.setTaxAmount(taxAmount);
        quotation.setDiscountAmount(discountAmount);
        quotation.setGrandTotal(subTotal + taxAmount - discountAmount);

        return quotationRepository.save(quotation);
    }

    public Quotation getQuotation(Long id) {
        return quotationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Quotation", "id", id));
    }

    @Transactional
    public Quotation submitForApproval(Long id, Long userId) {
        Quotation quotation = getQuotation(id);
        quotation.setApprovalStatus("PENDING_APPROVAL");
        quotation.setStatus("PENDING_APPROVAL");
        return quotationRepository.save(quotation);
    }

    @Transactional
    public Quotation approveQuotation(Long id, Long approverId, String comments) {
        Quotation quotation = getQuotation(id);
        quotation.setApprovalStatus("APPROVED");
        quotation.setStatus("APPROVED");
        quotation.setApprovedBy(approverId);
        quotation.setApprovedAt(LocalDateTime.now());

        QuotationApproval approval = QuotationApproval.builder()
            .quotation(quotation)
            .approver(userRepository.getReferenceById(approverId))
            .action("APPROVED")
            .comments(comments)
            .build();
        approvalRepository.save(approval);

        return quotationRepository.save(quotation);
    }

    @Transactional
    public Quotation rejectQuotation(Long id, Long approverId, String comments) {
        Quotation quotation = getQuotation(id);
        quotation.setApprovalStatus("REJECTED");
        quotation.setStatus("REJECTED");
        quotation.setRejectionReason(comments);

        QuotationApproval approval = QuotationApproval.builder()
            .quotation(quotation)
            .approver(userRepository.getReferenceById(approverId))
            .action("REJECTED")
            .comments(comments)
            .build();
        approvalRepository.save(approval);

        return quotationRepository.save(quotation);
    }

    @Transactional
    public Quotation sendToCustomer(Long id, String channel, String email, String phone) {
        Quotation quotation = getQuotation(id);
        quotation.setSentToCustomerAt(LocalDateTime.now());
        quotation.setSentChannel(channel);
        quotation.setStatus("SENT");
        return quotationRepository.save(quotation);
    }

    @Transactional
    public Quotation recordCustomerResponse(Long id, String response, String notes) {
        Quotation quotation = getQuotation(id);
        quotation.setCustomerResponse(response);
        quotation.setCustomerResponseAt(LocalDateTime.now());
        quotation.setCustomerResponseNotes(notes);

        if ("ACCEPTED".equals(response)) {
            quotation.setStatus("ACCEPTED");
        } else if ("DECLINED".equals(response)) {
            quotation.setStatus("DECLINED");
        } else if ("NEGOTIATION".equals(response)) {
            quotation.setStatus("NEGOTIATION");
        }

        return quotationRepository.save(quotation);
    }

    @Transactional
    public Quotation reviseQuotation(Long id, Long userId, QuotationRequest request) {
        Quotation existing = getQuotation(id);
        Quotation revised = createQuotation(userId, request);
        revised.setQuoteVersion(existing.getQuoteVersion() + 1);
        revised.setOriginalQuotationId(id);
        return quotationRepository.save(revised);
    }

    public Page<Quotation> listQuotations(Long userId, String status, Pageable pageable) {
        if (userId != null) {
            return quotationRepository.findByCreatedByIdOrderByCreatedAtDesc(userId, pageable);
        }
        if (status != null) {
            return quotationRepository.findByApprovalStatus(status, pageable);
        }
        return quotationRepository.findAll(pageable);
    }

    public List<Quotation> getPendingApprovals() {
        return quotationRepository.findByApprovalStatus("PENDING_APPROVAL");
    }

    public List<Quotation> getSlaBreaches() {
        return quotationRepository.findNearingSlaBreach(LocalDateTime.now().plusHours(2));
    }

    private String generateQuotationNumber() {
        return "QTN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

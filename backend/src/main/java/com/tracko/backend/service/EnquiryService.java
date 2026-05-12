package com.tracko.backend.service;

import com.tracko.backend.dto.EnquiryFollowupRequest;
import com.tracko.backend.dto.EnquiryRequest;
import com.tracko.backend.exception.BusinessException;
import com.tracko.backend.exception.ResourceNotFoundException;
import com.tracko.backend.model.Enquiry;
import com.tracko.backend.model.EnquiryFollowup;
import com.tracko.backend.model.User;
import com.tracko.backend.repository.EnquiryFollowupRepository;
import com.tracko.backend.repository.EnquiryRepository;
import com.tracko.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnquiryService {

    private final EnquiryRepository enquiryRepository;
    private final EnquiryFollowupRepository followupRepository;
    private final UserRepository userRepository;

    @Transactional
    public Enquiry createEnquiry(Long userId, EnquiryRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Enquiry enquiry = Enquiry.builder()
            .customerName(request.getCustomerName())
            .customerPhone(request.getCustomerPhone())
            .customerEmail(request.getCustomerEmail())
            .enquiryType(request.getEnquiryType())
            .description(request.getDescription())
            .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
            .status("NEW")
            .source(request.getSource())
            .createdBy(user)
            .expectedClosureDate(request.getExpectedClosureDate())
            .build();

        if (request.getAssignedTo() != null) {
            User assignedUser = userRepository.findById(request.getAssignedTo())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedTo()));
            enquiry.setAssignedTo(assignedUser);
            enquiry.setAssignedAt(LocalDateTime.now());
        }

        return enquiryRepository.save(enquiry);
    }

    @Transactional
    public Enquiry updateEnquiry(Long id, EnquiryRequest request) {
        Enquiry enquiry = enquiryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Enquiry", "id", id));

        if (request.getCustomerName() != null) enquiry.setCustomerName(request.getCustomerName());
        if (request.getCustomerPhone() != null) enquiry.setCustomerPhone(request.getCustomerPhone());
        if (request.getCustomerEmail() != null) enquiry.setCustomerEmail(request.getCustomerEmail());
        if (request.getEnquiryType() != null) enquiry.setEnquiryType(request.getEnquiryType());
        if (request.getDescription() != null) enquiry.setDescription(request.getDescription());
        if (request.getPriority() != null) enquiry.setPriority(request.getPriority());
        if (request.getExpectedClosureDate() != null) enquiry.setExpectedClosureDate(request.getExpectedClosureDate());
        if (request.getSource() != null) enquiry.setSource(request.getSource());

        return enquiryRepository.save(enquiry);
    }

    @Transactional
    public Enquiry assignEnquiry(Long id, Long assignedToId) {
        Enquiry enquiry = enquiryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Enquiry", "id", id));

        User assignedUser = userRepository.findById(assignedToId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", assignedToId));

        enquiry.setAssignedTo(assignedUser);
        enquiry.setAssignedAt(LocalDateTime.now());
        if ("NEW".equals(enquiry.getStatus())) {
            enquiry.setStatus("ASSIGNED");
        }

        return enquiryRepository.save(enquiry);
    }

    @Transactional
    public Enquiry updateStatus(Long id, String status, String closureNotes) {
        Enquiry enquiry = enquiryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Enquiry", "id", id));

        enquiry.setStatus(status);
        if ("CLOSED".equals(status) || "RESOLVED".equals(status)) {
            enquiry.setClosedAt(LocalDateTime.now());
            enquiry.setClosureNotes(closureNotes);
        }

        return enquiryRepository.save(enquiry);
    }

    @Transactional
    public EnquiryFollowup addFollowup(Long enquiryId, Long userId, EnquiryFollowupRequest request) {
        Enquiry enquiry = enquiryRepository.findById(enquiryId)
            .orElseThrow(() -> new ResourceNotFoundException("Enquiry", "id", enquiryId));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        EnquiryFollowup followup = EnquiryFollowup.builder()
            .enquiry(enquiry)
            .notes(request.getNotes())
            .type(request.getType() != null ? request.getType() : "NOTE")
            .nextFollowupDate(request.getNextFollowupDate())
            .createdBy(user)
            .build();

        return followupRepository.save(followup);
    }

    public Enquiry getEnquiry(Long id) {
        return enquiryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Enquiry", "id", id));
    }

    public Page<Enquiry> getEnquiryList(Long assignedToId, String status, Pageable pageable) {
        if (assignedToId != null) {
            return enquiryRepository.findByAssignedToIdOrderByCreatedAtDesc(assignedToId, pageable);
        }
        if (status != null) {
            return enquiryRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        }
        return enquiryRepository.findAll(pageable);
    }

    public Map<String, Object> getFunnelData() {
        Map<String, Object> funnel = new HashMap<>();
        funnel.put("total", enquiryRepository.count());
        funnel.put("byStatus", enquiryRepository.getStatusCounts());
        funnel.put("bySource", enquiryRepository.getSourceCounts());
        return funnel;
    }

    public List<EnquiryFollowup> getFollowups(Long enquiryId) {
        return followupRepository.findByEnquiryIdOrderByCreatedAtDesc(enquiryId);
    }
}

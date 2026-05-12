package com.tracko.backend.service;

import com.tracko.backend.dto.VisitCheckInRequest;
import com.tracko.backend.dto.VisitRequest;
import com.tracko.backend.exception.BusinessException;
import com.tracko.backend.exception.ResourceNotFoundException;
import com.tracko.backend.model.CustomerMaster;
import com.tracko.backend.model.User;
import com.tracko.backend.model.Visit;
import com.tracko.backend.repository.CustomerMasterRepository;
import com.tracko.backend.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final CustomerMasterRepository customerRepository;

    @Transactional
    public Visit createVisit(Long userId, VisitRequest request) {
        User user = User.builder().id(userId).build();
        CustomerMaster customer = request.getCustomerId() != null ?
            customerRepository.findById(request.getCustomerId()).orElse(null) : null;

        Visit visit = Visit.builder()
            .user(user)
            .customer(customer)
            .plannedDate(request.getPlannedDate())
            .plannedStartTime(request.getPlannedStartTime())
            .plannedEndTime(request.getPlannedEndTime())
            .status(request.getStatus() != null ? request.getStatus() : "PLANNED")
            .type(request.getType())
            .visitPurpose(request.getVisitPurpose())
            .isRevisit(request.getIsRevisit() != null && request.getIsRevisit())
            .originalVisitId(request.getOriginalVisitId())
            .isAdhoc(request.getIsAdhoc() != null && request.getIsAdhoc())
            .createdBy(userId)
            .build();

        return visitRepository.save(visit);
    }

    @Transactional
    public Visit updateVisit(Long id, Long userId, VisitRequest request) {
        Visit visit = visitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Visit", "id", id));

        if (request.getCustomerId() != null) {
            CustomerMaster customer = customerRepository.findById(request.getCustomerId()).orElse(null);
            visit.setCustomer(customer);
        }
        if (request.getPlannedDate() != null) visit.setPlannedDate(request.getPlannedDate());
        if (request.getPlannedStartTime() != null) visit.setPlannedStartTime(request.getPlannedStartTime());
        if (request.getPlannedEndTime() != null) visit.setPlannedEndTime(request.getPlannedEndTime());
        if (request.getStatus() != null) visit.setStatus(request.getStatus());
        if (request.getType() != null) visit.setType(request.getType());
        if (request.getVisitPurpose() != null) visit.setVisitPurpose(request.getVisitPurpose());

        return visitRepository.save(visit);
    }

    @Transactional
    public Visit checkInVisit(Long id, Long userId, VisitCheckInRequest request) {
        Visit visit = visitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Visit", "id", id));

        if (visit.getCheckInTime() != null) {
            throw new BusinessException("Already checked in to this visit");
        }

        visit.setCheckInTime(LocalDateTime.now());
        visit.setCheckInLat(request.getLat());
        visit.setCheckInLng(request.getLng());
        visit.setCheckInPhotoUrl(request.getPhoto());
        visit.setNoVisitReason(request.getNoVisitReason());
        visit.setStatus(request.getNoVisitReason() != null ? "NO_VISIT" : "IN_PROGRESS");
        visit.setGeofenceVerified(false);

        return visitRepository.save(visit);
    }

    @Transactional
    public Visit checkOutVisit(Long id, Long userId, VisitCheckInRequest request) {
        Visit visit = visitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Visit", "id", id));

        if (visit.getCheckInTime() == null) {
            throw new BusinessException("Must check in before checking out");
        }
        if (visit.getCheckOutTime() != null) {
            throw new BusinessException("Already checked out from this visit");
        }

        visit.setCheckOutTime(LocalDateTime.now());
        visit.setCheckOutLat(request.getLat());
        visit.setCheckOutLng(request.getLng());
        visit.setCheckOutPhotoUrl(request.getPhoto());
        visit.setStatus("COMPLETED");

        int minutes = (int) ChronoUnit.MINUTES.between(visit.getCheckInTime(), visit.getCheckOutTime());
        visit.setTimeOnSiteMinutes(minutes);

        return visitRepository.save(visit);
    }

    public Visit getVisitById(Long id) {
        return visitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Visit", "id", id));
    }

    public List<Visit> getTodayVisits(Long userId) {
        return visitRepository.findByUserIdAndPlannedDate(userId, LocalDate.now());
    }

    public Page<Visit> getVisitHistory(Long userId, Pageable pageable) {
        return visitRepository.findByUserIdOrderByPlannedDateDesc(userId, pageable);
    }

    public List<Visit> getTeamVisits(Long managerId, LocalDate date) {
        return visitRepository.findByManagerIdAndDate(managerId, date);
    }

    public List<Visit> getMissedVisits(LocalDate date) {
        return visitRepository.findMissedVisits(date);
    }

    @Transactional
    public Visit createAdhocVisit(Long userId, VisitRequest request) {
        request.setIsAdhoc(true);
        return createVisit(userId, request);
    }

    public long countCompletedVisits(Long userId, LocalDate start, LocalDate end) {
        return visitRepository.countCompletedByUserAndDateBetween(userId, start, end);
    }

    public long countTotalVisits(Long userId, LocalDate start, LocalDate end) {
        return visitRepository.countTotalByUserAndDateBetween(userId, start, end);
    }
}

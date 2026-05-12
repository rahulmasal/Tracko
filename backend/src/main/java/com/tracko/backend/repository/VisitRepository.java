package com.tracko.backend.repository;

import com.tracko.backend.model.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByUserIdAndPlannedDate(Long userId, LocalDate plannedDate);

    Page<Visit> findByUserIdOrderByPlannedDateDesc(Long userId, Pageable pageable);

    List<Visit> findByUserIdAndPlannedDateBetween(Long userId, LocalDate start, LocalDate end);

    List<Visit> findByStatus(String status);

    List<Visit> findByCustomerId(Long customerId);

    @Query("SELECT COUNT(v) FROM Visit v WHERE v.user.id = :userId " +
           "AND v.plannedDate BETWEEN :start AND :end AND v.status = 'COMPLETED'")
    long countCompletedByUserAndDateBetween(@Param("userId") Long userId,
                                            @Param("start") LocalDate start,
                                            @Param("end") LocalDate end);

    @Query("SELECT COUNT(v) FROM Visit v WHERE v.user.id = :userId " +
           "AND v.plannedDate BETWEEN :start AND :end")
    long countTotalByUserAndDateBetween(@Param("userId") Long userId,
                                        @Param("start") LocalDate start,
                                        @Param("end") LocalDate end);

    @Query("SELECT v FROM Visit v WHERE v.user.managerId = :managerId " +
           "AND v.plannedDate = :date ORDER BY v.plannedStartTime")
    List<Visit> findByManagerIdAndDate(@Param("managerId") Long managerId,
                                       @Param("date") LocalDate date);

    @Query("SELECT v FROM Visit v WHERE v.plannedDate = :date AND v.status = 'PLANNED' " +
           "AND v.checkInTime IS NULL")
    List<Visit> findMissedVisits(@Param("date") LocalDate date);

    Page<Visit> findByStatusNot(String status, Pageable pageable);
}

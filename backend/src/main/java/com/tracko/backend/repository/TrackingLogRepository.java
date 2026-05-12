package com.tracko.backend.repository;

import com.tracko.backend.model.TrackingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingLogRepository extends JpaRepository<TrackingLog, Long> {

    List<TrackingLog> findByUserIdAndRecordedAtBetweenOrderByRecordedAtAsc(
        Long userId, LocalDateTime start, LocalDateTime end);

    Optional<TrackingLog> findFirstByUserIdOrderByRecordedAtDesc(Long userId);

    @Query("SELECT COUNT(tl) FROM TrackingLog tl WHERE tl.userId = :userId " +
           "AND tl.recordedAt BETWEEN :start AND :end")
    long countByUserIdAndRecordedAtBetween(@Param("userId") Long userId,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    @Query("SELECT tl FROM TrackingLog tl WHERE tl.userId IN :userIds " +
           "AND tl.recordedAt = (SELECT MAX(tl2.recordedAt) FROM TrackingLog tl2 WHERE tl2.userId = tl.userId)")
    List<TrackingLog> findLastKnownForUsers(@Param("userIds") List<Long> userIds);

    @Query("SELECT tl FROM TrackingLog tl WHERE tl.recordedAt < :cutoff")
    List<TrackingLog> findOlderThan(@Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Query("DELETE FROM TrackingLog tl WHERE tl.recordedAt < :cutoff")
    int deleteByRecordedAtBefore(@Param("cutoff") LocalDateTime cutoff);
}

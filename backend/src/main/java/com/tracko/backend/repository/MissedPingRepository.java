package com.tracko.backend.repository;

import com.tracko.backend.model.MissedPing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MissedPingRepository extends JpaRepository<MissedPing, Long> {

    List<MissedPing> findByUserIdAndIsResolvedFalse(Long userId);

    List<MissedPing> findByIsResolvedFalse();

    @Query("SELECT mp FROM MissedPing mp WHERE mp.isResolved = false " +
           "AND mp.escalationLevel < :maxLevel AND mp.createdAt < :since")
    List<MissedPing> findUnresolvedNeedingEscalation(@Param("maxLevel") int maxLevel,
                                                     @Param("since") LocalDateTime since);

    List<MissedPing> findByCreatedAtBefore(LocalDateTime cutoff);
}

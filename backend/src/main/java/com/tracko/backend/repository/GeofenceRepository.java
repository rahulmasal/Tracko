package com.tracko.backend.repository;

import com.tracko.backend.model.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, Long> {
    List<Geofence> findByIsActiveTrue();
    List<Geofence> findByBranchId(Long branchId);
    List<Geofence> findByType(String type);
}

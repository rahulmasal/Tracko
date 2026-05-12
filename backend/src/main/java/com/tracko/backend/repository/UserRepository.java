package com.tracko.backend.repository;

import com.tracko.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByMobile(String mobile);

    Optional<User> findByEmployeeCode(String employeeCode);

    List<User> findByManagerId(Long managerId);

    List<User> findByBranchId(Long branchId);

    List<User> findByDepartmentId(Long departmentId);

    List<User> findByIsActiveTrue();

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    boolean existsByEmployeeCode(String employeeCode);

    @Query("SELECT u FROM User u WHERE u.managerId = :managerId AND u.isActive = true")
    List<User> findActiveByManagerId(@Param("managerId") Long managerId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
}

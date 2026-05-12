package com.tracko.backend.service;

import com.tracko.backend.exception.DuplicateResourceException;
import com.tracko.backend.exception.ResourceNotFoundException;
import com.tracko.backend.model.Role;
import com.tracko.backend.model.User;
import com.tracko.backend.repository.RoleRepository;
import com.tracko.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Transactional
    public User createUser(User user, Set<Long> roleIds) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }
        if (userRepository.existsByMobile(user.getMobile())) {
            throw new DuplicateResourceException("User", "mobile", user.getMobile());
        }
        if (user.getEmployeeCode() != null && userRepository.existsByEmployeeCode(user.getEmployeeCode())) {
            throw new DuplicateResourceException("User", "employeeCode", user.getEmployeeCode());
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = Set.copyOf(roleRepository.findAllById(roleIds));
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User updated, Set<Long> roleIds) {
        User existing = getUserById(id);

        if (updated.getFullName() != null) existing.setFullName(updated.getFullName());
        if (updated.getDesignation() != null) existing.setDesignation(updated.getDesignation());
        if (updated.getMobile() != null) {
            if (!existing.getMobile().equals(updated.getMobile()) &&
                userRepository.existsByMobile(updated.getMobile())) {
                throw new DuplicateResourceException("User", "mobile", updated.getMobile());
            }
            existing.setMobile(updated.getMobile());
        }
        if (updated.getDepartmentId() != null) existing.setDepartmentId(updated.getDepartmentId());
        if (updated.getBranchId() != null) existing.setBranchId(updated.getBranchId());
        if (updated.getManagerId() != null) existing.setManagerId(updated.getManagerId());
        if (updated.getShiftId() != null) existing.setShiftId(updated.getShiftId());
        if (updated.getProfilePhotoUrl() != null) existing.setProfilePhotoUrl(updated.getProfilePhotoUrl());

        if (roleIds != null) {
            Set<Role> roles = Set.copyOf(roleRepository.findAllById(roleIds));
            existing.setRoles(roles);
        }

        return userRepository.save(existing);
    }

    @Transactional
    public void toggleActive(Long id) {
        User user = getUserById(id);
        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        userRepository.save(user);
    }

    @Transactional
    public void toggleLocked(Long id) {
        User user = getUserById(id);
        user.setIsLocked(!Boolean.TRUE.equals(user.getIsLocked()));
        userRepository.save(user);
    }

    public List<User> getUsersByManager(Long managerId) {
        return userRepository.findByManagerId(managerId);
    }

    public List<User> getUsersByBranch(Long branchId) {
        return userRepository.findByBranchId(branchId);
    }

    @Transactional
    public User assignRoles(Long userId, Set<Long> roleIds) {
        User user = getUserById(userId);
        Set<Role> roles = Set.copyOf(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        return userRepository.save(user);
    }
}

package com.tracko.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_role_map")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserRoleMap.UserRoleMapId.class)
public class UserRoleMap {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "assigned_by")
    private Long assignedBy;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRoleMapId implements Serializable {
        private Long userId;
        private Long roleId;
    }

    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) assignedAt = LocalDateTime.now();
    }
}

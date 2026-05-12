package com.tracko.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    @ColumnInfo(name = "employee_code") val employeeCode: String? = null,
    @ColumnInfo(name = "employee_name") val employeeName: String? = null,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "mobile") val mobile: String? = null,
    @ColumnInfo(name = "role") val role: String? = null,
    @ColumnInfo(name = "designation") val designation: String? = null,
    @ColumnInfo(name = "department") val department: String? = null,
    @ColumnInfo(name = "manager_id") val managerId: String? = null,
    @ColumnInfo(name = "profile_image_url") val profileImageUrl: String? = null,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "last_sync_at") val lastSyncAt: Long? = null
)

package com.tracko.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tracko.app.data.local.dao.AttendanceDao
import com.tracko.app.data.local.dao.CallReportDao
import com.tracko.app.data.local.dao.EnquiryDao
import com.tracko.app.data.local.dao.LeaveRequestDao
import com.tracko.app.data.local.dao.NotificationDao
import com.tracko.app.data.local.dao.VisitDao
import com.tracko.app.data.local.dao.UserDao
import com.tracko.app.data.local.entity.AttendanceEntity
import com.tracko.app.data.local.entity.CallReportEntity
import com.tracko.app.data.local.entity.EnquiryEntity
import com.tracko.app.data.local.entity.LeaveRequestEntity
import com.tracko.app.data.local.entity.NotificationEntity
import com.tracko.app.data.local.entity.SyncQueueEntity
import com.tracko.app.data.local.entity.TrackingLogEntity
import com.tracko.app.data.local.entity.UserEntity
import com.tracko.app.data.local.entity.VisitEntity

@Database(
    entities = [
        AttendanceEntity::class,
        VisitEntity::class,
        CallReportEntity::class,
        EnquiryEntity::class,
        LeaveRequestEntity::class,
        TrackingLogEntity::class,
        NotificationEntity::class,
        SyncQueueEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
    abstract fun visitDao(): VisitDao
    abstract fun callReportDao(): CallReportDao
    abstract fun enquiryDao(): EnquiryDao
    abstract fun leaveRequestDao(): LeaveRequestDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "tracko_db"
    }
}

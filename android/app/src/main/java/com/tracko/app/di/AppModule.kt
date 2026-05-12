package com.tracko.app.di

import android.content.Context
import androidx.room.Room
import com.tracko.app.data.local.AppDatabase
import com.tracko.app.data.local.dao.AttendanceDao
import com.tracko.app.data.local.dao.CallReportDao
import com.tracko.app.data.local.dao.EnquiryDao
import com.tracko.app.data.local.dao.LeaveRequestDao
import com.tracko.app.data.local.dao.NotificationDao
import com.tracko.app.data.local.dao.VisitDao
import com.tracko.app.data.remote.interceptor.AppVersionProvider
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.util.SyncManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAttendanceDao(database: AppDatabase): AttendanceDao = database.attendanceDao()

    @Provides
    fun provideVisitDao(database: AppDatabase): VisitDao = database.visitDao()

    @Provides
    fun provideCallReportDao(database: AppDatabase): CallReportDao = database.callReportDao()

    @Provides
    fun provideEnquiryDao(database: AppDatabase): EnquiryDao = database.enquiryDao()

    @Provides
    fun provideLeaveRequestDao(database: AppDatabase): LeaveRequestDao = database.leaveRequestDao()

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao = database.notificationDao()

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideAppVersionProvider(@ApplicationContext context: Context): AppVersionProvider {
        return AppVersionProviderImpl(context)
    }

    @Provides
    @Singleton
    fun provideSyncManager(
        @ApplicationContext context: Context,
        gson: Gson,
        tokenManager: TokenManager
    ): SyncManager {
        return SyncManager(context, gson, tokenManager)
    }
}

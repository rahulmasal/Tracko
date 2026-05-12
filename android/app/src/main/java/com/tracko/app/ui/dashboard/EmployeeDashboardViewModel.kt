package com.tracko.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracko.app.data.local.entity.AttendanceEntity
import com.tracko.app.data.local.entity.VisitEntity
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.data.repository.AttendanceRepository
import com.tracko.app.data.repository.LeaveRepository
import com.tracko.app.data.repository.NotificationRepository
import com.tracko.app.data.repository.VisitRepository
import com.tracko.app.util.DateTimeUtils
import com.tracko.app.util.NetworkUtils
import com.tracko.app.util.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val employeeName: String? = null,
    val todayAttendance: AttendanceEntity? = null,
    val todayVisitCount: Int = 0,
    val completedVisitCount: Int = 0,
    val pendingReportCount: Int = 0,
    val unreadNotificationCount: Int = 0,
    val currentMonthScore: Int? = null,
    val previousMonthScore: Int? = null,
    val leaveBalance: String? = null,
    val isOnline: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class EmployeeDashboardViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val visitRepository: VisitRepository,
    private val leaveRepository: LeaveRepository,
    private val notificationRepository: NotificationRepository,
    private val tokenManager: TokenManager,
    private val networkUtils: NetworkUtils,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val userId = tokenManager.getUserId() ?: return@launch
            val isOnline = networkUtils.isOnline()
            val today = DateTimeUtils.todayDate()

            _uiState.update { it.copy(isOnline = isOnline) }

            try {
                attendanceRepository.getTodayAttendance(userId).collect { attendance ->
                    _uiState.update { it.copy(todayAttendance = attendance) }
                }
            } catch (_: Exception) { }

            try {
                visitRepository.getTodayVisits(userId).collect { visits ->
                    val total = visits.size
                    val completed = visits.count { it.status == "completed" }
                    _uiState.update { it.copy(todayVisitCount = total, completedVisitCount = completed) }
                }
            } catch (_: Exception) { }

            try {
                notificationRepository.getUnreadCount(userId).collect { count ->
                    _uiState.update { it.copy(unreadNotificationCount = count) }
                }
            } catch (_: Exception) { }

            try {
                val userData = tokenManager::class.java
                _uiState.update { it.copy(employeeName = tokenManager.getUserId()) }
            } catch (_: Exception) { }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun refresh() {
        loadDashboard()
    }
}

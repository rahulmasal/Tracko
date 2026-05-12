package com.tracko.app.ui.leaves

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracko.app.data.local.entity.LeaveRequestEntity
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.data.repository.LeaveRepository
import com.tracko.app.data.repository.LeaveBalanceResult
import com.tracko.app.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaveApplyUiState(
    val leaveType: String = "annual",
    val startDate: String = "",
    val endDate: String = "",
    val totalDays: String = "0",
    val reason: String = "",
    val contactDuringLeave: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val leaveBalances: Map<String, String> = emptyMap()
)

data class LeaveHistoryUiState(
    val isLoading: Boolean = true,
    val leaves: List<LeaveRequestEntity> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class LeaveViewModel @Inject constructor(
    private val leaveRepository: LeaveRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _applyState = MutableStateFlow(LeaveApplyUiState())
    val applyState: StateFlow<LeaveApplyUiState> = _applyState.asStateFlow()

    private val _historyState = MutableStateFlow(LeaveHistoryUiState())
    val historyState: StateFlow<LeaveHistoryUiState> = _historyState.asStateFlow()

    fun loadLeaveHistory() {
        viewModelScope.launch {
            _historyState.update { it.copy(isLoading = true) }
            val userId = tokenManager.getUserId() ?: return@launch
            leaveRepository.getLeavesByUser(userId).collect { leaves ->
                _historyState.update { it.copy(isLoading = false, leaves = leaves) }
            }
        }
    }

    fun setLeaveType(type: String) {
        _applyState.update { it.copy(leaveType = type) }
        recalculateDays()
    }

    fun setStartDate(date: String) {
        _applyState.update { it.copy(startDate = date) }
        recalculateDays()
    }

    fun setEndDate(date: String) {
        _applyState.update { it.copy(endDate = date) }
        recalculateDays()
    }

    fun setReason(reason: String) {
        _applyState.update { it.copy(reason = reason) }
    }

    fun setContactDuringLeave(contact: String) {
        _applyState.update { it.copy(contactDuringLeave = contact) }
    }

    private fun recalculateDays() {
        val state = _applyState.value
        if (state.startDate.isNotBlank() && state.endDate.isNotBlank()) {
            val days = DateTimeUtils.diffDays(state.startDate, state.endDate)
            _applyState.update { it.copy(totalDays = maxOf(1, days).toString()) }
        }
    }

    fun submitLeave() {
        viewModelScope.launch {
            _applyState.update { it.copy(isSubmitting = true, error = null) }

            val state = _applyState.value
            if (state.startDate.isBlank() || state.endDate.isBlank()) {
                _applyState.update { it.copy(isSubmitting = false, error = "Please select start and end dates") }
                return@launch
            }

            val entity = LeaveRequestEntity(
                leaveType = state.leaveType,
                startDate = state.startDate,
                endDate = state.endDate,
                totalDays = state.totalDays.toDoubleOrNull() ?: 1.0,
                reason = state.reason.ifBlank { null },
                contactDuringLeave = state.contactDuringLeave.ifBlank { null }
            )

            val result = leaveRepository.applyLeave(entity)
            result.fold(
                onSuccess = { _applyState.update { it.copy(isSubmitting = false, isSuccess = true) } },
                onFailure = { _applyState.update { it.copy(isSubmitting = false, error = it.message ?: "Failed to apply leave") } }
            )
        }
    }

    fun loadLeaveBalance() {
        viewModelScope.launch {
            val result = leaveRepository.getLeaveBalance()
            if (result is LeaveBalanceResult.Success) {
                val balance = result.balance
                val map = mutableMapOf<String, String>()
                balance.annual?.let { map["annual"] = "${it.remaining} remaining" }
                balance.sick?.let { map["sick"] = "${it.remaining} remaining" }
                balance.personal?.let { map["personal"] = "${it.remaining} remaining" }
                balance.casual?.let { map["casual"] = "${it.remaining} remaining" }
                _applyState.update { it.copy(leaveBalances = map) }
            }
        }
    }
}

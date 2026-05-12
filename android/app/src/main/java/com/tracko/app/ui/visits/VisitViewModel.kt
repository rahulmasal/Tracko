package com.tracko.app.ui.visits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracko.app.data.local.entity.VisitEntity
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.data.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VisitListUiState(
    val isLoading: Boolean = true,
    val visits: List<VisitEntity> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null
)

data class VisitDetailUiState(
    val isLoading: Boolean = true,
    val visit: VisitEntity? = null,
    val isCheckingIn: Boolean = false,
    val isCheckingOut: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class VisitViewModel @Inject constructor(
    private val visitRepository: VisitRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _listState = MutableStateFlow(VisitListUiState())
    val listState: StateFlow<VisitListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(VisitDetailUiState())
    val detailState: StateFlow<VisitDetailUiState> = _detailState.asStateFlow()

    fun loadVisits() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true) }
            val userId = tokenManager.getUserId() ?: return@launch

            visitRepository.getTodayVisits(userId).collect { visits ->
                _listState.update {
                    it.copy(isLoading = false, visits = visits)
                }
            }
        }
    }

    fun loadVisitDetail(visitId: Long) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true) }
            visitRepository.getVisitByIdFlow(visitId).collect { visit ->
                _detailState.update {
                    it.copy(isLoading = false, visit = visit)
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _listState.update { it.copy(selectedTab = index) }
    }

    fun getFilteredVisits(): List<VisitEntity> {
        val state = _listState.value
        return when (state.selectedTab) {
            0 -> state.visits
            1 -> state.visits.filter { it.status == "completed" }
            2 -> state.visits.filter { it.status == "missed" || it.status == "planned" }
            else -> state.visits
        }
    }

    fun checkInVisit(visitId: Long, lat: Double, lng: Double) {
        viewModelScope.launch {
            _detailState.update { it.copy(isCheckingIn = true, error = null) }
            val result = visitRepository.checkInVisit(visitId, lat, lng)
            result.fold(
                onSuccess = {
                    _detailState.update { it.copy(isCheckingIn = false, successMessage = "Checked in successfully") }
                },
                onFailure = {
                    _detailState.update { it.copy(isCheckingIn = false, error = it.message ?: "Check-in failed") }
                }
            )
        }
    }

    fun checkOutVisit(visitId: Long, lat: Double, lng: Double, remarks: String? = null) {
        viewModelScope.launch {
            _detailState.update { it.copy(isCheckingOut = true, error = null) }
            val result = visitRepository.checkOutVisit(visitId, lat, lng, remarks)
            result.fold(
                onSuccess = {
                    _detailState.update { it.copy(isCheckingOut = false, successMessage = "Checked out successfully") }
                },
                onFailure = {
                    _detailState.update { it.copy(isCheckingOut = false, error = it.message ?: "Check-out failed") }
                }
            )
        }
    }

    fun clearMessages() {
        _detailState.update { it.copy(error = null, successMessage = null) }
    }
}

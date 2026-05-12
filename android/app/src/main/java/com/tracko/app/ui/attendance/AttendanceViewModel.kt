package com.tracko.app.ui.attendance

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracko.app.data.repository.AttendanceRepository
import com.tracko.app.security.DeviceSecurityChecker
import com.tracko.app.util.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AttendanceUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val location: Location? = null,
    val locationAccuracy: String = "",
    val selfieUri: String? = null,
    val remarks: String = "",
    val deviceRiskScore: Int = 0,
    val geoFenceValidated: Boolean = false,
    val currentTimestamp: String = "",
    val error: String? = null,
    val isSuccess: Boolean = false,
    val timeSinceCheckIn: String = ""
)

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val attendanceRepository: AttendanceRepository,
    private val deviceSecurityChecker: DeviceSecurityChecker
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val securityResult = deviceSecurityChecker.performSecurityCheck()
            _uiState.update { it.copy(deviceRiskScore = securityResult.riskScore) }

            val location = LocationUtils.getCurrentLocation(context)
            if (location != null) {
                _uiState.update {
                    it.copy(
                        location = location,
                        locationAccuracy = LocationUtils.getLocationAccuracy(location.accuracy ?: 0f)
                    )
                }
            }

            _uiState.update {
                it.copy(
                    currentTimestamp = java.text.SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a", java.util.Locale.US
                    ).format(java.util.Date()),
                    isLoading = false
                )
            }
        }
    }

    fun updateSelfieUri(uri: String) {
        _uiState.update { it.copy(selfieUri = uri) }
    }

    fun updateRemarks(remarks: String) {
        _uiState.update { it.copy(remarks = remarks) }
    }

    fun checkIn() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }

            val location = _uiState.value.location
            if (location == null) {
                _uiState.update { it.copy(isSubmitting = false, error = "Location not available") }
                return@launch
            }

            val result = attendanceRepository.checkIn(
                location = location,
                deviceRiskScore = _uiState.value.deviceRiskScore,
                geoFenceValidated = _uiState.value.geoFenceValidated,
                selfieBase64 = _uiState.value.selfieUri
            )

            result.fold(
                onSuccess = { _uiState.update { it.copy(isSubmitting = false, isSuccess = true) } },
                onFailure = { _uiState.update { it.copy(isSubmitting = false, error = it.message ?: "Check-in failed") } }
            )
        }
    }

    fun checkOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }

            val location = _uiState.value.location
            if (location == null) {
                _uiState.update { it.copy(isSubmitting = false, error = "Location not available") }
                return@launch
            }

            val result = attendanceRepository.checkOut(
                location = location,
                remarks = _uiState.value.remarks.ifBlank { null },
                selfieBase64 = _uiState.value.selfieUri
            )

            result.fold(
                onSuccess = { _uiState.update { it.copy(isSubmitting = false, isSuccess = true) } },
                onFailure = { _uiState.update { it.copy(isSubmitting = false, error = it.message ?: "Check-out failed") } }
            )
        }
    }

    fun refreshLocation() {
        viewModelScope.launch {
            val location = LocationUtils.getCurrentLocation(context)
            if (location != null) {
                _uiState.update {
                    it.copy(
                        location = location,
                        locationAccuracy = LocationUtils.getLocationAccuracy(location.accuracy ?: 0f)
                    )
                }
            }
        }
    }
}

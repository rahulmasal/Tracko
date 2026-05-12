package com.tracko.app.ui.reports

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracko.app.data.local.entity.CallReportEntity
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.data.repository.CallReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportFormUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSaving: Boolean = false,
    val customerName: String = "",
    val customerId: String = "",
    val contactPerson: String = "",
    val contactNumber: String = "",
    val siteName: String = "",
    val siteAddress: String = "",
    val visitType: String = "regular",
    val problemReported: String = "",
    val observation: String = "",
    val workDone: String = "",
    val partsUsed: String = "",
    val resolutionStatus: String = "open",
    val pendingIssue: String = "",
    val nextAction: String = "",
    val nextFollowupDate: String = "",
    val timeSpentMinutes: String = "",
    val customerRemarks: String = "",
    val customerRating: Int = 0,
    val engineerRemarks: String = "",
    val beforePhotos: List<Uri> = emptyList(),
    val afterPhotos: List<Uri> = emptyList(),
    val signatureUri: Uri? = null,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val successMessage: String? = null
)

data class ReportHistoryUiState(
    val isLoading: Boolean = true,
    val reports: List<CallReportEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "all",
    val error: String? = null
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val callReportRepository: CallReportRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _formState = MutableStateFlow(ReportFormUiState())
    val formState: StateFlow<ReportFormUiState> = _formState.asStateFlow()

    private val _historyState = MutableStateFlow(ReportHistoryUiState())
    val historyState: StateFlow<ReportHistoryUiState> = _historyState.asStateFlow()

    fun loadReports() {
        viewModelScope.launch {
            _historyState.update { it.copy(isLoading = true) }
            val userId = tokenManager.getUserId() ?: return@launch

            callReportRepository.getReportsByUser(userId).collect { reports ->
                _historyState.update {
                    it.copy(isLoading = false, reports = reports)
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _historyState.update { it.copy(searchQuery = query) }
    }

    fun updateFilter(filter: String) {
        _historyState.update { it.copy(selectedFilter = filter) }
    }

    fun getFilteredReports(): List<CallReportEntity> {
        val state = _historyState.value
        var reports = state.reports

        if (state.selectedFilter != "all") {
            reports = reports.filter { it.submissionStatus == state.selectedFilter }
        }

        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase()
            reports = reports.filter {
                it.customerName.lowercase().contains(query) ||
                        it.reportNumber.lowercase().contains(query)
            }
        }

        return reports
    }

    fun updateFormField(field: String, value: String) {
        _formState.update { state ->
            when (field) {
                "customerName" -> state.copy(customerName = value)
                "customerId" -> state.copy(customerId = value)
                "contactPerson" -> state.copy(contactPerson = value)
                "contactNumber" -> state.copy(contactNumber = value)
                "siteName" -> state.copy(siteName = value)
                "siteAddress" -> state.copy(siteAddress = value)
                "visitType" -> state.copy(visitType = value)
                "problemReported" -> state.copy(problemReported = value)
                "observation" -> state.copy(observation = value)
                "workDone" -> state.copy(workDone = value)
                "partsUsed" -> state.copy(partsUsed = value)
                "resolutionStatus" -> state.copy(resolutionStatus = value)
                "pendingIssue" -> state.copy(pendingIssue = value)
                "nextAction" -> state.copy(nextAction = value)
                "nextFollowupDate" -> state.copy(nextFollowupDate = value)
                "timeSpentMinutes" -> state.copy(timeSpentMinutes = value)
                "customerRemarks" -> state.copy(customerRemarks = value)
                "engineerRemarks" -> state.copy(engineerRemarks = value)
                else -> state
            }
        }
    }

    fun updateCustomerRating(rating: Int) {
        _formState.update { it.copy(customerRating = rating) }
    }

    fun addBeforePhoto(uri: Uri) {
        _formState.update { it.copy(beforePhotos = it.beforePhotos + uri) }
    }

    fun removeBeforePhoto(uri: Uri) {
        _formState.update { it.copy(beforePhotos = it.beforePhotos - uri) }
    }

    fun addAfterPhoto(uri: Uri) {
        _formState.update { it.copy(afterPhotos = it.afterPhotos + uri) }
    }

    fun removeAfterPhoto(uri: Uri) {
        _formState.update { it.copy(afterPhotos = it.afterPhotos - uri) }
    }

    fun updateSignatureUri(uri: Uri?) {
        _formState.update { it.copy(signatureUri = uri) }
    }

    fun saveDraft() {
        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true, error = null) }

            val userId = tokenManager.getUserId() ?: return@launch
            val state = _formState.value

            val entity = CallReportEntity(
                userId = userId,
                customerId = state.customerId,
                customerName = state.customerName,
                contactPerson = state.contactPerson,
                contactNumber = state.contactNumber,
                siteName = state.siteName,
                siteAddress = state.siteAddress,
                visitDate = com.tracko.app.util.DateTimeUtils.todayDate(),
                visitType = state.visitType,
                problemReported = state.problemReported,
                observation = state.observation,
                workDone = state.workDone,
                partsUsed = state.partsUsed,
                resolutionStatus = state.resolutionStatus,
                pendingIssue = state.pendingIssue,
                nextAction = state.nextAction,
                nextFollowupDate = state.nextFollowupDate,
                timeSpentMinutes = state.timeSpentMinutes.toIntOrNull(),
                customerRemarks = state.customerRemarks,
                customerRating = state.customerRating,
                engineerRemarks = state.engineerRemarks,
                submissionStatus = "draft"
            )

            val result = callReportRepository.saveDraft(entity)
            result.fold(
                onSuccess = {
                    _formState.update { it.copy(isSaving = false, isSuccess = true, successMessage = "Draft saved") }
                },
                onFailure = {
                    _formState.update { it.copy(isSaving = false, error = it.message ?: "Failed to save draft") }
                }
            )
        }
    }

    fun submitReport() {
        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, error = null) }

            val state = _formState.value
            if (state.customerName.isBlank()) {
                _formState.update { it.copy(isSubmitting = false, error = "Customer name is required") }
                return@launch
            }

            val userId = tokenManager.getUserId() ?: return@launch

            val entity = CallReportEntity(
                userId = userId,
                customerId = state.customerId,
                customerName = state.customerName,
                contactPerson = state.contactPerson,
                contactNumber = state.contactNumber,
                siteName = state.siteName,
                siteAddress = state.siteAddress,
                visitDate = com.tracko.app.util.DateTimeUtils.todayDate(),
                visitType = state.visitType,
                problemReported = state.problemReported,
                observation = state.observation,
                workDone = state.workDone,
                partsUsed = state.partsUsed,
                resolutionStatus = state.resolutionStatus,
                pendingIssue = state.pendingIssue,
                nextAction = state.nextAction,
                nextFollowupDate = state.nextFollowupDate,
                timeSpentMinutes = state.timeSpentMinutes.toIntOrNull(),
                customerRemarks = state.customerRemarks,
                customerRating = state.customerRating,
                engineerRemarks = state.engineerRemarks,
                submissionStatus = "submitted"
            )

            val result = callReportRepository.submitReport(entity)
            result.fold(
                onSuccess = {
                    _formState.update { it.copy(isSubmitting = false, isSuccess = true, successMessage = "Report submitted") }
                },
                onFailure = {
                    _formState.update { it.copy(isSubmitting = false, error = it.message ?: "Failed to submit") }
                }
            )
        }
    }

    fun clearForm() {
        _formState.update { ReportFormUiState() }
    }
}

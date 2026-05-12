package com.tracko.app.ui.enquiries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracko.app.data.local.entity.EnquiryEntity
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.data.repository.EnquiryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EnquiryFormUiState(
    val customerName: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val contactPerson: String = "",
    val contactNumber: String = "",
    val requirementDescription: String = "",
    val problemStatement: String = "",
    val category: String = "",
    val priority: String = "medium",
    val budgetRange: String = "",
    val source: String = "",
    val notes: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

data class EnquiryListUiState(
    val isLoading: Boolean = true,
    val enquiries: List<EnquiryEntity> = emptyList(),
    val selectedStatus: String = "all",
    val error: String? = null
)

@HiltViewModel
class EnquiryViewModel @Inject constructor(
    private val enquiryRepository: EnquiryRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _formState = MutableStateFlow(EnquiryFormUiState())
    val formState: StateFlow<EnquiryFormUiState> = _formState.asStateFlow()

    private val _listState = MutableStateFlow(EnquiryListUiState())
    val listState: StateFlow<EnquiryListUiState> = _listState.asStateFlow()

    fun loadEnquiries() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true) }
            val userId = tokenManager.getUserId() ?: return@launch
            enquiryRepository.getEnquiriesByUser(userId).collect { enquiries ->
                _listState.update { it.copy(isLoading = false, enquiries = enquiries) }
            }
        }
    }

    fun updateFormField(field: String, value: String) {
        _formState.update { state ->
            when (field) {
                "customerName" -> state.copy(customerName = value)
                "customerPhone" -> state.copy(customerPhone = value)
                "customerEmail" -> state.copy(customerEmail = value)
                "contactPerson" -> state.copy(contactPerson = value)
                "contactNumber" -> state.copy(contactNumber = value)
                "requirementDescription" -> state.copy(requirementDescription = value)
                "problemStatement" -> state.copy(problemStatement = value)
                "category" -> state.copy(category = value)
                "priority" -> state.copy(priority = value)
                "budgetRange" -> state.copy(budgetRange = value)
                "source" -> state.copy(source = value)
                "notes" -> state.copy(notes = value)
                else -> state
            }
        }
    }

    fun submitEnquiry() {
        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, error = null) }

            val state = _formState.value
            if (state.customerName.isBlank()) {
                _formState.update { it.copy(isSubmitting = false, error = "Customer name is required") }
                return@launch
            }
            if (state.requirementDescription.isBlank()) {
                _formState.update { it.copy(isSubmitting = false, error = "Requirement description is required") }
                return@launch
            }

            val entity = EnquiryEntity(
                customerName = state.customerName,
                customerPhone = state.customerPhone.ifBlank { null },
                customerEmail = state.customerEmail.ifBlank { null },
                contactPerson = state.contactPerson.ifBlank { null },
                contactNumber = state.contactNumber.ifBlank { null },
                requirementDescription = state.requirementDescription,
                problemStatement = state.problemStatement.ifBlank { null },
                category = state.category.ifBlank { null },
                priority = state.priority,
                budgetRange = state.budgetRange.ifBlank { null },
                source = state.source.ifBlank { null },
                notes = state.notes.ifBlank { null }
            )

            val result = enquiryRepository.createEnquiry(entity)
            result.fold(
                onSuccess = { _formState.update { it.copy(isSubmitting = false, isSuccess = true) } },
                onFailure = { _formState.update { it.copy(isSubmitting = false, error = it.message ?: "Failed to create enquiry") } }
            )
        }
    }

    fun setStatusFilter(status: String) {
        _listState.update { it.copy(selectedStatus = status) }
    }

    fun getFilteredEnquiries(): List<EnquiryEntity> {
        val state = _listState.value
        return if (state.selectedStatus == "all") state.enquiries
        else state.enquiries.filter { it.status == state.selectedStatus }
    }

    fun clearForm() {
        _formState.update { EnquiryFormUiState() }
    }
}

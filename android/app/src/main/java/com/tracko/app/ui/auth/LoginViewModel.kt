package com.tracko.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracko.app.data.repository.AuthRepository
import com.tracko.app.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val mobile: String = "",
    val otp: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false,
    val loginMode: LoginMode = LoginMode.PASSWORD,
    val employeeName: String? = null,
    val userRole: String? = null
)

enum class LoginMode { PASSWORD, OTP }

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateUsername(value: String) {
        _uiState.update { it.copy(username = value, error = null) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun updateMobile(value: String) {
        _uiState.update { it.copy(mobile = value, error = null) }
    }

    fun updateOtp(value: String) {
        _uiState.update { it.copy(otp = value, error = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun setLoginMode(mode: LoginMode) {
        _uiState.update { it.copy(loginMode = mode, error = null) }
    }

    fun login() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Please enter username and password") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.login(state.username, state.password)

            launch {
                authRepository.authState.collect { result ->
                    when (result) {
                        is AuthResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is AuthResult.Success -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                isLoginSuccessful = true,
                                employeeName = result.loginResponse.employeeName,
                                userRole = result.loginResponse.role
                            )
                        }
                        is AuthResult.Error -> _uiState.update {
                            it.copy(isLoading = false, error = result.message)
                        }
                    }
                }
            }
        }
    }

    fun loginWithOtp() {
        val state = _uiState.value
        if (state.mobile.isBlank() || state.mobile.length < 10) {
            _uiState.update { it.copy(error = "Please enter a valid mobile number") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.loginWithOtp(state.mobile)
        }
    }

    fun verifyOtp() {
        val state = _uiState.value
        if (state.otp.isBlank() || state.otp.length < 4) {
            _uiState.update { it.copy(error = "Please enter a valid OTP") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.verifyOtp(state.mobile, state.otp)
        }
    }

    fun resetState() {
        _uiState.update { LoginUiState() }
    }
}

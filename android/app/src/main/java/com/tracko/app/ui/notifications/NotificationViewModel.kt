package com.tracko.app.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracko.app.data.local.entity.NotificationEntity
import com.tracko.app.data.remote.interceptor.TokenManager
import com.tracko.app.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationEntity> = emptyList(),
    val unreadCount: Int = 0,
    val error: String? = null
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = tokenManager.getUserId() ?: return@launch

            notificationRepository.fetchNotificationsFromServer(userId)

            notificationRepository.getNotifications(userId).collect { notifications ->
                _uiState.update { it.copy(isLoading = false, notifications = notifications) }
            }

            notificationRepository.getUnreadCount(userId).collect { count ->
                _uiState.update { it.copy(unreadCount = count) }
            }
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val userId = tokenManager.getUserId() ?: return@launch
            notificationRepository.markAllAsRead(userId)
        }
    }
}

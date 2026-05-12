package com.tracko.app.ui.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracko.app.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScoreBreakdown(
    val label: String,
    val score: Int,
    val maxScore: Int = 100,
    val color: Long = 0xFF4CAF50
)

data class ScoreUiState(
    val isLoading: Boolean = true,
    val overallScore: Int = 0,
    val scoreColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
    val breakdowns: List<ScoreBreakdown> = emptyList(),
    val teamRank: Int? = null,
    val teamSize: Int? = null,
    val previousMonthScore: Int? = null,
    val trend: String = "stable",
    val month: String = DateTimeUtils.currentMonthPattern()
)

@HiltViewModel
class ScoreViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ScoreUiState())
    val uiState: StateFlow<ScoreUiState> = _uiState.asStateFlow()

    init {
        loadScore()
    }

    fun loadScore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Mock data - in production, fetch from API
            val breakdowns = listOf(
                ScoreBreakdown("Attendance", 85, 100, 0xFF4CAF50),
                ScoreBreakdown("Punctuality", 70, 100, 0xFFFFA000),
                ScoreBreakdown("Visit Compliance", 90, 100, 0xFF4CAF50),
                ScoreBreakdown("Report Quality", 75, 100, 0xFFFFA000),
                ScoreBreakdown("Customer Rating", 4, 5, 0xFF1565C0)
            )

            val overall = breakdowns.filter { it.maxScore == 100 }.let { list ->
                if (list.isEmpty()) 0 else list.sumOf { it.score } / list.size
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    overallScore = overall,
                    scoreColor = when {
                        overall >= 80 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                        overall >= 60 -> androidx.compose.ui.graphics.Color(0xFFFFA000)
                        else -> androidx.compose.ui.graphics.Color(0xFFD32F2F)
                    },
                    breakdowns = breakdowns,
                    teamRank = 3,
                    teamSize = 12,
                    previousMonthScore = 72,
                    trend = if (overall >= 72) "up" else "down"
                )
            }
        }
    }
}

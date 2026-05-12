package com.tracko.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tracko.app.ui.theme.Tertiary

@Composable
fun RatingBar(
    rating: Int,
    onRatingChanged: ((Int) -> Unit)? = null,
    maxStars: Int = 5,
    modifier: Modifier = Modifier,
    activeColor: Color = Tertiary,
    inactiveColor: Color = Color.Gray.copy(alpha = 0.3f),
    size: Int = 32
) {
    Row(modifier = modifier) {
        for (i in 1..maxStars) {
            IconButton(
                onClick = { onRatingChanged?.invoke(i) },
                modifier = Modifier.size(size.dp)
            ) {
                Icon(
                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Star $i",
                    tint = if (i <= rating) activeColor else inactiveColor,
                    modifier = Modifier.size(size.dp)
                )
            }
        }
    }
}

@Composable
fun DisplayRatingBar(
    rating: Float,
    maxStars: Int = 5,
    modifier: Modifier = Modifier,
    activeColor: Color = Tertiary,
    inactiveColor: Color = Color.Gray.copy(alpha = 0.3f),
    size: Int = 20
) {
    Row(modifier = modifier) {
        for (i in 1..maxStars) {
            Icon(
                imageVector = when {
                    i <= rating.toInt() -> Icons.Default.Star
                    i - rating <= 0.5f && rating % 1 > 0 -> Icons.Default.StarHalf
                    else -> Icons.Default.StarBorder
                },
                contentDescription = null,
                tint = if (i <= rating) activeColor else inactiveColor,
                modifier = Modifier.size(size.dp)
            )
        }
    }
}

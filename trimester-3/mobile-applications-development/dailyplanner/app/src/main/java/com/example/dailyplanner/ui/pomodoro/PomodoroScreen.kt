package com.example.dailyplanner.ui.pomodoro

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyplanner.ui.theme.*

@Composable
fun PomodoroScreen(viewModel: PomodoroViewModel = viewModel()) {
    val timeLeft by viewModel.timeLeft.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val currentMode by viewModel.currentMode.collectAsState()
    val sessionsCompleted by viewModel.sessionsCompleted.collectAsState()
    val totalDuration by viewModel.totalDuration.collectAsState()

    val progress by animateFloatAsState(
        targetValue = if (totalDuration > 0) timeLeft.toFloat() / totalDuration.toFloat() else 1f,
        animationSpec = tween(500),
        label = "progress"
    )

    val modeColor by animateColorAsState(
        targetValue = when (currentMode) {
            PomodoroMode.WORK -> MaterialTheme.colorScheme.primary
            PomodoroMode.SHORT_BREAK -> SuccessGreen
            PomodoroMode.LONG_BREAK -> RoseGold
        },
        label = "modeColor"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Focus Timer",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Stay focused, stay productive",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mode selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PomodoroMode.entries.forEach { mode ->
                FilterChip(
                    selected = currentMode == mode,
                    onClick = { viewModel.switchMode(mode) },
                    label = { Text(mode.label, style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = modeColor.copy(alpha = 0.2f),
                        selectedLabelColor = modeColor
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Timer circle
        Box(
            modifier = Modifier.size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            val trackColor = MaterialTheme.colorScheme.surfaceVariant
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 12.dp.toPx()
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                // Track
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Progress
                drawArc(
                    color = modeColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val minutes = (timeLeft / 1000) / 60
                val seconds = (timeLeft / 1000) % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentMode.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = modeColor
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset
            FilledTonalIconButton(
                onClick = { viewModel.reset() },
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Play/Pause
            FloatingActionButton(
                onClick = { viewModel.startPause() },
                modifier = Modifier.size(72.dp),
                containerColor = modeColor,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Start",
                    modifier = Modifier.size(36.dp)
                )
            }

            // Skip
            FilledTonalIconButton(
                onClick = {
                    when (currentMode) {
                        PomodoroMode.WORK -> viewModel.switchMode(PomodoroMode.SHORT_BREAK)
                        PomodoroMode.SHORT_BREAK, PomodoroMode.LONG_BREAK -> viewModel.switchMode(PomodoroMode.WORK)
                    }
                },
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    Icons.Filled.SkipNext,
                    contentDescription = "Skip",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sessions counter
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.WorkspacePremium,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Sessions Completed: $sessionsCompleted",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

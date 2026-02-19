package com.example.counterapps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.counterapps.ui.theme.CounterappsTheme
import com.example.counterapps.ui.theme.DeepPink
import com.example.counterapps.ui.theme.HotPink
import com.example.counterapps.ui.theme.LightPink
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CounterappsTheme {
                val viewModel: CounterViewModel = viewModel()
                CounterApp(viewModel)
            }
        }
    }
}

@Composable
fun CounterApp(viewModel: CounterViewModel) {
    val count by viewModel.count

    val animatedProgress by animateFloatAsState(targetValue = (count / 100f).coerceIn(0f, 1f))

    val targetColor by remember(count) {
        derivedStateOf {
            when {
                count > 0 -> HotPink
                count < 0 -> DeepPink
                else -> LightPink
            }
        }
    }

    val animatedColor by animateColorAsState(targetValue = targetColor)

    val gradientBrush = Brush.verticalGradient(
        listOf(animatedColor.copy(alpha = 0.6f), animatedColor)
    )

    val scale by animateFloatAsState(targetValue = if (count != 0) 1.1f else 1f, animationSpec = spring(dampingRatio = 0.3f, stiffness = 400f))

    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(count) {
        if (count != 0 && count % 10 == 0) {
            showConfetti = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (showConfetti) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                        position = Position.Relative(0.5, 0.3)
                    )
                ),
                
            )
        }
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .shadow(elevation = 8.dp, shape = CircleShape),
            shape = CircleShape,
        ) {
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 8.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.scale(scale)
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val interactionSourceDecrement = remember { MutableInteractionSource() }
            val isPressedDecrement by interactionSourceDecrement.collectIsPressedAsState()
            val colorDecrement by animateColorAsState(if (isPressedDecrement) HotPink else LightPink)

            val interactionSourceIncrement = remember { MutableInteractionSource() }
            val isPressedIncrement by interactionSourceIncrement.collectIsPressedAsState()
            val colorIncrement by animateColorAsState(if (isPressedIncrement) HotPink else LightPink)

            val interactionSourceReset = remember { MutableInteractionSource() }
            val isPressedReset by interactionSourceReset.collectIsPressedAsState()
            val colorReset by animateColorAsState(if (isPressedReset) HotPink else LightPink)

            FloatingActionButton(
                onClick = { viewModel.decrement() },
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = colorDecrement,
                interactionSource = interactionSourceDecrement
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrement")
            }
            FloatingActionButton(
                onClick = { viewModel.increment() },
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = colorIncrement,
                interactionSource = interactionSourceIncrement
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increment")
            }
            FloatingActionButton(
                onClick = { viewModel.reset() },
                modifier = Modifier.padding(start = 16.dp),
                containerColor = colorReset,
                interactionSource = interactionSourceReset
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset")
            }
        }
    }
}
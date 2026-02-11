package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                CalculatorApp()
            }
        }
    }
}

@Composable
fun CalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFFF9800),
            background = Color(0xFF1A1A1A),
            surface = Color(0xFF2D2D2D)
        ),
        content = content
    )
}

@Composable
fun CalculatorApp() {
    var display by remember { mutableStateOf("0") }
    var previousValue by remember { mutableStateOf<Double?>(null) }
    var operation by remember { mutableStateOf<String?>(null) }
    var shouldResetDisplay by remember { mutableStateOf(false) }

    // Box Layout - Main Container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF2D2D2D),
                        Color(0xFF1A1A1A)
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Column Layout - Vertical stacking
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF2D2D2D),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Display Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF1A1A1A),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(24.dp)
                    .heightIn(min = 120.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                if (operation != null && previousValue != null) {
                    Text(
                        text = "${formatNumber(previousValue!!)} $operation",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Text(
                    text = display,
                    fontSize = 48.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End,
                    maxLines = 2
                )
            }

            // Button Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CalcButton("C", Modifier.weight(1f), Color(0xFFDC3545)) {
                        display = "0"
                        previousValue = null
                        operation = null
                        shouldResetDisplay = false
                    }
                    CalcButton("⌫", Modifier.weight(1f), Color(0xFF495057)) {
                        display = if (display.length > 1) display.dropLast(1) else "0"
                    }
                    CalcButton("%", Modifier.weight(1f), Color(0xFF495057)) {
                        display = formatNumber(display.toDoubleOrNull()?.div(100) ?: 0.0)
                    }
                    CalcButton("÷", Modifier.weight(1f), Color(0xFFFF9800)) {
                        handleOp("÷", display, previousValue, operation, shouldResetDisplay,
                            { display = it }, { previousValue = it }, { operation = it }, { shouldResetDisplay = it })
                    }
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CalcButton("7", Modifier.weight(1f)) {
                        display = handleNum("7", display, shouldResetDisplay)
                        shouldResetDisplay = false
                    }
                    CalcButton("8", Modifier.weight(1f)) {
                        display = handleNum("8", display, shouldResetDisplay)
                        shouldResetDisplay = false
                    }
                    CalcButton("9", Modifier.weight(1f)) {
                        display = handleNum("9", display, shouldResetDisplay)
                        shouldResetDisplay = false
                    }
                    CalcButton("×", Modifier.weight(1f), Color(0xFFFF9800)) {
                        handleOp("×", display, previousValue, operation, shouldResetDisplay,
                            { display = it }, { previousValue = it }, { operation = it }, { shouldResetDisplay = it })
                    }
                }

                // Row 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CalcButton("4", Modifier.weight(1f)) {
                        display = handleNum("4", display, shouldResetDisplay)
                        shouldResetDisplay = false
                    }
                    CalcButton("5", Modifier.weight(1f)) {
                        display = handleNum("5", display, shouldResetDisplay)
                        shouldResetDisplay = false
                    }
                    CalcButton("6", Modifier.weight(1f)) {
                        display = handleNum("6", display, shouldResetDisplay)
                        shouldResetDisplay = false
                    }
                    CalcButton("−", Modifier.weight(1f), Color(0xFFFF9800)) {
                        handleOp("−", display, previousValue, operation, shouldResetDisplay,
                            { display = it }, { previousValue = it }, { operation = it }, { shouldResetDisplay = it })
                    }
                }

                // Row 4
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CalcButton("1", Modifier.weight(1f)) {
                        display = handleNum("1", display, shouldResetDisplay)
                        shouldResetDisplay = false
                    }
                    CalcButton("2", Modifier.weight(1f)) {
                        display = handleNum("2", display, shouldResetDisplay)
                        shouldResetDisplay = false
                    }
                    CalcButton("3", Modifier.weight(1f)) {
                        display = handleNum("3", display, shouldResetDisplay)
                        shouldResetDisplay = false
                    }
                    CalcButton("+", Modifier.weight(1f), Color(0xFFFF9800)) {
                        handleOp("+", display, previousValue, operation, shouldResetDisplay,
                            { display = it }, { previousValue = it }, { operation = it }, { shouldResetDisplay = it })
                    }
                }

                // Row 5
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CalcButton("0", Modifier.weight(2f)) {
                        display = handleNum("0", display, shouldResetDisplay)
                        shouldResetDisplay = false
                    }
                    CalcButton(".", Modifier.weight(1f)) {
                        if (shouldResetDisplay) {
                            display = "0."
                            shouldResetDisplay = false
                        } else if (!display.contains(".")) {
                            display += "."
                        }
                    }
                    CalcButton("=", Modifier.weight(1f), Color(0xFF28A745)) {
                        if (operation != null && previousValue != null) {
                            val result = calc(previousValue!!, display.toDoubleOrNull() ?: 0.0, operation!!)
                            display = formatNumber(result)
                            previousValue = null
                            operation = null
                            shouldResetDisplay = true
                        }
                    }
                }
            }

            Text(
                text = "Jetpack Compose Layout Demo",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    bgColor: Color = Color(0xFF495057),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp)
    ) {
        Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

fun handleNum(num: String, curr: String, reset: Boolean): String {
    return if (reset) num else if (curr == "0") num else curr + num
}

fun handleOp(
    op: String, curr: String, prev: Double?, currOp: String?, reset: Boolean,
    setDisp: (String) -> Unit, setPrev: (Double?) -> Unit, setOp: (String?) -> Unit, setReset: (Boolean) -> Unit
) {
    val current = curr.toDoubleOrNull() ?: 0.0
    if (prev != null && currOp != null && !reset) {
        val result = calc(prev, current, currOp)
        setDisp(formatNumber(result))
        setPrev(result)
    } else {
        setPrev(current)
    }
    setOp(op)
    setReset(true)
}

fun calc(a: Double, b: Double, op: String): Double {
    return when (op) {
        "+" -> a + b
        "−" -> a - b
        "×" -> a * b
        "÷" -> if (b != 0.0) a / b else 0.0
        else -> b
    }
}

fun formatNumber(num: Double): String {
    return if (num % 1.0 == 0.0) num.toInt().toString() else num.toString()
}
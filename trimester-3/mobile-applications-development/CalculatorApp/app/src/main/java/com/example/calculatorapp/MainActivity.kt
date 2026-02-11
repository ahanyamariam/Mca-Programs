// MainActivity.kt
package com.example.calculatorapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit  var display: TextView
    private lateinit var resultDisplay: TextView

    private var currentInput = ""
    private var operator = ""
    private var firstOperand = 0.0
    private var isNewOperation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)
        resultDisplay = findViewById(R.id.resultDisplay)

        setupNumberButtons()
        setupOperatorButtons()
        setupFunctionButtons()
    }

    private fun setupNumberButtons() {
F        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        numberButtons.forEachIndexed { index, id ->
            findViewById<Button>(id).setOnClickListener {
                appendNumber(index.toString())
            }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener {
            appendDecimal()
        }
    }

    private fun setupOperatorButtons() {
        findViewById<Button>(R.id.btnAdd).setOnClickListener { setOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { setOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { setOperator("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { setOperator("÷") }
    }

    private fun setupFunctionButtons() {
        findViewById<Button>(R.id.btnClear).setOnClickListener { clear() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { delete() }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculate() }
    }

    private fun appendNumber(number: String) {
        if (isNewOperation) {
            currentInput = number
            isNewOperation = false
        } else {
            currentInput += number
        }
        updateDisplay()
    }

    private fun appendDecimal() {
        if (isNewOperation) {
            currentInput = "0."
            isNewOperation = false
        } else if (!currentInput.contains(".")) {
            currentInput += if (currentInput.isEmpty()) "0." else "."
        }
        updateDisplay()
    }

    private fun setOperator(op: String) {
        if (currentInput.isNotEmpty()) {
            if (operator.isNotEmpty()) {
                calculate()
            }
            firstOperand = currentInput.toDoubleOrNull() ?: 0.0
            operator = op
            display.text = "${formatNumber(firstOperand)} $operator"
            currentInput = ""
        }
    }

    private fun calculate() {
        if (operator.isEmpty() || currentInput.isEmpty()) return

        val secondOperand = currentInput.toDoubleOrNull() ?: 0.0
        val result = when (operator) {
            "+" -> firstOperand + secondOperand
            "-" -> firstOperand - secondOperand
            "×" -> firstOperand * secondOperand
            "÷" -> if (secondOperand != 0.0) firstOperand / secondOperand else Double.NaN
            else -> 0.0
        }

        if (result.isNaN() || result.isInfinite()) {
            resultDisplay.text = "Error"
            clear()
        } else {
            display.text = "${formatNumber(firstOperand)} $operator ${formatNumber(secondOperand)}"
            resultDisplay.text = formatNumber(result)
            currentInput = result.toString()
            operator = ""
            isNewOperation = true
        }
    }

    private fun clear() {
        currentInput = ""
        operator = ""
        firstOperand = 0.0
        isNewOperation = true
        display.text = "0"
        resultDisplay.text = ""
    }

    private fun delete() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        display.text = if (currentInput.isEmpty()) "0" else currentInput
    }

    private fun formatNumber(number: Double): String {
        val df = DecimalFormat("#.##########")
        return df.format(number)
    }
}

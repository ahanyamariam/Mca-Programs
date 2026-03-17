package com.example.dailyplanner.ui.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyplanner.data.entity.ExpenseEntity
import com.example.dailyplanner.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private val rupeeFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel = viewModel()) {
    val expenses by viewModel.expenses.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val totalBudget by viewModel.totalBudget.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Expense Tracker",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Track every rupee you spend",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Budget overview card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Total Spent",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                rupeeFormat.format(totalExpenses),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Budget Allocated",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                rupeeFormat.format(totalBudget),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val remaining = totalBudget - totalExpenses
                    val progress = if (totalBudget > 0) (totalExpenses / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (remaining >= 0) MaterialTheme.colorScheme.primary else ErrorRed,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (remaining >= 0) "Remaining: ${rupeeFormat.format(remaining)}"
                        else "Over budget by: ${rupeeFormat.format(-remaining)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (remaining >= 0) MaterialTheme.colorScheme.onPrimaryContainer
                        else ErrorRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No expenses recorded",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            "Tap + to log an expense",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        ExpenseCard(
                            expense = expense,
                            onDelete = { viewModel.deleteExpense(expense) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            budgets = budgets,
            onDismiss = { showAddDialog = false },
            onAdd = { title, amount, category, note, budgetId ->
                viewModel.addExpense(title, amount, category, note, budgetId)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ExpenseCard(
    expense: ExpenseEntity,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        getCategoryIcon(expense.category),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            expense.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        dateFormat.format(Date(expense.date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                rupeeFormat.format(expense.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun getCategoryIcon(category: String) = when (category) {
    "Food" -> Icons.Filled.Restaurant
    "Transport" -> Icons.Filled.DirectionsCar
    "Shopping" -> Icons.Filled.ShoppingBag
    "Bills" -> Icons.Filled.ElectricBolt
    "Entertainment" -> Icons.Filled.TheaterComedy
    "Health" -> Icons.Filled.LocalHospital
    "Education" -> Icons.Filled.School
    else -> Icons.Filled.MoreHoriz
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    budgets: List<com.example.dailyplanner.data.entity.BudgetEntity>,
    onDismiss: () -> Unit,
    onAdd: (String, Double, String, String, Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }
    var note by remember { mutableStateOf("") }

    val matchingBudget = budgets.find { it.category == category }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Expense", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { newVal ->
                        if (newVal.isEmpty() || newVal.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            amount = newVal
                        }
                    },
                    label = { Text("Amount (INR)") },
                    prefix = { Text("Rs. ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text("Category", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ExpenseViewModel.categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                if (matchingBudget != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            "Budget: ${rupeeFormat.format(matchingBudget.allocatedAmount)} allocated for ${matchingBudget.category}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (title.isNotBlank() && amt != null && amt > 0) {
                        onAdd(title, amt, category, note, matchingBudget?.id)
                    }
                },
                enabled = title.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

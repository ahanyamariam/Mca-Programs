package com.example.dailyplanner.ui.budget

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
import com.example.dailyplanner.data.entity.BudgetEntity
import com.example.dailyplanner.data.entity.ExpenseEntity
import com.example.dailyplanner.ui.theme.*
import java.text.NumberFormat
import java.util.*

private val rupeeFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(viewModel: BudgetViewModel = viewModel()) {
    val budgets by viewModel.budgets.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val totalAllocated by viewModel.totalAllocated.collectAsState()
    val totalSpent by viewModel.totalSpent.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Budget")
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
                text = "Budget Planner",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Allocate and manage your finances",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Total overview card
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Total Allocated",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                rupeeFormat.format(totalAllocated),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Total Spent",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                rupeeFormat.format(totalSpent),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (totalSpent > totalAllocated) ErrorRed
                                else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    val remaining = totalAllocated - totalSpent
                    val progress = if (totalAllocated > 0) (totalSpent / totalAllocated).toFloat().coerceIn(0f, 1f) else 0f
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
                        text = if (remaining >= 0) "Available: ${rupeeFormat.format(remaining)}"
                        else "Over budget by: ${rupeeFormat.format(-remaining)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (remaining >= 0) MaterialTheme.colorScheme.onPrimaryContainer
                        else ErrorRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (budgets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.AccountBalanceWallet,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No budgets set",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            "Create a budget to start planning",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(budgets, key = { it.id }) { budget ->
                        BudgetCard(
                            budget = budget,
                            spent = viewModel.getSpentForCategory(budget.category, expenses),
                            onDelete = { viewModel.deleteBudget(budget) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBudgetDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, amount, category ->
                viewModel.addBudget(name, amount, category)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BudgetCard(
    budget: BudgetEntity,
    spent: Double,
    onDelete: () -> Unit
) {
    val remaining = budget.allocatedAmount - spent
    val progress = if (budget.allocatedAmount > 0) (spent / budget.allocatedAmount).toFloat().coerceIn(0f, 1f) else 0f
    val isOverBudget = remaining < 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        budget.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            budget.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        rupeeFormat.format(budget.allocatedAmount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "allocated",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (isOverBudget) ErrorRed else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Spent: ${rupeeFormat.format(spent)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isOverBudget) "Over: ${rupeeFormat.format(-remaining)}"
                    else "Left: ${rupeeFormat.format(remaining)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isOverBudget) ErrorRed else SuccessGreen
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Budget", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Budget Name") },
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
                    items(BudgetViewModel.categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (name.isNotBlank() && amt != null && amt > 0) {
                        onAdd(name, amt, category)
                    }
                },
                enabled = name.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

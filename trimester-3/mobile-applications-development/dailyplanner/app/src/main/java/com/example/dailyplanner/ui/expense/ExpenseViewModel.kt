package com.example.dailyplanner.ui.expense

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyplanner.data.AppDatabase
import com.example.dailyplanner.data.entity.BudgetEntity
import com.example.dailyplanner.data.entity.ExpenseEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val expenseDao = db.expenseDao()
    private val budgetDao = db.budgetDao()

    val expenses: StateFlow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExpenses: StateFlow<Double> = expenseDao.getTotalExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalBudget: StateFlow<Double> = budgetDao.getTotalAllocated()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val budgets: StateFlow<List<BudgetEntity>> = budgetDao.getAllBudgets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addExpense(title: String, amount: Double, category: String, note: String, budgetId: Long?) {
        viewModelScope.launch {
            expenseDao.insert(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    note = note,
                    budgetId = budgetId
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            expenseDao.delete(expense)
        }
    }

    companion object {
        val categories = listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Health", "Education", "Other")
    }
}

package com.example.dailyplanner.ui.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyplanner.data.AppDatabase
import com.example.dailyplanner.data.entity.BudgetEntity
import com.example.dailyplanner.data.entity.ExpenseEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val budgetDao = db.budgetDao()
    private val expenseDao = db.expenseDao()

    val budgets: StateFlow<List<BudgetEntity>> = budgetDao.getAllBudgets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalAllocated: StateFlow<Double> = budgetDao.getTotalAllocated()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalSpent: StateFlow<Double> = expenseDao.getTotalExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun getSpentForCategory(category: String, allExpenses: List<ExpenseEntity>): Double {
        return allExpenses.filter { it.category == category }.sumOf { it.amount }
    }

    fun addBudget(name: String, allocatedAmount: Double, category: String) {
        val cal = Calendar.getInstance()
        viewModelScope.launch {
            budgetDao.insert(
                BudgetEntity(
                    name = name,
                    allocatedAmount = allocatedAmount,
                    category = category,
                    month = cal.get(Calendar.MONTH) + 1,
                    year = cal.get(Calendar.YEAR)
                )
            )
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            budgetDao.delete(budget)
        }
    }

    fun updateBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            budgetDao.update(budget)
        }
    }

    companion object {
        val categories = listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Health", "Education", "Other")
    }
}

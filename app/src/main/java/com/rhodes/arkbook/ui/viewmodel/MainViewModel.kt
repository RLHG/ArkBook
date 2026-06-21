package com.rhodes.arkbook.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rhodes.arkbook.ArkBookApp
import com.rhodes.arkbook.data.*
import com.rhodes.arkbook.repository.MonthlyStats
import com.rhodes.arkbook.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as ArkBookApp).transactionRepository

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    private val _monthlyStats = MutableStateFlow<MonthlyStats?>(null)
    val monthlyStats: StateFlow<MonthlyStats?> = _monthlyStats.asStateFlow()

    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val currentYear = _currentYear.asStateFlow()

    private val _currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)
    val currentMonth = _currentMonth.asStateFlow()

    init {
        loadMonthlyStats()
    }

    fun loadMonthlyStats() {
        viewModelScope.launch {
            val stats = repository.getMonthlyStats(_currentYear.value, _currentMonth.value)
            _monthlyStats.value = stats
        }
    }

    fun selectMonth(year: Int, month: Int) {
        _currentYear.value = year
        _currentMonth.value = month
        loadMonthlyStats()
    }

    fun addTransaction(amount: Double, type: TransactionType, categoryId: String, note: String, date: Long) {
        viewModelScope.launch {
            repository.addTransaction(
                TransactionEntity(
                    amount = amount,
                    type = type,
                    categoryId = categoryId,
                    note = note,
                    date = date
                )
            )
            loadMonthlyStats()
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            loadMonthlyStats()
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllTransactions()
            loadMonthlyStats()
        }
    }

    // Category breakdown for stats
    fun getCategoryBreakdown(year: Int, month: Int): List<CategoryBreakdown> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1, 0, 0, 0)
        val startDate = calendar.timeInMillis
        calendar.set(year, month - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        val endDate = calendar.timeInMillis

        return transactions.value
            .filter { it.date in startDate..endDate && it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId }
            .mapNotNull { (catId, txs) ->
                val cat = Category.getById(catId) ?: return@mapNotNull null
                CategoryBreakdown(
                    category = cat,
                    amount = txs.sumOf { it.amount }
                )
            }
            .sortedByDescending { it.amount }
    }

    // Monthly trend for last 6 months
    fun getMonthlyTrend(): List<MonthTrend> {
        val result = mutableListOf<MonthTrend>()
        val now = Calendar.getInstance()
        for (i in 5 downTo 0) {
            val d = Calendar.getInstance().apply { add(Calendar.MONTH, -i) }
            val year = d.get(Calendar.YEAR)
            val month = d.get(Calendar.MONTH) + 1

            val cal = Calendar.getInstance()
            cal.set(year, month - 1, 1, 0, 0, 0)
            val start = cal.timeInMillis
            cal.set(year, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            val end = cal.timeInMillis

            val expense = transactions.value
                .filter { it.date in start..end && it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
            val income = transactions.value
                .filter { it.date in start..end && it.type == TransactionType.INCOME }
                .sumOf { it.amount }

            result.add(MonthTrend(
                label = "${month}月",
                expense = expense,
                income = income
            ))
        }
        return result
    }
}

data class CategoryBreakdown(
    val category: Category,
    val amount: Double
)

data class MonthTrend(
    val label: String,
    val expense: Double,
    val income: Double
)

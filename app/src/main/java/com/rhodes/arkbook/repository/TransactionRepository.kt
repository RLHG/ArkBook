package com.rhodes.arkbook.repository

import com.rhodes.arkbook.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*

class TransactionRepository(
    private val dao: TransactionDao,
    private val settingsRepo: AppSettingsRepository
) {
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val settings: Flow<AppSettings> = settingsRepo.settings

    suspend fun addTransaction(transaction: TransactionEntity): Long {
        return dao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) {
        dao.deleteById(id)
    }

    suspend fun getMonthlyStats(year: Int, month: Int): MonthlyStats {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1, 0, 0, 0)
        val startDate = calendar.timeInMillis

        calendar.set(year, month - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        val endDate = calendar.timeInMillis

        val totalExpense = dao.getTotalExpenseBetween(startDate, endDate) ?: 0.0
        val totalIncome = dao.getTotalIncomeBetween(startDate, endDate) ?: 0.0
        val balance = totalIncome - totalExpense

        val now = Calendar.getInstance()
        val today = now.get(Calendar.DAY_OF_MONTH)
        val daysInMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH)
        val daysRemaining = daysInMonth - today + 1
        val dailyAverage = if (today > 0) totalExpense / today else 0.0

        // Days until next allowance
        val currentSettings = settingsRepo.settings
        val settingsValue = currentSettings.first()
        val currentDay = now.get(Calendar.DAY_OF_MONTH)
        val allowanceDay = settingsValue.allowanceDay

        val allowanceDaysUntil = if (currentDay < allowanceDay) {
            allowanceDay - currentDay
        } else {
            val nextMonthDays = Calendar.getInstance().apply {
                add(Calendar.MONTH, 1)
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.DAY_OF_MONTH, -1)
            }.get(Calendar.DAY_OF_MONTH)
            nextMonthDays - currentDay + allowanceDay
        }

        return MonthlyStats(
            totalExpense = totalExpense,
            totalIncome = totalIncome,
            balance = balance,
            dailyAverage = dailyAverage,
            daysRemaining = daysRemaining,
            allowanceDaysUntil = allowanceDaysUntil
        )
    }

    suspend fun updateSettings(settings: AppSettings) {
        settingsRepo.updateSettings(settings)
    }

    suspend fun deleteAllTransactions() {
        dao.deleteAll()
    }
}

data class MonthlyStats(
    val totalExpense: Double,
    val totalIncome: Double,
    val balance: Double,
    val dailyAverage: Double,
    val daysRemaining: Int,
    val allowanceDaysUntil: Int
)

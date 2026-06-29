package com.rhodes.arkbook.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "arkbook_settings")

class AppSettingsRepository(private val context: Context) {

    private object Keys {
        val ALLOWANCE_AMOUNT = doublePreferencesKey("allowance_amount")
        val ALLOWANCE_DAY = intPreferencesKey("allowance_day")
        val MONTHLY_BUDGET = doublePreferencesKey("monthly_budget")
        val USERNAME = stringPreferencesKey("username")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val IS_REMINDER_ENABLED = booleanPreferencesKey("is_reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val IS_BUDGET_ALERT_ENABLED = booleanPreferencesKey("is_budget_alert_enabled")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            allowanceAmount = prefs[Keys.ALLOWANCE_AMOUNT] ?: 3000.0,
            allowanceDay = prefs[Keys.ALLOWANCE_DAY] ?: 1,
            monthlyBudget = prefs[Keys.MONTHLY_BUDGET] ?: 3000.0,
            username = prefs[Keys.USERNAME] ?: "博士",
            themeMode = ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name),
            isReminderEnabled = prefs[Keys.IS_REMINDER_ENABLED] ?: true,
            reminderHour = prefs[Keys.REMINDER_HOUR] ?: 20,
            reminderMinute = prefs[Keys.REMINDER_MINUTE] ?: 0,
            isBudgetAlertEnabled = prefs[Keys.IS_BUDGET_ALERT_ENABLED] ?: true
        )
    }

    suspend fun updateAllowanceAmount(amount: Double) {
        context.dataStore.edit { it[Keys.ALLOWANCE_AMOUNT] = amount }
    }

    suspend fun updateAllowanceDay(day: Int) {
        context.dataStore.edit { it[Keys.ALLOWANCE_DAY] = day }
    }

    suspend fun updateMonthlyBudget(budget: Double) {
        context.dataStore.edit { it[Keys.MONTHLY_BUDGET] = budget }
    }

    suspend fun updateUsername(name: String) {
        context.dataStore.edit { it[Keys.USERNAME] = name }
    }

    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ALLOWANCE_AMOUNT] = settings.allowanceAmount
            prefs[Keys.ALLOWANCE_DAY] = settings.allowanceDay
            prefs[Keys.MONTHLY_BUDGET] = settings.monthlyBudget
            prefs[Keys.USERNAME] = settings.username
            prefs[Keys.THEME_MODE] = settings.themeMode.name
            prefs[Keys.IS_REMINDER_ENABLED] = settings.isReminderEnabled
            prefs[Keys.REMINDER_HOUR] = settings.reminderHour
            prefs[Keys.REMINDER_MINUTE] = settings.reminderMinute
            prefs[Keys.IS_BUDGET_ALERT_ENABLED] = settings.isBudgetAlertEnabled
        }
    }
}

enum class ThemeMode(val label: String) {
    LIGHT("浅色"),
    DARK("深色"),
    SYSTEM("跟随系统")
}

data class AppSettings(
    val allowanceAmount: Double = 3000.0,
    val allowanceDay: Int = 1,
    val monthlyBudget: Double = 3000.0,
    val username: String = "博士",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isReminderEnabled: Boolean = true,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val isBudgetAlertEnabled: Boolean = true
)

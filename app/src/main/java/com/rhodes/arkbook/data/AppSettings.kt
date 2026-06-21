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
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            allowanceAmount = prefs[Keys.ALLOWANCE_AMOUNT] ?: 3000.0,
            allowanceDay = prefs[Keys.ALLOWANCE_DAY] ?: 1,
            monthlyBudget = prefs[Keys.MONTHLY_BUDGET] ?: 3000.0,
            username = prefs[Keys.USERNAME] ?: "博士",
            themeMode = ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
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
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

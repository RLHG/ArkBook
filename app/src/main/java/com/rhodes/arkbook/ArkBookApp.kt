package com.rhodes.arkbook

import android.app.Application
import com.rhodes.arkbook.data.AppDatabase
import com.rhodes.arkbook.data.AppSettingsRepository
import com.rhodes.arkbook.repository.TransactionRepository
import com.rhodes.arkbook.utils.NotificationHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ArkBookApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        
        // 异步获取设置并排期提醒
        MainScope().launch {
            val settings = settingsRepository.settings.first()
            NotificationHelper.scheduleDailyReminder(this@ArkBookApp, settings)
        }
    }

    val database by lazy { AppDatabase.getDatabase(this) }
    val settingsRepository by lazy { AppSettingsRepository(this) }
    val transactionRepository by lazy {
        TransactionRepository(database.transactionDao(), settingsRepository)
    }
}

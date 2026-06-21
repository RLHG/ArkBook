package com.rhodes.arkbook

import android.app.Application
import com.rhodes.arkbook.data.AppDatabase
import com.rhodes.arkbook.data.AppSettingsRepository
import com.rhodes.arkbook.repository.TransactionRepository

class ArkBookApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val settingsRepository by lazy { AppSettingsRepository(this) }
    val transactionRepository by lazy {
        TransactionRepository(database.transactionDao(), settingsRepository)
    }
}

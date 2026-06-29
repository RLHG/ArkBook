package com.rhodes.arkbook.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rhodes.arkbook.ArkBookApp
import kotlinx.coroutines.flow.first
import java.util.*

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as ArkBookApp
        val repository = app.transactionRepository

        // 检查设置，如果提醒已禁用，则直接返回
        val settings = repository.settings.first()
        if (!settings.isReminderEnabled) {
            return Result.success()
        }

        // 获取今天的开始和结束时间
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis

        // 检查今天是否有记录
        val transactionsToday = repository.allTransactions.first().filter { 
            it.date in startTime..endTime 
        }

        if (transactionsToday.isEmpty()) {
            NotificationHelper.sendReminderNotification(applicationContext)
        }

        return Result.success()
    }
}

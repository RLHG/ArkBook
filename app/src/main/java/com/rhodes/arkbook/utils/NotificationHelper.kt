package com.rhodes.arkbook.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.rhodes.arkbook.R
import java.util.concurrent.TimeUnit

object NotificationHelper {
    private const val CHANNEL_ID = "arkbook_reminder"
    private const val CHANNEL_NAME = "记账提醒"
    private const val CHANNEL_DESCRIPTION = "用于提醒日常记账和预算预警"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendTestNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // 使用系统图标作为临时方案
            .setContentTitle("ArkBook 测试通知")
            .setContentText("这是一条来自 ArkBook 的测试通知，证明通知功能已正常工作。")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // 这里我们假设权限已经申请过，如果是 Android 13+ 需要在 Activity 处理权限
            try {
                notify(1, builder.build())
            } catch (e: SecurityException) {
                // 处理没有权限的情况
            }
        }
    }

    fun sendReminderNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("记账提醒")
            .setContentText("今天还没有记账哦，快来记录一笔吧！")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(10, builder.build())
            } catch (e: SecurityException) { }
        }
    }

    fun sendBudgetAlertNotification(context: Context, budget: Double, totalExpense: Double) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("预算超支预警")
            .setContentText("本月支出已达 ¥${"%.2f".format(totalExpense)}，超过预算 ¥${"%.2f".format(budget)}！")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(20, builder.build())
            } catch (e: SecurityException) { }
        }
    }

    fun scheduleDailyReminder(context: Context, settings: com.rhodes.arkbook.data.AppSettings? = null) {
        val workManager = WorkManager.getInstance(context)
        
        // 如果提供了 settings 且提醒被禁用，则取消现有任务
        if (settings != null && !settings.isReminderEnabled) {
            workManager.cancelUniqueWork("daily_reminder")
            return
        }

        val hour = settings?.reminderHour ?: 20
        val minute = settings?.reminderMinute ?: 0

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(calculateInitialDelay(hour, minute), TimeUnit.MILLISECONDS)
            .addTag("daily_reminder")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.REPLACE, // 使用 REPLACE 这样修改时间会生效
            workRequest
        )
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val calendar = java.util.Calendar.getInstance()
        val now = calendar.timeInMillis
        
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
        calendar.set(java.util.Calendar.MINUTE, minute)
        calendar.set(java.util.Calendar.SECOND, 0)
        
        if (calendar.timeInMillis <= now) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }
        
        return calendar.timeInMillis - now
    }
}

package com.rhodes.arkbook.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.rhodes.arkbook.data.AppSettings
import com.rhodes.arkbook.data.TransactionEntity
import com.rhodes.arkbook.ui.viewmodel.MainViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun exportData(context: Context, transactions: List<TransactionEntity>, settings: AppSettings) {
    try {
        val json = JSONObject().apply {
            put("exportDate", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
            put("settings", JSONObject().apply {
                put("username", settings.username)
                put("allowanceAmount", settings.allowanceAmount)
                put("allowanceDay", settings.allowanceDay)
                put("monthlyBudget", settings.monthlyBudget)
            })
            val txArray = JSONArray()
            transactions.forEach { tx ->
                txArray.put(JSONObject().apply {
                    put("id", tx.id)
                    put("amount", tx.amount)
                    put("type", tx.type.name)
                    put("categoryId", tx.categoryId)
                    put("note", tx.note)
                    put("date", tx.date)
                    put("createdAt", tx.createdAt)
                })
            }
            put("transactions", txArray)
        }

        val fileName = "arkbook_backup_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.json"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        file.writeText(json.toString(2))
        Toast.makeText(context, "已导出到下载目录: $fileName", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "导出失败: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun importData(context: Context, viewModel: MainViewModel) {
    Toast.makeText(context, "请将备份文件放到下载目录后使用", Toast.LENGTH_SHORT).show()
}

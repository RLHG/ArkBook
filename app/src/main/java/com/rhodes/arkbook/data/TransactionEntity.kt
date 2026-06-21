package com.rhodes.arkbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "transactions")
@TypeConverters(Converters::class)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryId: String,
    val note: String = "",
    val date: Long, // timestamp
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    EXPENSE, INCOME
}

class Converters {
    @androidx.room.TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @androidx.room.TypeConverter
    fun toTransactionType(name: String): TransactionType = TransactionType.valueOf(name)
}

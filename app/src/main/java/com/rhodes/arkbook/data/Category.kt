package com.rhodes.arkbook.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class Category(
    val displayName: String,
    val type: TransactionType,
    val color: Color,
    val icon: ImageVector
) {
    // Expense categories
    FOOD("餐饮", TransactionType.EXPENSE, Color(0xFFFFB800), Icons.Default.Restaurant),
    TRANSPORT("交通", TransactionType.EXPENSE, Color(0xFF00D4AA), Icons.Default.DirectionsBus),
    SHOPPING("购物", TransactionType.EXPENSE, Color(0xFFFF6B35), Icons.Default.ShoppingBag),
    ENTERTAINMENT("娱乐", TransactionType.EXPENSE, Color(0xFF8B5CF6), Icons.Default.Gamepad),
    STUDY("学习", TransactionType.EXPENSE, Color(0xFF3B82F6), Icons.Default.MenuBook),
    LIVING("生活", TransactionType.EXPENSE, Color(0xFFEC4899), Icons.Default.Checkroom),
    MEDICAL("医疗", TransactionType.EXPENSE, Color(0xFFEF4444), Icons.Default.MedicalServices),
    SOCIAL("社交", TransactionType.EXPENSE, Color(0xFF14B8A6), Icons.Default.Groups),
    OTHER_EXPENSE("其他", TransactionType.EXPENSE, Color(0xFF6B7280), Icons.Default.MoreHoriz),

    // Income categories
    ALLOWANCE("生活费", TransactionType.INCOME, Color(0xFFFFB800), Icons.Default.AccountBalanceWallet),
    SALARY("兼职", TransactionType.INCOME, Color(0xFF00D4AA), Icons.Default.Work),
    REDPACKET("红包", TransactionType.INCOME, Color(0xFFFF6B35), Icons.Default.CardGiftcard),
    SCHOLARSHIP("奖学金", TransactionType.INCOME, Color(0xFF8B5CF6), Icons.Default.EmojiEvents),
    OTHER_INCOME("其他", TransactionType.INCOME, Color(0xFF6B7280), Icons.Default.MoreHoriz);

    companion object {
        fun getById(id: String): Category? {
            return entries.find { it.name == id }
        }

        fun getExpenseCategories(): List<Category> = entries.filter { it.type == TransactionType.EXPENSE }
        fun getIncomeCategories(): List<Category> = entries.filter { it.type == TransactionType.INCOME }
    }
}

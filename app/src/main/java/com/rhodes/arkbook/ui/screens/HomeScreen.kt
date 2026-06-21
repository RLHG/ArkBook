package com.rhodes.arkbook.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rhodes.arkbook.data.TransactionEntity
import com.rhodes.arkbook.data.TransactionType
import com.rhodes.arkbook.repository.MonthlyStats
import com.rhodes.arkbook.ui.components.BalanceCard
import com.rhodes.arkbook.ui.components.StatCard
import com.rhodes.arkbook.ui.components.TransactionItem
import com.rhodes.arkbook.ui.theme.ArkColors

@Composable
fun HomeScreen(
    stats: MonthlyStats?,
    recentTransactions: List<TransactionEntity>,
    budget: Double,
    username: String,
    monthLabel: String,
    onMonthClick: () -> Unit,
    onAddClick: () -> Unit,
    onViewAllClick: () -> Unit,
    onDeleteTransaction: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Header
        item {
            HeaderSection(username = username, monthLabel = monthLabel, onMonthClick = onMonthClick)
        }

        // Balance Card
        item {
            val balance = stats?.balance ?: 0.0
            val totalExpense = stats?.totalExpense ?: 0.0
            val percentage = if (budget > 0) ((budget - totalExpense) / budget).toFloat().coerceIn(0f, 1f) else 0f
            BalanceCard(
                balance = balance,
                budget = budget,
                percentage = percentage,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // Quick Stats Grid
        item {
            StatsGrid(
                stats = stats,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // Daily Budget Hint
        item {
            DailyBudgetHint(stats = stats, modifier = Modifier.padding(top = 12.dp))
        }

        // Recent Transactions
        item {
            RecentTransactionsHeader(
                onViewAll = onViewAllClick,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )
        }

        items(recentTransactions.size) { index ->
            val tx = recentTransactions[index]
            TransactionItem(
                transaction = tx,
                onDelete = { onDeleteTransaction(tx.id) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (recentTransactions.isEmpty()) {
            item {
                EmptyState(modifier = Modifier.padding(vertical = 32.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection(username: String, monthLabel: String, onMonthClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "学生记账本",
                style = MaterialTheme.typography.titleLarge,
                color = ArkColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "欢迎回来，${username}",
                style = MaterialTheme.typography.labelSmall,
                color = ArkColors.TextTertiary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(ArkColors.SurfaceLight)
                .border(1.dp, ArkColors.Border, RoundedCornerShape(8.dp))
                .clickable { onMonthClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = monthLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = ArkColors.Primary,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = ArkColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun StatsGrid(stats: MonthlyStats?, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "本月支出",
                value = "¥${(stats?.totalExpense ?: 0.0).toInt()}",
                color = ArkColors.Expense,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "本月收入",
                value = "¥${(stats?.totalIncome ?: 0.0).toInt()}",
                color = ArkColors.Income,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "日均消费",
                value = "¥${String.format("%.1f", stats?.dailyAverage ?: 0.0)}",
                color = ArkColors.Accent,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "距发钱日",
                value = "${stats?.allowanceDaysUntil ?: 0}天",
                color = ArkColors.Primary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DailyBudgetHint(stats: MonthlyStats?, modifier: Modifier = Modifier) {
    val dailyAvailable = if ((stats?.daysRemaining ?: 0) > 0) {
        (stats?.balance ?: 0.0) / (stats?.daysRemaining ?: 1)
    } else 0.0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ArkColors.Background)
            .border(1.dp, ArkColors.Primary.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = ArkColors.Primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "本月剩余日均可用",
                    style = MaterialTheme.typography.labelMedium,
                    color = ArkColors.TextSecondary
                )
                Text(
                    text = "¥${String.format("%.2f", dailyAvailable)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ArkColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "/ 天（剩${stats?.daysRemaining ?: 0}天）",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArkColors.TextTertiary
                )
            }
        }
    }
}

@Composable
private fun RecentTransactionsHeader(onViewAll: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "最近记录",
            style = MaterialTheme.typography.titleMedium,
            color = ArkColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onViewAll) {
            Text(
                text = "查看全部",
                style = MaterialTheme.typography.labelLarge,
                color = ArkColors.Primary
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ReceiptLong,
            contentDescription = null,
            tint = ArkColors.TextTertiary.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "暂无记录",
            style = MaterialTheme.typography.bodyMedium,
            color = ArkColors.TextTertiary
        )
    }
}

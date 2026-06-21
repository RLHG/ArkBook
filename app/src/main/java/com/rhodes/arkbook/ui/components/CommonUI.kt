package com.rhodes.arkbook.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rhodes.arkbook.data.Category
import com.rhodes.arkbook.data.TransactionEntity
import com.rhodes.arkbook.data.TransactionType
import com.rhodes.arkbook.ui.navigation.Screen
import com.rhodes.arkbook.ui.theme.ArkColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BalanceCard(
    balance: Double,
    budget: Double,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(ArkColors.Surface, ArkColors.SurfaceLight)
                )
            )
            .border(1.dp, ArkColors.Border, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.TopEnd)
                .offset(x = 32.dp, y = (-32).dp)
                .background(ArkColors.Primary.copy(alpha = 0.05f), RoundedCornerShape(48.dp))
        )

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "本月余额",
                    style = MaterialTheme.typography.labelLarge,
                    color = ArkColors.TextSecondary
                )
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "¥",
                    style = MaterialTheme.typography.titleLarge,
                    color = ArkColors.Primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%.2f", balance),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 36.sp),
                    color = ArkColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Progress bar
            val isOverBudget = percentage < 0.1f && balance < 0
            val isWarning = percentage < 0.2f
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(ArkColors.SurfaceLighter)
            ) {
                val progressColor = when {
                    balance < 0 -> ArkColors.Expense
                    isWarning -> ArkColors.AccentOrange
                    else -> ArkColors.Primary
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(progressColor)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "预算 ${budget.toInt()} 元",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArkColors.TextTertiary
                )
                Text(
                    text = if (balance < 0) "已超支 ${(-balance).toInt()} 元" else "剩余 ${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        balance < 0 -> ArkColors.Expense
                        isWarning -> ArkColors.AccentOrange
                        else -> ArkColors.TextSecondary
                    },
                    fontWeight = if (isWarning || balance < 0) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ArkColors.Surface)
            .border(1.dp, ArkColors.Border.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = ArkColors.TextTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = Category.getById(transaction.categoryId)
    val dateStr = remember(transaction.date) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(transaction.date))
    }
    val dateLabel = remember(transaction.date) {
        val cal = Calendar.getInstance()
        val txCal = Calendar.getInstance().apply { timeInMillis = transaction.date }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }

        when {
            txCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            txCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> "今天"
            txCal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
            txCal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) -> "昨天"
            else -> "${txCal.get(Calendar.MONTH) + 1}/${txCal.get(Calendar.DAY_OF_MONTH)}"
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArkColors.Surface)
            .border(1.dp, ArkColors.Border.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background((category?.color ?: ArkColors.TextTertiary).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (category != null) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "?",
                    color = ArkColors.TextTertiary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = category?.displayName ?: transaction.categoryId,
                style = MaterialTheme.typography.bodyLarge,
                color = ArkColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row {
                if (transaction.note.isNotBlank()) {
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.labelSmall,
                        color = ArkColors.TextTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${if (transaction.type == TransactionType.EXPENSE) "-" else "+"}¥${transaction.amount.toInt()}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (transaction.type == TransactionType.EXPENSE) ArkColors.Expense else ArkColors.Income,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$dateLabel $dateStr",
                style = MaterialTheme.typography.labelSmall,
                color = ArkColors.TextTertiary
            )
        }
    }
}

@Composable
fun MonthPickerDialog(
    currentYear: Int,
    currentMonth: Int,
    onDismiss: () -> Unit,
    onMonthSelected: (Int, Int) -> Unit
) {
    val months = remember {
        val list = mutableListOf<Pair<Int, Int>>()
        // Show last 12 months
        for (i in 0 until 12) {
            val d = Calendar.getInstance().apply { add(Calendar.MONTH, -i) }
            list.add(Pair(d.get(Calendar.YEAR), d.get(Calendar.MONTH) + 1))
        }
        list
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择月份", color = ArkColors.TextPrimary) },
        containerColor = ArkColors.Surface,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                months.forEach { (year, month) ->
                    val isSelected = year == currentYear && month == currentMonth
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ArkColors.Primary.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { onMonthSelected(year, month) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${year}年${month}月",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) ArkColors.Primary else ArkColors.TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Icon(Icons.Default.Check, null, tint = ArkColors.Primary, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = ArkColors.TextSecondary)
            }
        }
    )
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Triple(Screen.Home.route, "首页", Icons.Default.Home),
        Triple(Screen.Records.route, "账单", Icons.Default.Receipt),
        Triple(Screen.Stats.route, "统计", Icons.Default.BarChart),
        Triple(Screen.Settings.route, "设置", Icons.Default.Settings)
    )

    Surface(
        color = ArkColors.Background.copy(alpha = 0.95f),
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .border(
                    width = 1.dp,
                    color = ArkColors.Border,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(ArkColors.Background.copy(alpha = 0.95f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { (route, label, icon) ->
                    val isSelected = currentRoute == route
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigate(route) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(2.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(ArkColors.Primary)
                                    .padding(bottom = 4.dp)
                            )
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) ArkColors.Primary else ArkColors.TextTertiary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (isSelected) ArkColors.Primary else ArkColors.TextTertiary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySelector(
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    type: TransactionType
) {
    val categories = remember(type) {
        if (type == TransactionType.EXPENSE) Category.getExpenseCategories()
        else Category.getIncomeCategories()
    }

    Column {
        Text(
            text = "分类",
            style = MaterialTheme.typography.labelMedium,
            color = ArkColors.TextTertiary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        categories.chunked(3).forEach { rowCategories ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowCategories.forEach { cat ->
                    val isSelected = selectedCategory == cat.name
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) ArkColors.Primary.copy(alpha = 0.1f)
                                else ArkColors.SurfaceLight
                            )
                            .border(
                                1.dp,
                                if (isSelected) ArkColors.Primary.copy(alpha = 0.4f) else ArkColors.Border,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onCategorySelected(cat.name) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = null,
                                tint = if (isSelected) ArkColors.Primary else cat.color,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cat.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) ArkColors.Primary else ArkColors.TextPrimary
                            )
                        }
                    }
                }
                // Fill remaining slots
                repeat(3 - rowCategories.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

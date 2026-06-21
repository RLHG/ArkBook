package com.rhodes.arkbook.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rhodes.arkbook.data.Category
import com.rhodes.arkbook.ui.theme.ArkColors
import com.rhodes.arkbook.ui.viewmodel.CategoryBreakdown
import com.rhodes.arkbook.ui.viewmodel.MonthTrend
import java.util.*

@Composable
fun StatsScreen(
    monthlyStats: com.rhodes.arkbook.repository.MonthlyStats?,
    categoryBreakdown: List<CategoryBreakdown>,
    monthlyTrend: List<MonthTrend>,
    onMonthClick: () -> Unit,
    monthLabel: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "收支统计",
                style = MaterialTheme.typography.titleLarge,
                color = ArkColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        // Month Selector
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArkColors.SurfaceLight)
                    .border(1.dp, ArkColors.Border, RoundedCornerShape(12.dp))
                    .clickable { onMonthClick() }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = monthLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        color = ArkColors.TextPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = ArkColors.Primary
                    )
                }
            }
        }

        // Summary Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "总支出",
                    value = "¥${(monthlyStats?.totalExpense ?: 0.0).toInt()}",
                    color = ArkColors.Expense,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "总收入",
                    value = "¥${(monthlyStats?.totalIncome ?: 0.0).toInt()}",
                    color = ArkColors.Income,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Trend Chart
        item {
            TrendChartSection(trend = monthlyTrend, modifier = Modifier.padding(top = 24.dp))
        }

        // Category Breakdown
        item {
            CategoryBreakdownSection(
                breakdown = categoryBreakdown,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
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
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TrendChartSection(trend: List<MonthTrend>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = ArkColors.Primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "近6个月趋势",
                style = MaterialTheme.typography.titleMedium,
                color = ArkColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ArkColors.Surface)
                .border(1.dp, ArkColors.Border.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            if (trend.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ArkColors.TextTertiary
                    )
                }
            } else {
                Column {
                    // Bar chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val maxExpense = trend.maxOfOrNull { it.expense }?.coerceAtLeast(1.0) ?: 1.0
                        val maxIncome = trend.maxOfOrNull { it.income }?.coerceAtLeast(1.0) ?: 1.0

                        trend.forEach { month ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Income bar
                                if (month.income > 0) {
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .height((month.income / maxIncome * 40).dp.coerceAtLeast(2.dp))
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(ArkColors.Income.copy(alpha = 0.6f))
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                // Expense bar
                                if (month.expense > 0) {
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .height((month.expense / maxExpense * 40).dp.coerceAtLeast(2.dp))
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(ArkColors.Expense.copy(alpha = 0.6f))
                                    )
                                } else {
                                    Box(modifier = Modifier.height(2.dp))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = month.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ArkColors.TextTertiary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(color = ArkColors.Expense.copy(alpha = 0.6f), label = "支出")
                        Spacer(modifier = Modifier.width(24.dp))
                        LegendItem(color = ArkColors.Income.copy(alpha = 0.6f), label = "收入")
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ArkColors.TextTertiary
        )
    }
}

@Composable
private fun CategoryBreakdownSection(breakdown: List<CategoryBreakdown>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.PieChart,
                contentDescription = null,
                tint = ArkColors.Primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "支出分类",
                style = MaterialTheme.typography.titleMedium,
                color = ArkColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (breakdown.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ArkColors.Surface)
                    .border(1.dp, ArkColors.Border.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "本月暂无支出记录",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArkColors.TextTertiary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ArkColors.Surface)
                    .border(1.dp, ArkColors.Border.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val total = breakdown.sumOf { it.amount }
                breakdown.forEach { item ->
                    val percentage = if (total > 0) (item.amount / total * 100).toFloat() else 0f
                    CategoryBar(
                        category = item.category,
                        amount = item.amount,
                        percentage = percentage
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBar(category: Category, amount: Double, percentage: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(category.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ArkColors.TextPrimary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${percentage.toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = ArkColors.TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "¥${amount.toInt()}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ArkColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(ArkColors.SurfaceLighter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage.coerceIn(0f, 100f) / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(category.color)
            )
        }
    }
}

package com.rhodes.arkbook.ui.screens

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
import com.rhodes.arkbook.data.TransactionEntity
import com.rhodes.arkbook.data.TransactionType
import com.rhodes.arkbook.ui.components.TransactionItem
import com.rhodes.arkbook.ui.theme.ArkColors
import java.util.*

@Composable
fun RecordsScreen(
    transactions: List<TransactionEntity>,
    onDelete: (Long) -> Unit
) {
    var filter by remember { mutableStateOf<FilterType>(FilterType.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var deleteConfirmId by remember { mutableStateOf<Long?>(null) }

    val filtered = remember(transactions, filter, searchQuery) {
        transactions.filter { tx ->
            val matchesFilter = when (filter) {
                FilterType.ALL -> true
                FilterType.EXPENSE -> tx.type == TransactionType.EXPENSE
                FilterType.INCOME -> tx.type == TransactionType.INCOME
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                val categoryDisplayName = com.rhodes.arkbook.data.Category.getById(tx.categoryId)?.displayName ?: ""
                tx.note.contains(searchQuery, ignoreCase = true) ||
                categoryDisplayName.contains(searchQuery, ignoreCase = true)
            }
            matchesFilter && matchesSearch
        }
    }

    // Group by date
    val grouped = remember(filtered) {
        val map = sortedMapOf<String, MutableList<TransactionEntity>>(reverseOrder())
        filtered.forEach { tx ->
            val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
            val key = String.format("%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
            map.getOrPut(key) { mutableListOf() }.add(tx)
        }
        map
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "账单明细",
                style = MaterialTheme.typography.titleLarge,
                color = ArkColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "共 ${filtered.size} 笔记录",
                style = MaterialTheme.typography.labelMedium,
                color = ArkColors.TextTertiary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Search
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter tabs
        FilterTabs(
            selected = filter,
            onSelect = { filter = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // List
        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                grouped.forEach { (dateKey, dayTxs) ->
                    item {
                        DateHeader(
                            dateKey = dateKey,
                            dayExpense = dayTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount },
                            dayIncome = dayTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                        )
                    }
                    items(dayTxs.size) { index ->
                        TransactionItem(
                            transaction = dayTxs[index],
                            onDelete = { deleteConfirmId = dayTxs[index].id },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (deleteConfirmId != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmId = null },
            containerColor = ArkColors.Surface,
            titleContentColor = ArkColors.TextPrimary,
            textContentColor = ArkColors.TextSecondary,
            title = { Text("确认删除") },
            text = { Text("删除后无法恢复，是否继续？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteConfirmId?.let { onDelete(it) }
                        deleteConfirmId = null
                    }
                ) {
                    Text("删除", color = ArkColors.Expense)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmId = null }) {
                    Text("取消", color = ArkColors.TextSecondary)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArkColors.SurfaceLight)
            .border(1.dp, ArkColors.Border, RoundedCornerShape(12.dp))
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("搜索分类或备注...", color = ArkColors.TextHint) },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = ArkColors.TextTertiary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = ArkColors.TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = ArkColors.TextPrimary,
                unfocusedTextColor = ArkColors.TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FilterTabs(selected: FilterType, onSelect: (FilterType) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterType.entries.forEach { type ->
            val isSelected = selected == type
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) ArkColors.Primary.copy(alpha = 0.12f)
                        else Color.Transparent
                    )
                    .border(
                        1.dp,
                        if (isSelected) ArkColors.Primary.copy(alpha = 0.35f) else ArkColors.Border,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelect(type) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = type.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) ArkColors.Primary else ArkColors.TextTertiary
                )
            }
        }
    }
}

@Composable
private fun DateHeader(dateKey: String, dayExpense: Double, dayIncome: Double) {
    val cal = Calendar.getInstance().apply {
        val parts = dateKey.split("-")
        set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
    }
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }
    val label = when {
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> "今天"
        cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
        cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) -> "昨天"
        else -> "${cal.get(Calendar.MONTH) + 1}月${cal.get(Calendar.DAY_OF_MONTH)}日"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = ArkColors.TextSecondary,
            fontWeight = FontWeight.Medium
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (dayIncome > 0) {
                Text(
                    text = "+${dayIncome.toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArkColors.Income
                )
            }
            if (dayExpense > 0) {
                Text(
                    text = "-${dayExpense.toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArkColors.Expense
                )
            }
        }
    }
}

enum class FilterType(val label: String) {
    ALL("全部"),
    EXPENSE("支出"),
    INCOME("收入")
}

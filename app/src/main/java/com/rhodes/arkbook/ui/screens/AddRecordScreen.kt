package com.rhodes.arkbook.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rhodes.arkbook.data.Category
import com.rhodes.arkbook.data.TransactionType
import com.rhodes.arkbook.ui.theme.ArkColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    initialDate: Long = Calendar.getInstance().timeInMillis,
    onAdd: (Double, TransactionType, String, String, Long) -> Unit,
    onCancel: () -> Unit
) {
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(initialDate) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        // DatePicker returns UTC millis. We need to convert it to Local time's midday
                        // to avoid off-by-one errors when displaying in the app.
                        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        utcCalendar.timeInMillis = it

                        val localCalendar = Calendar.getInstance()
                        localCalendar.set(
                            utcCalendar.get(Calendar.YEAR),
                            utcCalendar.get(Calendar.MONTH),
                            utcCalendar.get(Calendar.DAY_OF_MONTH),
                            12, 0, 0
                        )
                        date = localCalendar.timeInMillis
                    }
                    showDatePicker = false
                }) {
                    Text("确认", color = ArkColors.Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消", color = ArkColors.TextSecondary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = ArkColors.Surface,
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val categories = remember(type) {
        if (type == TransactionType.EXPENSE) Category.getExpenseCategories()
        else Category.getIncomeCategories()
    }

    val quickAmounts = remember(type) {
        if (type == TransactionType.EXPENSE) listOf(10, 15, 20, 30, 50, 100)
        else listOf(500, 1000, 1500, 2000, 3000)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "记一笔",
                style = MaterialTheme.typography.titleLarge,
                color = ArkColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = ArkColors.TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Type Toggle
        TypeToggle(
            selected = type,
            onSelect = {
                type = it
                selectedCategory = null
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Amount Input
        AmountInput(
            amount = amount,
            onAmountChange = { amount = it },
            type = type,
            quickAmounts = quickAmounts,
            onQuickSelect = { amount = it.toString() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Category Selector
        CategorySelector(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Date Selector
        DateSelector(
            selectedDate = date,
            onClick = { showDatePicker = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Note Input
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("备注", color = ArkColors.TextTertiary) },
            placeholder = { Text("添加备注...", color = ArkColors.TextHint) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ArkColors.SurfaceLight,
                unfocusedContainerColor = ArkColors.SurfaceLight,
                focusedBorderColor = ArkColors.Primary.copy(alpha = 0.4f),
                unfocusedBorderColor = ArkColors.Border,
                focusedTextColor = ArkColors.TextPrimary,
                unfocusedTextColor = ArkColors.TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = {
                val numAmount = amount.toDoubleOrNull() ?: 0.0
                if (numAmount > 0 && selectedCategory != null) {
                    onAdd(numAmount, type, selectedCategory!!, note, date)
                }
            },
            enabled = amount.isNotBlank() && amount.toDoubleOrNull()?.let { it > 0 } == true && selectedCategory != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ArkColors.Primary,
                contentColor = ArkColors.OnPrimary,
                disabledContainerColor = ArkColors.SurfaceLighter,
                disabledContentColor = ArkColors.TextTertiary
            )
        ) {
            Text(
                text = "确认${if (type == TransactionType.EXPENSE) "支出" else "收入"}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DateSelector(
    selectedDate: Long,
    onClick: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()) }
    val dateString = remember(selectedDate) { sdf.format(Date(selectedDate)) }

    Column {
        Text(
            text = "日期",
            style = MaterialTheme.typography.labelMedium,
            color = ArkColors.TextTertiary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ArkColors.SurfaceLight)
                .border(1.dp, ArkColors.Border, RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = ArkColors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodyLarge,
                        color = ArkColors.TextPrimary
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = ArkColors.TextHint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TypeToggle(
    selected: TransactionType,
    onSelect: (TransactionType) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ArkColors.SurfaceLight)
            .border(1.dp, ArkColors.Border, RoundedCornerShape(14.dp))
            .padding(4.dp)
    ) {
        Row {
            TransactionType.entries.forEach { t ->
                val isSelected = selected == t
                val bgColor = when {
                    isSelected && t == TransactionType.EXPENSE -> ArkColors.Expense.copy(alpha = 0.12f)
                    isSelected && t == TransactionType.INCOME -> ArkColors.Income.copy(alpha = 0.12f)
                    else -> Color.Transparent
                }
                val textColor = when {
                    isSelected && t == TransactionType.EXPENSE -> ArkColors.Expense
                    isSelected && t == TransactionType.INCOME -> ArkColors.Income
                    else -> ArkColors.TextTertiary
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .clickable { onSelect(t) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (t == TransactionType.EXPENSE) "支出" else "收入",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun AmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    type: TransactionType,
    quickAmounts: List<Int>,
    onQuickSelect: (Int) -> Unit
) {
    Column {
        Text(
            text = "金额",
            style = MaterialTheme.typography.labelMedium,
            color = ArkColors.TextTertiary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(ArkColors.SurfaceLight)
                .border(1.dp, ArkColors.Border, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = if (type == TransactionType.EXPENSE) "-" else "+",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
                    color = if (type == TransactionType.EXPENSE) ArkColors.Expense else ArkColors.Income,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "¥",
                    style = MaterialTheme.typography.titleLarge,
                    color = ArkColors.Primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    placeholder = { Text("0.00", color = ArkColors.TextHint) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = ArkColors.TextPrimary,
                        unfocusedTextColor = ArkColors.TextPrimary
                    ),
                    textStyle = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick amounts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickAmounts.forEach { a ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ArkColors.SurfaceLight)
                        .border(1.dp, ArkColors.Border, RoundedCornerShape(10.dp))
                        .clickable { onQuickSelect(a) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "$a",
                        style = MaterialTheme.typography.labelLarge,
                        color = ArkColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySelector(
    categories: List<Category>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    Column {
        Text(
            text = "分类",
            style = MaterialTheme.typography.labelMedium,
            color = ArkColors.TextTertiary,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        categories.chunked(3).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { cat ->
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
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

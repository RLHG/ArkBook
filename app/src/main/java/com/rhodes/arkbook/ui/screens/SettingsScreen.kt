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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rhodes.arkbook.data.AppSettings
import com.rhodes.arkbook.data.ThemeMode
import com.rhodes.arkbook.ui.theme.ArkColors

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onSettingsChange: (AppSettings) -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onClearAll: () -> Unit
) {
    var showClearConfirm by remember { mutableStateOf(false) }
    var username by remember(settings.username) { mutableStateOf(settings.username) }
    var allowanceAmount by remember(settings.allowanceAmount) { 
        mutableStateOf(java.math.BigDecimal(settings.allowanceAmount).toPlainString().replace(Regex("\\.0$"), "")) 
    }
    var allowanceDay by remember(settings.allowanceDay) { mutableStateOf(settings.allowanceDay) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Header
        Text(
            text = "设置",
            style = MaterialTheme.typography.titleLarge,
            color = ArkColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "管理你的记账偏好",
            style = MaterialTheme.typography.labelMedium,
            color = ArkColors.TextTertiary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Card
        ProfileCard(
            username = username,
            onUsernameChange = {
                username = it
                onSettingsChange(settings.copy(username = it))
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Appearance
        SectionTitle("外观设置")
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ArkColors.Surface)
                .border(1.dp, ArkColors.Border.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            ThemeSelector(
                currentMode = settings.themeMode,
                onModeSelected = { onSettingsChange(settings.copy(themeMode = it)) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Allowance Settings
        SectionTitle("生活费设置")
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ArkColors.Surface)
                .border(1.dp, ArkColors.Border.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            // Allowance Amount
            SettingsNumberItem(
                icon = { Icon(Icons.Default.AccountBalanceWallet, null, tint = ArkColors.Primary, modifier = Modifier.size(18.dp)) },
                title = "每月生活费",
                subtitle = "用于计算预算和日均花费",
                value = allowanceAmount,
                prefix = "¥",
                onValueChange = { input ->
                    // Limit total length to 20, only allow digits (no decimals)
                    if (input.length <= 20) {
                        if (input.isEmpty() || input.all { it.isDigit() }) {
                            allowanceAmount = input
                            val amount = input.toDoubleOrNull() ?: 0.0
                            onSettingsChange(settings.copy(allowanceAmount = amount, monthlyBudget = amount))
                        }
                    }
                }
            )

            Divider(color = ArkColors.Divider, modifier = Modifier.padding(horizontal = 16.dp))

            // Allowance Day
            SettingsDaySelector(
                icon = { Icon(Icons.Default.CalendarToday, null, tint = ArkColors.Accent, modifier = Modifier.size(18.dp)) },
                title = "发放日期",
                subtitle = "每月几号收到生活费",
                selectedDay = allowanceDay,
                onDaySelected = {
                    allowanceDay = it
                    onSettingsChange(settings.copy(allowanceDay = it))
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Data Management
        SectionTitle("数据管理")
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ArkColors.Surface)
                .border(1.dp, ArkColors.Border.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            SettingsClickItem(
                icon = { Icon(Icons.Default.Download, null, tint = ArkColors.Accent, modifier = Modifier.size(18.dp)) },
                title = "导出数据",
                subtitle = "备份为 JSON 文件",
                onClick = onExport
            )

            Divider(color = ArkColors.Divider, modifier = Modifier.padding(horizontal = 16.dp))

            SettingsClickItem(
                icon = { Icon(Icons.Default.Upload, null, tint = ArkColors.Income, modifier = Modifier.size(18.dp)) },
                title = "导入数据",
                subtitle = "从 JSON 文件恢复",
                onClick = onImport
            )

            Divider(color = ArkColors.Divider, modifier = Modifier.padding(horizontal = 16.dp))

            SettingsClickItem(
                icon = { Icon(Icons.Default.DeleteForever, null, tint = ArkColors.Expense, modifier = Modifier.size(18.dp)) },
                title = "清除所有数据",
                subtitle = "删除所有记录和设置",
                isDanger = true,
                onClick = { showClearConfirm = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // About
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ArkBook v1.0",
                style = MaterialTheme.typography.labelSmall,
                color = ArkColors.TextHint
            )
            Text(
                text = "个人记账助手 - 本地存储，数据安全",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = ArkColors.TextHint.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Clear confirmation dialog
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            containerColor = ArkColors.Surface,
            titleContentColor = ArkColors.TextPrimary,
            textContentColor = ArkColors.TextSecondary,
            icon = { Icon(Icons.Default.Warning, null, tint = ArkColors.Primary) },
            title = { Text("警告") },
            text = { Text("此操作将清除所有账单记录和设置，数据将无法恢复。建议先导出备份。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        showClearConfirm = false
                    }
                ) {
                    Text("确认清除", color = ArkColors.Expense)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("取消", color = ArkColors.TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ThemeSelector(
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("选择主题", color = ArkColors.TextPrimary) },
            containerColor = ArkColors.Surface,
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        val isSelected = mode == currentMode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ArkColors.Primary.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable {
                                    onModeSelected(mode)
                                    showDialog = false
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) ArkColors.Primary else ArkColors.TextPrimary
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, null, tint = ArkColors.Primary, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("取消", color = ArkColors.TextSecondary)
                }
            }
        )
    }

    SettingsClickItem(
        icon = { Icon(Icons.Default.Palette, null, tint = ArkColors.Accent, modifier = Modifier.size(18.dp)) },
        title = "主题模式",
        subtitle = "当前：${currentMode.label}",
        onClick = { showDialog = true }
    )
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = ArkColors.TextSecondary,
        letterSpacing = 2.sp
    )
}

@Composable
private fun ProfileCard(username: String, onUsernameChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ArkColors.Surface)
            .border(1.dp, ArkColors.Border, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ArkColors.Primary.copy(alpha = 0.1f))
                    .border(1.dp, ArkColors.Primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = ArkColors.Primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                androidx.compose.foundation.text.BasicTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ArkColors.TextPrimary
                    ),
                    singleLine = true,
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(ArkColors.Primary),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "点击修改昵称",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArkColors.TextTertiary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsNumberItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    value: String,
    prefix: String = "",
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ArkColors.SurfaceLight),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = ArkColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = ArkColors.TextTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.widthIn(min = 60.dp, max = 120.dp)
        ) {
            Text(
                text = prefix,
                style = MaterialTheme.typography.bodyLarge,
                color = ArkColors.Primary,
                fontWeight = FontWeight.Medium
            )
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    color = ArkColors.TextPrimary
                ),
                singleLine = true,
                cursorBrush = androidx.compose.ui.graphics.SolidColor(ArkColors.Primary),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )
        }
    }
}

@Composable
private fun SettingsDaySelector(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("选择发放日期", color = ArkColors.TextPrimary) },
            containerColor = ArkColors.Surface,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    (1..31).forEach { day ->
                        val isSelected = day == selectedDay
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ArkColors.Primary.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable {
                                    onDaySelected(day)
                                    showDialog = false
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${day}号",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) ArkColors.Primary else ArkColors.TextPrimary
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, null, tint = ArkColors.Primary, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("取消", color = ArkColors.TextSecondary)
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ArkColors.SurfaceLight),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = ArkColors.TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = ArkColors.TextTertiary
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(ArkColors.SurfaceLight)
                .border(1.dp, ArkColors.Border, RoundedCornerShape(10.dp))
                .clickable { showDialog = true }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "${selectedDay}号",
                style = MaterialTheme.typography.bodyLarge,
                color = ArkColors.TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SettingsClickItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    isDanger: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ArkColors.SurfaceLight),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDanger) ArkColors.Expense else ArkColors.TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = ArkColors.TextTertiary
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

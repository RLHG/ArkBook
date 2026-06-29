package com.rhodes.arkbook

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rhodes.arkbook.data.AppSettings
import com.rhodes.arkbook.data.TransactionEntity
import com.rhodes.arkbook.repository.MonthlyStats
import com.rhodes.arkbook.ui.navigation.Screen
import com.rhodes.arkbook.ui.screens.*
import com.rhodes.arkbook.ui.components.*
import com.rhodes.arkbook.utils.*
import com.rhodes.arkbook.ui.theme.ArkBookTheme
import com.rhodes.arkbook.ui.theme.ArkColors
import com.rhodes.arkbook.ui.viewmodel.MainViewModel
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(application)
            )
            val settings by viewModel.settings.collectAsState()

            ArkBookTheme(themeMode = settings.themeMode) {
                val navController = rememberNavController()
                val transactions by viewModel.transactions.collectAsState()
                val monthlyStats by viewModel.monthlyStats.collectAsState()
                val currentYear by viewModel.currentYear.collectAsState()
                val currentMonth by viewModel.currentMonth.collectAsState()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                var showMonthPicker by remember { mutableStateOf(false) }

                if (showMonthPicker) {
                    MonthPickerDialog(
                        currentYear = currentYear,
                        currentMonth = currentMonth,
                        onDismiss = { showMonthPicker = false },
                        onMonthSelected = { year, month ->
                            viewModel.selectMonth(year, month)
                            showMonthPicker = false
                        }
                    )
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(ArkColors.BackgroundGradient)),
                    containerColor = Color.Transparent,
                    bottomBar = {
                        if (currentRoute != Screen.AddRecord.route) {
                            BottomNavBar(
                                currentRoute = currentRoute ?: Screen.Home.route,
                                onNavigate = { route ->
                                    if (route != currentRoute) {
                                        navController.navigate(route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    },
                    floatingActionButton = {
                        if (currentRoute != Screen.AddRecord.route) {
                            FloatingActionButton(
                                onClick = { navController.navigate(Screen.AddRecord.route) },
                                containerColor = ArkColors.Primary,
                                contentColor = ArkColors.OnPrimary,
                                shape = CircleShape,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            val filteredTransactions = remember(transactions, currentYear, currentMonth) {
                                val cal = Calendar.getInstance()
                                cal.set(currentYear, currentMonth - 1, 1, 0, 0, 0)
                                val start = cal.timeInMillis
                                cal.set(currentYear, currentMonth - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
                                val end = cal.timeInMillis
                                transactions.filter { it.date in start..end }
                            }
                            HomeScreen(
                                stats = monthlyStats,
                                recentTransactions = filteredTransactions.take(6),
                                budget = settings.monthlyBudget,
                                username = settings.username,
                                monthLabel = "${currentYear}年${currentMonth}月",
                                onMonthClick = { showMonthPicker = true },
                                onAddClick = { navController.navigate(Screen.AddRecord.route) },
                                onViewAllClick = {
                                    navController.navigate(Screen.Records.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onDeleteTransaction = viewModel::deleteTransaction
                            )
                        }
                        composable(Screen.Records.route) {
                            RecordsScreen(
                                transactions = transactions,
                                onDelete = viewModel::deleteTransaction
                            )
                        }
                        composable(Screen.Stats.route) {
                            StatsScreen(
                                monthlyStats = monthlyStats,
                                categoryBreakdown = viewModel.getCategoryBreakdown(currentYear, currentMonth),
                                monthlyTrend = viewModel.getMonthlyTrend(),
                                onMonthClick = { showMonthPicker = true },
                                monthLabel = "${currentYear}年${currentMonth}月"
                            )
                        }
                        composable(Screen.Settings.route) {
                            val context = LocalContext.current
                            
                            SettingsScreen(
                                settings = settings,
                                onSettingsChange = viewModel::updateSettings,
                                onExport = { exportData(context, transactions, settings) },
                                onImport = { importData(context, viewModel) },
                                onClearAll = viewModel::deleteAllData,
                                onOpenNotificationSettings = {
                                    val intent = Intent().apply {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                        } else {
                                            action = "android.settings.APP_NOTIFICATION_SETTINGS"
                                            putExtra("app_package", context.packageName)
                                            putExtra("app_uid", context.applicationInfo.uid)
                                        }
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                        composable(Screen.AddRecord.route) {
                            AddRecordScreen(
                                initialDate = {
                                    val cal = Calendar.getInstance()
                                    val now = Calendar.getInstance()
                                    // If currently selected month is NOT current month, default to 1st of that month
                                    if (currentYear != now.get(Calendar.YEAR) || currentMonth != (now.get(Calendar.MONTH) + 1)) {
                                        cal.set(currentYear, currentMonth - 1, 1, 12, 0, 0)
                                    }
                                    cal.timeInMillis
                                }(),
                                onAdd = { amount, type, category, note, date ->
                                    viewModel.addTransaction(amount, type, category, note, date)
                                    navController.popBackStack()
                                },
                                onCancel = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

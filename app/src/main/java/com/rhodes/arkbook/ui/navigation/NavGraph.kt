package com.rhodes.arkbook.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Records : Screen("records")
    data object Stats : Screen("stats")
    data object Settings : Screen("settings")
    data object AddRecord : Screen("add_record")
}

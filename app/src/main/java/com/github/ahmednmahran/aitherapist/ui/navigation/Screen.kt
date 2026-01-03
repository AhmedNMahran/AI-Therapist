package com.github.ahmednmahran.aitherapist.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Chat : Screen("chat")
    data object Audio : Screen("audio")
    data object Video : Screen("video")
}

package com.example.ui.settings.model

data class SettingsState(
    val passcode: String = "",
    val passcodeActivated: Boolean = false,
    val isDarkTheme: Boolean = true,
    val fontSize: Int = 16
)

package com.antigravity.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.antigravity.mobile.ui.main.MainScreen
import com.antigravity.mobile.ui.main.MainViewModel
import com.antigravity.mobile.ui.theme.AntigravityTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as? AntigravityApp)?.container
        val viewModel = if (container != null) MainViewModel(container) else MainViewModel()

        setContent {
            AntigravityTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

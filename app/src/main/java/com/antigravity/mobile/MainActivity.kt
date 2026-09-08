package com.antigravity.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.antigravity.mobile.ui.main.MainScreen
import com.antigravity.mobile.ui.main.MainViewModel
import com.antigravity.mobile.ui.theme.AntigravityTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            AntigravityTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

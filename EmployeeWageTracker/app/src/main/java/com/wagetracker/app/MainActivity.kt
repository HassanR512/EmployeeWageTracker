package com.wagetracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wagetracker.app.navigation.WageTrackerNavHost
import com.wagetracker.app.ui.theme.WageTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as WageTrackerApp
        setContent {
            WageTrackerRoot(repository = app.repository)
        }
    }
}

@Composable
fun WageTrackerRoot(repository: com.wagetracker.app.repository.WageRepository) {
    WageTrackerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WageTrackerNavHost(repository = repository)
        }
    }
}

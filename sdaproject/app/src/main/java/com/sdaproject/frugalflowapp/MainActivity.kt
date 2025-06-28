package com.sdaproject.frugalflowapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseApp
import com.sdaproject.frugalflowapp.ui.theme.FrugalflowTheme

class MainActivity : ComponentActivity() {

    private val PREFS_NAME = "user_preferences"
    private val DARK_MODE_KEY = "dark_mode_enabled"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load dark mode preference and apply the theme
        val darkModeEnabled = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getBoolean(DARK_MODE_KEY, false)

        setContent {
            FrugalflowTheme(darkTheme = darkModeEnabled) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    // Save the dark mode preference
    private fun saveDarkModePreference(isDarkModeEnabled: Boolean) {
        val preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean(DARK_MODE_KEY, isDarkModeEnabled)
        editor.apply()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


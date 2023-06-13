package com.example.lastproject

import TokenManager
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.lastproject.pages.authentication.LoginScreen
import com.example.lastproject.pages.authentication.RegisterScreen
import com.example.lastproject.pages.main.MainLayout
import com.example.lastproject.ui.theme.LastProjectTheme
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @SuppressLint("UnsafeIntentLaunch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(applicationContext, "AIzaSyCghuAC_LCbKmjCS3l2oFX4NU8_2v6fEv0")
        val refresh = {
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        setContent {
            LastProjectTheme {
                MyApp(refresh)
            }
        }
    }
}


@Composable
fun MyApp(refresh: () -> Unit) {

    var loggedIn by remember { mutableStateOf(false) }
    var refreshToken by remember { mutableStateOf("") }
    var accessToken by remember { mutableStateOf("") }
    val context = LocalContext.current
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            refreshToken = TokenManager.getRefreshToken(context) ?: ""
        }
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            accessToken = TokenManager.getAccessToken(context) ?: ""
        }
        loaded = true
    }
    if(loaded){
        if (refreshToken.isNotBlank()) {
            loggedIn = true;
        }

        if (loggedIn) {
            LastProjectTheme {
                MainLayout(onLogOut = { loggedIn = false
                    refresh()}, reloadList = {refresh()}
                )
            }
        } else {


            var currentScreen by remember { mutableStateOf(Screen.Register) }

            val switchToLogin = {
                currentScreen = Screen.Login
            }
            val switchToRegister = {
                currentScreen = Screen.Register
            }

            when (currentScreen) {
                Screen.Register -> {
                    LastProjectTheme {
                        RegisterScreen(
                            onLoggedIn = { loggedIn = true },
                            onSwitchToLogin = switchToLogin
                        )
                    }
                }

                Screen.Login -> {
                    LastProjectTheme {
                        LoginScreen(
                            onLoggedIn = { loggedIn = true },
                            onSwitchToRegister = switchToRegister
                        )
                    }
                }
            }
        }
    }

}

enum class Screen {
    Register, Login
}

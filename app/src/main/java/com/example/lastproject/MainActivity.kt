package com.example.lastproject

import TokenManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.lastproject.pages.authentication.LoginScreen
import com.example.lastproject.pages.authentication.RegisterScreen
import com.example.lastproject.pages.main.MainLayout
import com.example.lastproject.ui.theme.LastProjectTheme
import com.example.projectapp.client.ApiClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LastProjectTheme {
//                val singapore = LatLng(52.41188699416633, 16.912229646116916)
//                val cameraPositionState = rememberCameraPositionState {
//                    position = CameraPosition.fromLatLngZoom(singapore, 10f)
//                }
//                GoogleMap(
//                    modifier = Modifier.fillMaxSize(),
//                    cameraPositionState = cameraPositionState
//                )
//
                MyApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyApp() {

//    val singapore = LatLng(1.35, 103.87)
//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(singapore, 10f)
//    }
//    GoogleMap(
//        modifier = Modifier.fillMaxSize(),
//        cameraPositionState = cameraPositionState
//    )
    var loggedIn by remember { mutableStateOf(false) }
    var refreshToken by remember { mutableStateOf("") }
    var accessToken by remember { mutableStateOf("") }
    val context = LocalContext.current
    var tickets by remember { mutableStateOf(emptyList<ApiClient.Ticket>()) }
    var loading by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            refreshToken = TokenManager.getRefreshToken(context) ?: ""
        }
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            accessToken = TokenManager.getAccessToken(context) ?: ""
        }
    }

    if (refreshToken.isNotBlank()) {
        loggedIn = true;
    }
    if (loggedIn) {
//        accessToken = TokenManager.getAccessToken(context) ?: ""
//        ApiClient.setClientsJwt(accessToken)
////        Text(text = "Welcome to the app!")
//        LaunchedEffect(Unit) {
//            withContext(Dispatchers.IO) {
//                val fetchedTickets = ApiClient.getTickets(accessToken)
//                tickets = fetchedTickets
//                loading = false
//            }
//        }
//        if (loading) {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator()
//            }
//        } else {
//            TicketList(tickets)
//        }
        LastProjectTheme {
            MainLayout(onLogOut = { loggedIn = false })
        }
    } else {


        var currentScreen by remember { mutableStateOf(Screen.Register) }

        // Switch to the LoginScreen
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

enum class Screen {
    Register, Login
}

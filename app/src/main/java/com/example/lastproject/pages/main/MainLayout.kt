package com.example.lastproject.pages.main

import TokenManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lastproject.AddTicketActivity
import com.example.lastproject.MainActivity
import com.example.lastproject.client.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainLayout(onLogOut: () -> Unit, reloadList: () -> Unit) {
    val context = LocalContext.current

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Refresh the activity or perform any necessary actions
            context.startActivity(Intent(context, MainActivity::class.java))
            reloadList()
        }
    }

    var tickets by remember { mutableStateOf(emptyList<ApiClient.Ticket>()) }
    var loading by remember { mutableStateOf(true) }
    val accessToken = TokenManager.getAccessToken(context) ?: ""
    ApiClient.setClientsJwt(accessToken)
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val fetchedTickets = ApiClient.getTickets(accessToken)
            tickets = fetchedTickets
            loading = false
        }
    }
    var showAddTicketLayout by remember { mutableStateOf(false) }

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { val intent = Intent(context, AddTicketActivity::class.java)
            activityLauncher.launch(intent)}
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Travel")
        }
    },
        topBar = {
            TopAppBar(
                title = { Text("Travel Journal") },
                actions = {
                    Row {
                        IconButton(onClick = {
                            reloadList()
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                        IconButton(onClick = {
                            TokenManager.clearTokens(context)
                            onLogOut()
                        }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                        }


                    }
                }
            )
        }

    ) {
        Column(
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    TicketList(tickets = tickets)
                }
            }

        }
    }
}

package com.example.lastproject.pages.main

import TokenManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.projectapp.client.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun MainLayout(onLogOut: () -> Unit) {
    val context = LocalContext.current
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
    Column {
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            TicketList(tickets)
        }

        Button(
            onClick = {
                TokenManager.clearTokens(context)
                onLogOut()
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text(text = "Logout")
        }

        Button(
            onClick = {
                showAddTicketLayout = !showAddTicketLayout
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text(text = "Add Ticket")
        }
        if (showAddTicketLayout) {
            // Show the AddTicketLayout in a dialog
            Dialog(
                onDismissRequest = { showAddTicketLayout = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Card(modifier = Modifier.fillMaxSize()) {
                    AddTicketLayout(
                        onAddTicket = { ticket ->
                            // Add the ticket to the list
//                            tickets.add(ticket)
                            // Close the dialog
                            showAddTicketLayout = false
                        }
                    )
                }
            }
        }
    }
}
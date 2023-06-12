package com.example.lastproject.pages.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projectapp.client.ApiClient


@Composable
fun TicketList(tickets: List<ApiClient.Ticket>) {

    LazyColumn {
        items(tickets) { ticket ->
            TicketItem(ticket)
        }
    }
}

@Composable
fun TicketItem(ticket: ApiClient.Ticket) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Ticket ID: ${ticket.id}")
            Text(text = "Name: ${ticket.name}")
            Text(text = "Travel Date: ${ticket.travel_date}")
            Text(text = "Start Location: ${ticket.start_location ?: "N/A"}")
            Text(text = "End Location: ${ticket.end_location ?: "N/A"}")
        }
    }
}
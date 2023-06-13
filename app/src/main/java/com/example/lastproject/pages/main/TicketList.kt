package com.example.lastproject.pages.main

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lastproject.TicketDetailsActivity
import com.example.lastproject.client.ApiClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun TicketList(tickets: List<ApiClient.Ticket>) {

    LazyColumn {
        items(tickets) { ticket ->
            TicketItem(ticket)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketItem(ticket: ApiClient.Ticket) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val dateTime = LocalDateTime.parse(ticket.travel_date, formatter)
    val formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = {
            val intent = Intent(context, TicketDetailsActivity::class.java)
            intent.putExtra("ticketId", ticket.id)
            context.startActivity(intent)
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${ticket.name}",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(text = "Travel Date: $formattedDate")
            Text(text = "Start Location: ${ticket.start_location?.name ?: "N/A"}")
            Text(text = "End Location: ${ticket.end_location?.name ?: "N/A"}")
        }
    }
}
package com.example.lastproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lastproject.pages.main.AddTicketLayout
import com.example.lastproject.pages.main.TicketDetailsLayout
import com.example.lastproject.ui.theme.LastProjectTheme

class TicketDetailsActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ticketId = intent.getIntExtra("ticketId", -1)
        if (ticketId == -1){
            closeActivity()
        }
        setContent {
            LastProjectTheme {
                TicketDetailsLayout({ closeActivity() }, ticketId )
            }
        }
    }

    private fun closeActivity() {
        finish()
    }


}
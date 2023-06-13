package com.example.lastproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lastproject.pages.main.AddTicketLayout
import com.example.lastproject.ui.theme.LastProjectTheme

class AddTicketActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LastProjectTheme {
                AddTicketLayout { closeActivity() }
            }
        }
    }

    private fun closeActivity() {
        finish()
    }

}
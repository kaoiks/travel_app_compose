package com.example.lastproject.pages.main

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectapp.client.ApiClient
import com.google.android.gms.maps.model.LatLng

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTicketLayout(onAddTicket: (ApiClient.Ticket) -> Unit) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var fileField by remember { mutableStateOf<String?>(null) }
    var travelDate by remember { mutableStateOf("") }
    var startLocation by remember { mutableStateOf<String?>(null) }
    var endLocation by remember { mutableStateOf<String?>(null) }

    val calendar = Calendar.getInstance()

    var selectedDateText by remember { mutableStateOf("") }

// Fetching current year, month and day
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            selectedDateText = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        }, year, month, dayOfMonth
    )
    datePicker.datePicker.minDate = calendar.timeInMillis

    Column(modifier = Modifier.padding(2.dp)) {
        Text(text = "Add Ticket", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Optional file field
        OutlinedTextField(
            value = fileField ?: "",
            onValueChange = { fileField = it.takeIf { it.isNotEmpty() } },
            label = { Text("File Field") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                datePicker.show()
            }
        ) {
            Text(text = "Select a date")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Optional start location field
        OutlinedTextField(
            value = startLocation ?: "",
            onValueChange = { startLocation = it.takeIf { it.isNotEmpty() } },
            label = { Text("Start Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        var uiSettings by remember { mutableStateOf(MapUiSettings()) }
        var properties by remember {
            mutableStateOf(MapProperties(mapType = MapType.NORMAL))
        }


        var clickedLocation by remember { mutableStateOf<LatLng?>(null) }
        Box(Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                properties = properties,
                uiSettings = uiSettings,
                onMapClick = { latLng ->
                    clickedLocation = latLng
                }
            )
        }



        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val parsedDateTime = LocalDateTime.parse(travelDate, formatter)
                val ticket = ApiClient.Ticket(
                    id = 0, // Assign 0 or generate a unique ID
                    name = name,
                    file_field = fileField,
                    travel_date = parsedDateTime.toString(),
                    start_location = startLocation,
                    end_location = endLocation
                )
                onAddTicket(ticket)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Add Ticket")
        }
    }
}
package com.example.lastproject.pages.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.lastproject.R
import com.example.lastproject.client.ApiClient
import com.example.lastproject.client.ApiClient.Ticket
import com.example.lastproject.ui.theme.LastProjectTheme
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TicketDetailsLayout(closeActivity: () -> Unit, ticketId: Int) {
    val context = LocalContext.current

    var ticket by remember { mutableStateOf(Ticket(
        id = 0,
        name = "",
        file_field = null,
        travel_date = "",
        start_location = null,
        end_location = null
    ))}

    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val fetchedTicket = ApiClient.getTicket(context, ticketId)
            ticket = fetchedTicket
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Travel Details") },
                navigationIcon = {

                    Row {
                        IconButton(onClick = { closeActivity() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }

                    }
                },
                actions = {
                    if (ticket.file_field != null) {

                        IconButton(onClick = {
                            openBrowser(context, ticket.file_field!!)
                        }) {
                            val icon: Painter = painterResource(R.drawable.download_icon)
                            Icon(icon, contentDescription = "Logout")
                        }

                    }
                }
            )
        }
    ) {
        if (loading) {
            Box(
                modifier = Modifier
                    .padding(40.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val dateTime = LocalDateTime.parse(ticket.travel_date, formatter)
            val formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {

                Column(
                    modifier = Modifier
                        .padding(vertical = 40.dp)
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = ticket.name,
                                style = TextStyle(
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(text = "Travel Date:\n$formattedDate",
                                style = TextStyle(
                                    fontSize = 22.sp,)
                            )
                            Text(text = "Starting Location:\n${ticket.start_location?.name ?: "N/A"}",
                                style = TextStyle(
                                    fontSize = 22.sp)
                            )
                            Text(text = "Destination:\n${ticket.end_location?.name ?: "N/A"}",
                                style = TextStyle(
                                    fontSize = 22.sp)
                            )
                            Spacer(modifier = Modifier.padding(10.dp))


                        }
                    }

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        var properties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(
                                LatLng(
                                    ticket.start_location?.latitude!!.toDouble(),
                                    ticket.start_location?.longitude!!.toDouble()
                                ), 5f
                            )
                        }
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxSize()
                        ) {


                            Text(
                                text = "Map",
                                style = TextStyle(
                                    fontSize = 25.sp
                                )
                            )
                            GoogleMap(
                                properties = properties,
                                cameraPositionState = cameraPositionState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                MarkerInfoWindowContent(
                                    state = MarkerState(
                                        position = LatLng(
                                            ticket.start_location?.latitude!!.toDouble(),
                                            ticket.start_location?.longitude!!.toDouble()
                                        )
                                    ),
                                    title = "Starting Point",
                                    icon = BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_BLUE
                                    ),
                                )

                                Marker(
                                    state = MarkerState(
                                        position = LatLng(
                                            ticket.end_location?.latitude!!.toDouble(),
                                            ticket.end_location?.longitude!!.toDouble()
                                        )
                                    ),
                                    title = "Destination",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun openBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    startActivity(context, intent, null)
}


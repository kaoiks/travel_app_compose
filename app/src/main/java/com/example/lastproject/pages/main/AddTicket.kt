package com.example.lastproject.pages.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lastproject.client.ApiClient
import com.example.lastproject.client.ApiClient.fetchLocationInfo
import com.example.lastproject.ui.theme.LastProjectTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

@SuppressLint("CoroutineCreationDuringComposition", "MissingPermission",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTicketLayout(closeActivity: () -> Unit) {
    val context = LocalContext.current
    val placesClient: PlacesClient = Places.createClient(context)

    var name by remember { mutableStateOf("") }
    var fileField by remember { mutableStateOf<String?>(null) }
    var travelDate by remember { mutableStateOf("") }
    var startLocation by remember { mutableStateOf<String?>(null) }
    var endLocation by remember { mutableStateOf<String?>(null) }
    val showDialogEnd = remember { mutableStateOf(false) }
    val showDialogStart = remember { mutableStateOf(false) }
    val locationDetailsStart = remember { mutableStateOf("") }
    val locationDetailsEnd = remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()

    var selectedDateText by remember { mutableStateOf("") }


    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            selectedDateText =
                "$selectedYear-${selectedMonth + 1}-${selectedDayOfMonth}T00:00:00.000000Z"
        }, year, month, dayOfMonth
    )
    val selectedFileUri = remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            selectedFileUri.value = selectedUri.toString()
        }
    }

    val selectedLocationEnd = remember { mutableStateOf<LatLng?>(null) }
    val handleLocationDataEnd = { location: LatLng? ->
        selectedLocationEnd.value = location
    }

    val selectedLocationStart = remember { mutableStateOf<LatLng?>(null) }

    val handleLocationDataStart = { location: LatLng? ->
        selectedLocationStart.value = location

    }



    LastProjectTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Ticket Details") },
                    navigationIcon = {
                        IconButton(onClick = { closeActivity() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Add Travel",
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            )


                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally),
                                onClick = {
                                    filePickerLauncher.launch("application/*")
                                }
                            ) {
                                Text(text = "Choose Ticket File")
                            }

                            selectedFileUri.value?.let {
                                Text(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.CenterHorizontally),
                                    text = "File selected"
                                )
                            }



                            Button(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally),
                                onClick = {
                                    datePicker.show()
                                }
                            ) {
                                Text(text = "Select date")
                            }
                            Text(
                                text = "Selected Date: $selectedDateText",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally),
                            )


                            Button(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally),
                                onClick = {
                                    showDialogStart.value = true
                                }
                            ) {
                                Text(text = "Select Start Location")
                            }
//                        if (selectedLocationStart.value != null) {
//                            Text(
//                                text = "Selected Start Location Latitude: ${selectedLocationStart.value!!.latitude}\n" +
//                                        "Selected Start Location Longitude: ${selectedLocationStart.value!!.longitude}\n",
//                                modifier = Modifier.padding(16.dp)
//                            )
//                        }


                            if (showDialogStart.value) {
                                AlertDialog(
                                    onDismissRequest = {
                                        showDialogStart.value = false

                                        GlobalScope.launch(Dispatchers.IO) {
                                            try {
                                                val response = fetchLocationInfo(
                                                    selectedLocationStart.value!!.latitude,
                                                    selectedLocationStart.value!!.longitude
                                                )
                                                try {
                                                    val parts = response!!.split(", ")
                                                    val lastTwoParts =
                                                        parts.takeLast(2).joinToString(", ")
                                                    locationDetailsStart.value = lastTwoParts
                                                } catch (e: Exception) {
                                                    locationDetailsStart.value = ""
                                                }
                                            } catch (e: Exception) {
                                                println(e)
                                            }
                                        }

                                    },
                                    text = {
                                        MapWithSearch(onLocationSelected = handleLocationDataStart)
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                showDialogStart.value = false

                                                GlobalScope.launch(Dispatchers.IO) {
                                                    try {
                                                        val response = fetchLocationInfo(
                                                            selectedLocationStart.value!!.latitude,
                                                            selectedLocationStart.value!!.longitude
                                                        )
                                                        try {
                                                            val parts = response!!.split(", ")
                                                            val lastTwoParts =
                                                                parts.takeLast(2).joinToString(", ")
                                                            locationDetailsStart.value =
                                                                lastTwoParts
                                                        } catch (e: Exception) {
                                                            locationDetailsStart.value = ""
                                                        }

                                                    } catch (e: Exception) {
                                                        println(e)
                                                    }
                                                }

                                            }
                                        ) {
                                            Text(text = "Close")
                                        }
                                    },
                                    modifier = Modifier.padding(2.dp)
                                )
                            }


                            if (locationDetailsStart.value != "") {
                                Text(
                                    text = locationDetailsStart.value,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.CenterHorizontally),
                                )
                            }


                            Button(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally),
                                onClick = {
                                    showDialogEnd.value = true
                                }
                            ) {
                                Text(text = "Select End Location")
                            }
//                        if (selectedLocationEnd.value != null) {
//                            Text(
//                                text = "Selected End Location Latitude: ${selectedLocationEnd.value!!.latitude}\n" +
//                                        "Selected End Location Longitude: ${selectedLocationEnd.value!!.longitude}\n",
//                                modifier = Modifier.padding(16.dp)
//                            )
//                        }

                            if (showDialogEnd.value) {
                                AlertDialog(
                                    onDismissRequest = {
                                        showDialogEnd.value = false

                                        GlobalScope.launch(Dispatchers.IO) {
                                            try {
                                                val response = fetchLocationInfo(
                                                    selectedLocationEnd.value!!.latitude,
                                                    selectedLocationEnd.value!!.longitude
                                                )
                                                try {
                                                    val parts = response!!.split(", ")
                                                    val lastTwoParts =
                                                        parts.takeLast(2).joinToString(", ")
                                                    locationDetailsEnd.value = lastTwoParts
                                                } catch (e: Exception) {
                                                    locationDetailsEnd.value = ""
                                                }

                                            } catch (e: Exception) {
                                                println(e)
                                            }
                                        }
                                    },
                                    text = {
                                        MapWithSearch(onLocationSelected = handleLocationDataEnd)
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                showDialogEnd.value = false

                                                GlobalScope.launch(Dispatchers.IO) {
                                                    try {
                                                        val response = fetchLocationInfo(
                                                            selectedLocationEnd.value!!.latitude,
                                                            selectedLocationEnd.value!!.longitude
                                                        )
                                                        try {
                                                            val parts = response!!.split(", ")
                                                            val lastTwoParts =
                                                                parts.takeLast(2).joinToString(", ")
                                                            locationDetailsEnd.value = lastTwoParts
                                                        } catch (e: Exception) {
                                                            locationDetailsEnd.value = ""
                                                        }

                                                    } catch (e: Exception) {
                                                        println(e)
                                                    }
                                                }

                                            }
                                        ) {
                                            Text(text = "Close")
                                        }
                                    },
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                            if (locationDetailsEnd.value != "") {
                                Text(
                                    text = locationDetailsEnd.value,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.CenterHorizontally),
                                )
                            }





                            Button(
                                onClick = {
                                    try {
                                        val endLocation = ApiClient.Location(
                                            name = locationDetailsEnd.value,
                                            latitude = selectedLocationEnd.value!!.latitude.toString(),
                                            longitude = selectedLocationEnd.value!!.longitude.toString()
                                        )

                                        val startLocation = ApiClient.Location(
                                            name = locationDetailsStart.value,
                                            latitude = selectedLocationStart.value!!.latitude.toString(),
                                            longitude = selectedLocationStart.value!!.longitude.toString()
                                        )
                                        val ticketPost = ApiClient.TicketPost(
                                            name = name.toString(),
                                            travel_date = selectedDateText,
                                            start_location = startLocation,
                                            end_location = endLocation
                                        )

                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                val added = ApiClient.addTicket(context, ticketPost)
                                                if (added != -1 && selectedFileUri.value != null) {
                                                    val result = ApiClient.addFileToTicket(
                                                        context,
                                                        added,
                                                        selectedFileUri.value!!
                                                    )
                                                    closeActivity()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Something went wrong",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                            } catch (e: Exception) {
                                                println(e)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(text = "Add Travel")
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapWithSearch(onLocationSelected: (LatLng?) -> Unit) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(LatLng(52.231045489134445, 20.999396008501584), 10f)
    }

    var properties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
    val longitude = remember { mutableStateOf(0.0) }
    val latitude = remember { mutableStateOf(0.0) }
    Column {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = properties,
            cameraPositionState = cameraPositionState,
            onMapClick =
            { googleMap ->

                longitude.value = googleMap.longitude
                latitude.value = googleMap.latitude

            }) {
            if (longitude.value != 0.0 && latitude.value != 0.0) {
                Marker(
                    state = MarkerState(position = LatLng(latitude.value, longitude.value)),
                    title = "Selected Location"
                )
                onLocationSelected(LatLng(latitude.value, longitude.value))
            }
        }

        Button(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                if (longitude.value != 0.0 && latitude.value != 0.0) {
                    print("CLICKED")
                }
            }
        ) {
            Text(text = "Choose location")
        }
    }
}





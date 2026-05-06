package com.example.wssm.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.wssm.ui.theme.BrandDeepBlue
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun LocationScreen(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fusedLocationClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    val sheetState = rememberModalBottomSheetState()

    var showSheet by remember { mutableStateOf(false) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var nearbyAmenities by remember {
        mutableStateOf<List<NearbyPlace>>(emptyList())
    }

    var userLocation by remember {
        mutableStateOf(LatLng(15.2993, 74.0240))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
        }
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 13f)
    }

    LaunchedEffect(hasPermission) {

        if (hasPermission) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->

                location?.let {

                    val currentLatLng =
                        LatLng(it.latitude, it.longitude)

                    userLocation = currentLatLng

                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng,
                                13f
                            )
                        )
                    }

                    // OLD PLACES API FETCH
                    fetchNearbyPlaces(
                        location = it
                    ) { places ->

                        nearbyAmenities = places
                    }
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {

            if (hasPermission) {

                ExtendedFloatingActionButton(
                    onClick = { showSheet = true },
                    icon = {
                        Icon(Icons.Default.List, null)
                    },
                    text = {
                        Text(text = "${nearbyAmenities.size} Nearby")
                    },
                    containerColor = BrandDeepBlue,
                    contentColor = Color.White
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (hasPermission) {

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true
                    ),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true
                    )
                ) {

                    // USER LOCATION MARKER
                    Marker(
                        state = MarkerState(position = userLocation),
                        title = "You are here",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED
                        )
                    )

                    // NEARBY PLACES MARKERS
                    nearbyAmenities.forEach { place ->

                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    place.latitude,
                                    place.longitude
                                )
                            ),

                            title = place.name,

                            icon = BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_AZURE
                            )
                        )
                    }
                }

            } else {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Button(
                        onClick = {
                            launcher.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        }
                    ) {
                        Text("Grant Location Permission")
                    }
                }
            }

            // BOTTOM SHEET
            if (showSheet) {

                ModalBottomSheet(
                    onDismissRequest = {
                        showSheet = false
                    },
                    sheetState = sheetState
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Text(
                            text = "Nearby Services",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                        ) {

                            items(nearbyAmenities) { place ->

                                ListItem(
                                    headlineContent = {
                                        Text(place.name)
                                    },

                                    supportingContent = {
                                        Text(place.address)
                                    },

                                    trailingContent = {

                                        IconButton(
                                            onClick = {

                                                val uri = Uri.parse(
                                                    "google.navigation:q=${place.latitude},${place.longitude}"
                                                )

                                                val intent = Intent(
                                                    Intent.ACTION_VIEW,
                                                    uri
                                                ).apply {
                                                    setPackage("com.google.android.apps.maps")
                                                }

                                                context.startActivity(intent)
                                            }
                                        ) {

                                            Icon(
                                                Icons.Default.Directions,
                                                null,
                                                tint = BrandDeepBlue
                                            )
                                        }
                                    }
                                )

                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

// DATA CLASS
data class NearbyPlace(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

// OLD PLACES API FUNCTION
private fun fetchNearbyPlaces(
    location: Location,
    onResult: (List<NearbyPlace>) -> Unit
) {

    Thread {

        try {

            val apiKey = "YOUR_API_KEY"

            val url =
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                        "?location=${location.latitude},${location.longitude}" +
                        "&radius=3000" +
                        "&type=hospital" +
                        "&key=$apiKey"

            val response =
                URL(url).readText()

            val jsonObject =
                JSONObject(response)

            val results =
                jsonObject.getJSONArray("results")

            val places =
                mutableListOf<NearbyPlace>()

            for (i in 0 until results.length()) {

                val item =
                    results.getJSONObject(i)

                val geometry =
                    item.getJSONObject("geometry")

                val loc =
                    geometry.getJSONObject("location")

                places.add(
                    NearbyPlace(
                        name = item.getString("name"),
                        address = item.optString(
                            "vicinity",
                            "Nearby"
                        ),
                        latitude = loc.getDouble("lat"),
                        longitude = loc.getDouble("lng")
                    )
                )
            }

            onResult(places)

        } catch (e: Exception) {

            Log.e(
                "PLACES",
                "Error: ${e.message}"
            )
        }

    }.start()
}
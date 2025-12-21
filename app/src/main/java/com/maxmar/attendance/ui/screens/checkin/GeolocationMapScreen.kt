package com.maxmar.attendance.ui.screens.checkin

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

/**
 * Geolocation map screen showing user and office locations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeolocationMapScreen(
    userLat: Double,
    userLon: Double,
    officeLat: Double,
    officeLon: Double,
    radiusMeters: Int,
    officeName: String,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val appColors = LocalAppColors.current
    
    // Calculate distance
    val distance = LocationManager.calculateDistance(userLat, userLon, officeLat, officeLon)
    val isWithinRadius = distance <= radiusMeters
    
    val distanceText = if (distance < 1000) {
        "${distance.toInt()} m"
    } else {
        String.format("%.2f km", distance / 1000)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lokasi Anda", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appColors.surface,
                    titleContentColor = appColors.textPrimary,
                    navigationIconContentColor = appColors.textPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(appColors.backgroundGradientEnd)
        ) {
            // Map view
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                OSMMapView(
                    context = context,
                    userLat = userLat,
                    userLon = userLon,
                    officeLat = officeLat,
                    officeLon = officeLon,
                    radiusMeters = radiusMeters,
                    isWithinRadius = isWithinRadius
                )
            }
            
            // Bottom info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = appColors.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status icon
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isWithinRadius) 
                                        MaxmarColors.Success.copy(alpha = 0.1f) 
                                    else 
                                        MaxmarColors.Error.copy(alpha = 0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isWithinRadius) Icons.Default.CheckCircle else Icons.Default.LocationOff,
                                contentDescription = null,
                                tint = if (isWithinRadius) MaxmarColors.Success else MaxmarColors.Error,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = if (isWithinRadius) "Dalam Jangkauan" else "Di Luar Jangkauan",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isWithinRadius) MaxmarColors.Success else MaxmarColors.Error
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Jarak: $distanceText dari $officeName",
                                style = MaterialTheme.typography.bodyMedium,
                                color = appColors.textSecondary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaxmarColors.Primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kembali", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun OSMMapView(
    context: Context,
    userLat: Double,
    userLon: Double,
    officeLat: Double,
    officeLon: Double,
    radiusMeters: Int,
    isWithinRadius: Boolean
) {
    val mapView = remember {
        Configuration.getInstance().userAgentValue = context.packageName
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
            controller.setCenter(GeoPoint(officeLat, officeLon))
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach()
        }
    }
    
    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize(),
        update = { map ->
            map.overlays.clear()
            
            // Add radius circle
            val circle = Polygon().apply {
                points = Polygon.pointsAsCircle(
                    GeoPoint(officeLat, officeLon),
                    radiusMeters.toDouble()
                )
                fillPaint.color = android.graphics.Color.argb(50, 34, 197, 94) // Green with alpha
                outlinePaint.color = android.graphics.Color.rgb(34, 197, 94)
                outlinePaint.strokeWidth = 3f
            }
            map.overlays.add(circle)
            
            // Add office marker
            val officeMarker = Marker(map).apply {
                position = GeoPoint(officeLat, officeLon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Kantor"
            }
            map.overlays.add(officeMarker)
            
            // Add user marker
            val userMarker = Marker(map).apply {
                position = GeoPoint(userLat, userLon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Lokasi Anda"
            }
            map.overlays.add(userMarker)
            
            map.invalidate()
        }
    )
}

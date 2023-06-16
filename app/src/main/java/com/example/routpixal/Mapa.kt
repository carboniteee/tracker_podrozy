package com.example.routpixal

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.ArrayList
import org.osmdroid.bonuspack.routing.*
import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Geocoder
import com.google.firebase.database.*
import java.io.IOException


class Mapa : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView
    private lateinit var markerOverlay: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.mapa)

        /*
        readLastEntryFromDatabase()
        */
        map = findViewById(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)
        val mapController = map.controller
        mapController.setZoom(18.5)
        val startPoint = GeoPoint(52.51541145744718, 13.397866774345298)
        mapController.setCenter(startPoint)

        val endPoint = GeoPoint(52.51536943714169, 13.397170965767296)
        val intermediatePoints = ArrayList<GeoPoint>()
        intermediatePoints.add(GeoPoint(52.5072974705042, 13.39791073882347))
        intermediatePoints.add(GeoPoint(52.50652557133142, 13.386127210857268))
        intermediatePoints.add(GeoPoint(52.51392238088504, 13.38221698184606))
        createRoute(startPoint, endPoint, intermediatePoints)
        addMarkers(startPoint, endPoint, intermediatePoints)

        val markerIcon: Drawable = getDrawable(R.drawable.point)!!

        val startMarker = createMarker(startPoint, markerIcon)
        startMarker.setOnMarkerClickListener { marker, mapView ->
            val intent = Intent(this, Pamiatki::class.java)
            startActivity(intent)
            true
        }

        val endMarker = createMarker(endPoint, markerIcon)
        endMarker.setOnMarkerClickListener { marker, mapView ->
            true
        }

        markerOverlay = createMarkerOverlay(markerIcon)

        map.overlays.add(markerOverlay)
        map.overlays.add(startMarker)
        map.overlays.add(endMarker)

        for (overlay in map.overlays) {
            if (overlay is Marker) {
                overlay.setOnMarkerClickListener { marker, mapView ->
                    val intent = Intent(this, Pamiatki::class.java)
                    startActivity(intent)
                    true
                }
            }
        }
        map.invalidate()
    }
    fun geocodeLocation(context: Context, locationName: String): Pair<Double, Double>? {
        val geocoder = Geocoder(context)

        try {
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val latitude = address.latitude
                    val longitude = address.longitude
                    return Pair(latitude, longitude)
                }
            }
        } catch (e: IOException) {

        }
        return null
    }

    fun readLastEntryFromDatabase(callback: (MutableList<String>?) -> Unit) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("wiadomosci")

        val query: Query = databaseReference.orderByChild("id").limitToLast(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val podroz: Podroz? = childSnapshot.getValue(Podroz::class.java)
                    // Przetwarzaj odczytane dane
                    if (podroz != null) {
                        // Odczytaj ostatni wpis
                        println("Ostatni wpis: ${podroz.allPoints}")
                        callback(podroz.allPoints)
                    } else {
                        println("Nie ma danych")
                        callback(null)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Obsłuż błąd odczytu
                println("Błąd odczytu danych: ${databaseError.message}")
                callback(null)
            }
        })
    }

    private fun createMarker(point: GeoPoint, markerIcon: Drawable?): Marker {
        val marker = Marker(map)
        marker.position = point
        marker.icon = markerIcon
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.infoWindow = null
        return marker
    }

    private fun createMarkerOverlay(markerIcon: Drawable?): Marker {
        val markerOverlay = Marker(map)
        markerOverlay.icon = markerIcon
        markerOverlay.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_BOTTOM)
        return markerOverlay
    }

    private fun createRoute(startPoint: GeoPoint, endPoint: GeoPoint, intermediatePoints: ArrayList<GeoPoint>) {
        val routingTask = RoutingTask()
        val points = ArrayList<GeoPoint>()
        points.add(startPoint)
        points.addAll(intermediatePoints)
        points.add(endPoint)
        routingTask.execute(*points.toTypedArray())
    }

    private val markerInfoMap: MutableMap<Marker, MarkerInfo> = mutableMapOf()

    private fun addMarkers(startPoint: GeoPoint, endPoint: GeoPoint, intermediatePoints: ArrayList<GeoPoint>) {
        val markerIcon = getDrawable(R.drawable.point)
        val color = Color.parseColor("#0eacef")

        val startMarker = createMarker(startPoint, markerIcon)
        val endMarker = createMarker(endPoint, markerIcon)

        // Dodaj informacje o markerach do mapy
        markerInfoMap[startMarker] = MarkerInfo()
        markerInfoMap[endMarker] = MarkerInfo()

        for (point in intermediatePoints) {
            val intermediateMarker = createMarker(point, markerIcon)
            markerInfoMap[intermediateMarker] = MarkerInfo()
            map.overlays.add(intermediateMarker)
        }

        markerOverlay = createMarkerOverlay(markerIcon)

        map.overlays.add(markerOverlay)
        map.overlays.add(startMarker)
        map.overlays.add(endMarker)

        for (overlay in map.overlays) {
            if (overlay is Marker) {
                overlay.setOnMarkerClickListener { marker, mapView ->
                    val markerInfo = markerInfoMap[marker]
                    // Przekaż markerInfo do aktywności Pamiatki i obsłuż go tam
                    val intent = Intent(this, Pamiatki::class.java)
                    intent.putExtra("markerInfo", markerInfo)
                    startActivity(intent)
                    true
                }
            }
        }
        map.invalidate()
    }


    private inner class RoutingTask : AsyncTask<GeoPoint, Void, Road>() {
        override fun doInBackground(vararg params: GeoPoint): Road? {
            val points = params.toList()

            val roadManager: RoadManager = OSRMRoadManager(applicationContext, "userAgent")
            val road: Road = roadManager.getRoad(points as ArrayList<GeoPoint>?)

            return if (road.mStatus == Road.STATUS_OK) road else null
        }

        override fun onPostExecute(result: Road?) {
            if (result != null) {
                val color = Color.parseColor("#0eacef")
                val roadOverlay = RoadManager.buildRoadOverlay(result, color, 20f)
                map.overlays.add(0, roadOverlay)
                map.invalidate()
            } else {
                // co gdy nie można wyznaczyć trasy
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }
}

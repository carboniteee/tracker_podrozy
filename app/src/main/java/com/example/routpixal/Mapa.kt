package com.example.routpixal

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

class Mapa : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView
    private lateinit var markerOverlay: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.mapa)

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

        lateinit var markerIcon: Drawable
        markerIcon = getDrawable(R.drawable.point)!!

        val startMarker = Marker(map)
        startMarker.position = startPoint
        startMarker.icon = markerIcon
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        startMarker.infoWindow = null
        startMarker.setOnMarkerClickListener { marker, mapView ->
            val intent = Intent(this, Pamiatki::class.java)
            startActivity(intent)
            true
        }

        val endMarker = Marker(map)
        endMarker.position = endPoint
        endMarker.icon = markerIcon
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        endMarker.infoWindow = null
        endMarker.setOnMarkerClickListener { marker, mapView ->
            true
        }

        markerOverlay = Marker(map)
        markerOverlay.icon = markerIcon
        markerOverlay.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

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

    private fun createRoute(startPoint: GeoPoint, endPoint: GeoPoint, intermediatePoints: ArrayList<GeoPoint>) {
        val routingTask = RoutingTask()
        val points = ArrayList<GeoPoint>()
        points.add(startPoint)
        points.addAll(intermediatePoints)
        points.add(endPoint)
        routingTask.execute(*points.toTypedArray())
    }

    private fun addMarkers(startPoint: GeoPoint, endPoint: GeoPoint, intermediatePoints: ArrayList<GeoPoint>) {
        val markerIcon = getDrawable(R.drawable.point)
        val color = Color.parseColor("#0eacef")

        val startMarker = Marker(map)
        startMarker.position = startPoint
        startMarker.icon = markerIcon
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        startMarker.infoWindow = null

        for (point in intermediatePoints) {
            val intermediateMarker = Marker(map)
            intermediateMarker.position = point
            intermediateMarker.icon = markerIcon
            intermediateMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            intermediateMarker.infoWindow = null
            map.overlays.add(intermediateMarker)
        }

        val endMarker = Marker(map)
        endMarker.position = endPoint
        endMarker.icon = markerIcon
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        endMarker.infoWindow = null

        markerOverlay = Marker(map)
        markerOverlay.icon = markerIcon
        markerOverlay.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

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

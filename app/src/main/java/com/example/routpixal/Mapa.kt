package com.example.routpixal

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.routpixal.R
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.util.ArrayList
import org.osmdroid.bonuspack.routing.*
import org.osmdroid.bonuspack.routing.RoadManager.*

class Mapa : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView

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

        // Przykładowe wywołanie funkcji createRoute
        val endPoint = GeoPoint(52.51536943714169, 13.397170965767296) // Przykładowy punkt końcowy
        val intermediatePoints = ArrayList<GeoPoint>()
        intermediatePoints.add(GeoPoint(52.5072974705042, 13.39791073882347)) // Przykładowe punkty pośrednie
        intermediatePoints.add(GeoPoint(52.50652557133142, 13.386127210857268))
        intermediatePoints.add(GeoPoint(52.51392238088504, 13.38221698184606))
        createRoute(startPoint, endPoint, intermediatePoints)
    }

    private fun createRoute(startPoint: GeoPoint, endPoint: GeoPoint, intermediatePoints: ArrayList<GeoPoint>) {
        val routingTask = RoutingTask()
        val points = ArrayList<GeoPoint>()
        points.add(startPoint)
        points.addAll(intermediatePoints)
        points.add(endPoint)
        routingTask.execute(*points.toTypedArray())
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
                val roadOverlay = RoadManager.buildRoadOverlay(result, color, 20f) // Ustawienie koloru i grubości linii
                map.overlays.add(roadOverlay)

                val startPointMarker = Marker(map)
                startPointMarker.position = result.mNodes[0].mLocation
                startPointMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                startPointMarker.title = "Punkt początkowy"
                startPointMarker.showInfoWindow()
                map.overlays.add(startPointMarker)

                val endPointMarker = Marker(map)
                endPointMarker.position = result.mNodes[result.mNodes.size - 1].mLocation
                endPointMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                endPointMarker.title = "Punkt końcowy"
                endPointMarker.showInfoWindow()
                map.overlays.add(endPointMarker)

                val maxIntermediatePoints = 3
                for (i in 1 until result.mNodes.size - 1) {
                    val node = result.mNodes[i]
                    if (!node.mInstructions.isNullOrEmpty()) {
                        val marker = Marker(map)
                        marker.position = node.mLocation
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = "Punkt pośredni"
                        marker.showInfoWindow()
                        map.overlays.add(marker)

                        // Dodaj warunek sprawdzający ilość dodanych markerów pośrednich
                        if (map.overlays.size - 2 > maxIntermediatePoints) {
                            map.overlays.removeAt(map.overlays.size - 3)
                        }
                    }
                }


                map.invalidate()
            } else {
                // Obsługa błędu, gdy nie można wyznaczyć trasy
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
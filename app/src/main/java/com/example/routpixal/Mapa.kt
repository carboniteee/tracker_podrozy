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
import android.graphics.Paint

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
        val startPoint = GeoPoint(50.29489671148186, 18.93392417602203)
        mapController.setCenter(startPoint)

        // Przykładowe wywołanie funkcji createRoute
        val endPoint = GeoPoint(51.234567, 19.345678) // Przykładowy punkt końcowy
        createRoute(startPoint, endPoint)
    }

    private fun createRoute(startPoint: GeoPoint, endPoint: GeoPoint) {
        val routingTask = RoutingTask()
        routingTask.execute(startPoint, endPoint)
    }

    private inner class RoutingTask : AsyncTask<GeoPoint, Void, Road>() {
        override fun doInBackground(vararg params: GeoPoint): Road? {
            val startPoint = params[0]
            val endPoint = params[1]

            val roadManager: RoadManager = OSRMRoadManager(applicationContext, "userAgent")
            val road: Road = roadManager.getRoad(arrayListOf(startPoint, endPoint))

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

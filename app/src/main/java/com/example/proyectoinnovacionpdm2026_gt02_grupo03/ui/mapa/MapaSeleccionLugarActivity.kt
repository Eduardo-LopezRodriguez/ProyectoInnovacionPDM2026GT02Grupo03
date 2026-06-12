package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.mapa

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class MapaSeleccionLugarActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var txtCoordenadas: TextView
    private lateinit var btnUsarUbicacion: Button
    private lateinit var btnCancelar: Button

    private var marcador: Marker? = null
    private var latitudSeleccionada: Double? = null
    private var longitudSeleccionada: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_mapa_seleccion_lugar)

        inicializarVistas()
        configurarMapa()
        configurarEventos()
    }

    private fun inicializarVistas() {
        mapView = findViewById(R.id.mapViewSeleccionLugar)
        txtCoordenadas = findViewById(R.id.txtCoordenadasSeleccionadas)
        btnUsarUbicacion = findViewById(R.id.btnUsarUbicacion)
        btnCancelar = findViewById(R.id.btnCancelarMapa)
    }

    private fun configurarMapa() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val latInicial = intent.getDoubleExtra("latitud", 13.7189)
        val lonInicial = intent.getDoubleExtra("longitud", -89.2036)

        val puntoInicial = GeoPoint(latInicial, lonInicial)

        mapView.controller.setZoom(16.0)
        mapView.controller.setCenter(puntoInicial)

        seleccionarUbicacion(puntoInicial)

        val eventosMapa = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                seleccionarUbicacion(p)
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }

        mapView.overlays.add(MapEventsOverlay(eventosMapa))
    }

    private fun configurarEventos() {
        btnUsarUbicacion.setOnClickListener {
            val latitud = latitudSeleccionada
            val longitud = longitudSeleccionada

            if (latitud == null || longitud == null) {
                Toast.makeText(this, "Seleccione una ubicación en el mapa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultado = Intent()
            resultado.putExtra("latitud", latitud)
            resultado.putExtra("longitud", longitud)

            setResult(RESULT_OK, resultado)
            finish()
        }

        btnCancelar.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun seleccionarUbicacion(punto: GeoPoint) {
        latitudSeleccionada = punto.latitude
        longitudSeleccionada = punto.longitude

        txtCoordenadas.text = String.format(
            Locale.US,
            "Latitud: %.6f\nLongitud: %.6f",
            punto.latitude,
            punto.longitude
        )

        if (marcador == null) {
            marcador = Marker(mapView).apply {
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Ubicación seleccionada"
            }

            mapView.overlays.add(marcador)
        }

        marcador?.position = punto
        mapView.controller.setCenter(punto)
        mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }
}

package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.mapa

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database.AppDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ServicioEmergenciaEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ZonaRiesgoEntity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

class MapaSeguridadActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var txtEstadoMapa: TextView
    private lateinit var btnVolverMapa: Button
    private lateinit var btnCentrarMapa: Button
    private lateinit var btnRecargarMapa: Button

    private lateinit var db: AppDatabase
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var zonas: List<ZonaRiesgoEntity> = emptyList()
    private var servicios: List<ServicioEmergenciaEntity> = emptyList()
    private var idZonaAdvertida: Int? = null
    private var markerUsuario: Marker? = null

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 2001
        private const val LATITUD_INICIAL = 13.6929
        private const val LONGITUD_INICIAL = -89.2182
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_mapa_seguridad)

        db = AppDatabase.obtenerBaseDatos(this)

        inicializarVistas()
        configurarMapa()
        configurarEventos()
        cargarDatosMapa()
    }

    private fun inicializarVistas() {
        mapView = findViewById(R.id.mapViewSeguridad)
        txtEstadoMapa = findViewById(R.id.txtEstadoMapa)
        btnVolverMapa = findViewById(R.id.btnVolverMapa)
        btnCentrarMapa = findViewById(R.id.btnCentrarMapa)
        btnRecargarMapa = findViewById(R.id.btnRecargarMapa)
    }

    private fun configurarMapa() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val puntoInicial = GeoPoint(LATITUD_INICIAL, LONGITUD_INICIAL)
        mapView.controller.setZoom(14.0)
        mapView.controller.setCenter(puntoInicial)
    }

    private fun configurarEventos() {
        btnVolverMapa.setOnClickListener {
            finish()
        }

        btnCentrarMapa.setOnClickListener {
            solicitarUbicacionActual()
        }

        btnRecargarMapa.setOnClickListener {
            cargarDatosMapa()
        }
    }

    private fun cargarDatosMapa() {
        txtEstadoMapa.text = "Cargando zonas y servicios..."

        CoroutineScope(Dispatchers.IO).launch {
            val zonasLocales = db.zonaRiesgoDao().listarActivas()
            val serviciosLocales = db.servicioEmergenciaDao().listarActivos()

            withContext(Dispatchers.Main) {
                zonas = zonasLocales
                servicios = serviciosLocales

                dibujarMapa()

                txtEstadoMapa.text = "Zonas: ${zonas.size} | Servicios: ${servicios.size}"

                if (zonas.isEmpty() && servicios.isEmpty()) {
                    Toast.makeText(
                        this@MapaSeguridadActivity,
                        "No hay datos. Sincroniza servicios primero.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                solicitarUbicacionActual()
            }
        }
    }

    private fun dibujarMapa() {
        mapView.overlays.clear()
        markerUsuario = null

        zonas.forEach { zona ->
            dibujarZonaRiesgo(zona)
        }

        servicios.forEach { servicio ->
            dibujarServicio(servicio)
        }

        mapView.invalidate()
    }

    private fun dibujarZonaRiesgo(zona: ZonaRiesgoEntity) {
        val centro = GeoPoint(zona.latitud, zona.longitud)

        val circulo = Polygon()
        circulo.points = Polygon.pointsAsCircle(centro, zona.radioMetros.toDouble())
        circulo.setFillColor(obtenerColorRellenoZona(zona.nivelRiesgo))
        circulo.setStrokeColor(obtenerColorBordeZona(zona.nivelRiesgo))
        circulo.setStrokeWidth(3f)
        circulo.title = zona.nombre
        circulo.subDescription = "${zona.categoria} • ${zona.nivelRiesgo}"

        mapView.overlays.add(circulo)

        val marker = Marker(mapView)
        marker.position = centro
        marker.title = "Zona: ${zona.nombre}"
        marker.snippet = "${zona.categoria} • Riesgo ${zona.nivelRiesgo}"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_marker_zona))

        mapView.overlays.add(marker)
    }

    private fun dibujarServicio(servicio: ServicioEmergenciaEntity) {
        val punto = GeoPoint(servicio.latitud, servicio.longitud)

        val marker = Marker(mapView)
        marker.position = punto
        marker.title = "Servicio: ${servicio.nombre}"
        marker.snippet = "${servicio.tipoServicio} • ${servicio.telefono}"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_marker_servicio))

        mapView.overlays.add(marker)
    }

    private fun obtenerColorRellenoZona(nivel: String): Int {
        return when (nivel.uppercase()) {
            "ALTO" -> Color.argb(55, 239, 38, 38)
            "MEDIO" -> Color.argb(55, 245, 158, 11)
            "BAJO" -> Color.argb(45, 34, 168, 82)
            else -> Color.argb(40, 47, 98, 233)
        }
    }

    private fun obtenerColorBordeZona(nivel: String): Int {
        return when (nivel.uppercase()) {
            "ALTO" -> Color.rgb(239, 38, 38)
            "MEDIO" -> Color.rgb(245, 158, 11)
            "BAJO" -> Color.rgb(34, 168, 82)
            else -> Color.rgb(47, 98, 233)
        }
    }

    private fun solicitarUbicacionActual() {
        val permisoFine = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permisoFine != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }

        obtenerUbicacionActual()
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacionActual() {
        txtEstadoMapa.text = "Buscando ubicación actual..."

        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                procesarUbicacion(location)
            } else {
                obtenerUltimaUbicacionDisponible()
            }
        }.addOnFailureListener {
            obtenerUltimaUbicacionDisponible()
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUltimaUbicacionDisponible() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    procesarUbicacion(location)
                } else {
                    txtEstadoMapa.text = "Zonas: ${zonas.size} | Servicios: ${servicios.size} | Sin ubicación actual"
                    Toast.makeText(
                        this,
                        "No se pudo obtener ubicación. Activa GPS o envía ubicación desde el emulador.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener {
                txtEstadoMapa.text = "Zonas: ${zonas.size} | Servicios: ${servicios.size} | Error de ubicación"
                Toast.makeText(
                    this,
                    "Error al obtener ubicación actual.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun procesarUbicacion(location: Location) {
        mostrarUbicacionUsuario(location)
        verificarZonasCercanas(location)

        txtEstadoMapa.text =
            "Zonas: ${zonas.size} | Servicios: ${servicios.size} | Ubicación activa"
    }

    private fun mostrarUbicacionUsuario(location: Location) {
        markerUsuario?.let {
            mapView.overlays.remove(it)
        }

        val puntoUsuario = GeoPoint(location.latitude, location.longitude)

        val marker = Marker(mapView)
        marker.position = puntoUsuario
        marker.title = "Mi ubicación"
        marker.snippet = "Ubicación actual"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_marker_usuario))

        markerUsuario = marker

        mapView.overlays.add(marker)
        mapView.controller.animateTo(puntoUsuario)
        mapView.invalidate()
    }

    private fun verificarZonasCercanas(location: Location) {
        zonas.forEach { zona ->
            val distancia = FloatArray(1)

            Location.distanceBetween(
                location.latitude,
                location.longitude,
                zona.latitud,
                zona.longitud,
                distancia
            )

            val distanciaMetros = distancia[0]

            if (distanciaMetros <= zona.radioMetros) {
                if (idZonaAdvertida != zona.idZona) {
                    idZonaAdvertida = zona.idZona
                    mostrarAdvertenciaZona(zona, distanciaMetros)
                }
                return
            }
        }
    }

    private fun mostrarAdvertenciaZona(zona: ZonaRiesgoEntity, distanciaMetros: Float) {
        val mensaje = """
            Has ingresado a una zona marcada como riesgo.

            Zona: ${zona.nombre}
            Categoría: ${zona.categoria}
            Nivel: ${zona.nivelRiesgo}
            Distancia aproximada: ${distanciaMetros.toInt()} metros
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Advertencia de zona de riesgo")
            .setMessage(mensaje)
            .setPositiveButton("Entendido", null)
            .setNegativeButton("Ver servicios") { _, _ ->
                Toast.makeText(
                    this,
                    "Puedes revisar servicios de emergencia desde el dashboard.",
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActual()
            } else {
                Toast.makeText(
                    this,
                    "Permiso de ubicación denegado.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.alertas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database.AppDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.SosLocalRepository
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.util.SesionUsuario
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.net.Uri

class SosActivity : AppCompatActivity() {

    private lateinit var txtEstadoSos: TextView
    private lateinit var txtCoordenadasSos: TextView
    private lateinit var btnActivarSos: Button
    private lateinit var btnHistorialSos: Button
    private lateinit var btnVolverSos: Button

    private lateinit var repository: SosLocalRepository

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var ubicacionActual: Location? = null

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 3001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos)

        val db = AppDatabase.obtenerBaseDatos(this)
        repository = SosLocalRepository(db)

        inicializarVistas()
        configurarEventos()
        solicitarUbicacion()
    }

    private fun inicializarVistas() {
        txtEstadoSos = findViewById(R.id.txtEstadoSos)
        txtCoordenadasSos = findViewById(R.id.txtCoordenadasSos)
        btnActivarSos = findViewById(R.id.btnActivarSos)
        btnHistorialSos = findViewById(R.id.btnHistorialSos)
        btnVolverSos = findViewById(R.id.btnVolverSos)
    }

    private fun configurarEventos() {
        btnVolverSos.setOnClickListener {
            finish()
        }

        btnHistorialSos.setOnClickListener {
            startActivity(Intent(this, HistorialAlertasActivity::class.java))
        }

        btnActivarSos.setOnClickListener {
            confirmarActivacionSos()
        }
    }

    private fun solicitarUbicacion() {
        val permiso = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permiso != PackageManager.PERMISSION_GRANTED) {
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
        txtEstadoSos.text = "Buscando ubicación actual..."

        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                ubicacionActual = location
                mostrarUbicacion(location)
            } else {
                obtenerUltimaUbicacion()
            }
        }.addOnFailureListener {
            obtenerUltimaUbicacion()
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUltimaUbicacion() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    ubicacionActual = location
                    mostrarUbicacion(location)
                } else {
                    txtEstadoSos.text = "No se pudo obtener la ubicación."
                    txtCoordenadasSos.text = "Activa GPS o configura ubicación en el emulador."
                }
            }
            .addOnFailureListener {
                txtEstadoSos.text = "Error al obtener ubicación."
            }
    }

    private fun mostrarUbicacion(location: Location) {
        txtEstadoSos.text = "Ubicación lista para alerta."
        txtCoordenadasSos.text = "Latitud: ${location.latitude}\nLongitud: ${location.longitude}"
    }

    private fun confirmarActivacionSos() {
        val location = ubicacionActual

        if (location == null) {
            Toast.makeText(
                this,
                "Primero se necesita obtener la ubicación actual.",
                Toast.LENGTH_LONG
            ).show()

            solicitarUbicacion()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Activar alerta SOS")
            .setMessage("Se registrará una alerta SOS con tu ubicación actual y tus contactos activos quedarán asociados al aviso.")
            .setPositiveButton("Activar SOS") { _, _ ->
                registrarSos(location)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun registrarSos(location: Location) {
        val idUsuario = SesionUsuario.obtenerIdUsuario(this)

        if (idUsuario <= 0) {
            Toast.makeText(this, "No se encontró sesión activa.", Toast.LENGTH_SHORT).show()
            return
        }

        txtEstadoSos.text = "Registrando alerta SOS..."

        CoroutineScope(Dispatchers.IO).launch {
            val fechaHora = obtenerFechaHoraActual()

            val resultado = repository.registrarAlertaSos(
                idUsuario = idUsuario,
                latitud = location.latitude,
                longitud = location.longitude,
                fechaHora = fechaHora
            )

            val contactos = repository.obtenerContactosActivosParaEnvio(idUsuario)

            val mensajeSos = crearMensajeSos(
                latitud = location.latitude,
                longitud = location.longitude,
                fechaHora = fechaHora
            )

            withContext(Dispatchers.Main) {
                txtEstadoSos.text = resultado.mensaje

                Toast.makeText(
                    this@SosActivity,
                    resultado.mensaje,
                    Toast.LENGTH_LONG
                ).show()

                if (resultado.exito) {
                    if (contactos.isEmpty()) {
                        Toast.makeText(
                            this@SosActivity,
                            "No hay contactos activos para enviar la alerta.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        mostrarSelectorContactoSos(contactos, mensajeSos)
                    }
                }
            }
        }
    }

    private fun obtenerFechaHoraActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formato.format(Date())
    }

    private fun crearMensajeSos(
        latitud: Double,
        longitud: Double,
        fechaHora: String
    ): String {
        val linkMapa = "https://www.google.com/maps?q=$latitud,$longitud"

        return """
        ALERTA SOS

        Necesito ayuda. Esta es mi ubicación actual:

        $linkMapa

        Fecha y hora: $fechaHora
    """.trimIndent()
    }

    private fun abrirSmsSos(
        telefono: String,
        mensaje: String
    ) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$telefono")
            putExtra("sms_body", mensaje)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "No se encontró una app de mensajes para enviar la alerta.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun mostrarSelectorContactoSos(
        contactos: List<com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.ContactoEnvioSos>,
        mensaje: String
    ) {
        val opciones = contactos.map { contacto ->
            "${contacto.nombre} - ${contacto.telefono}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Enviar alerta SOS")
            .setItems(opciones) { _, which ->
                val contactoSeleccionado = contactos[which]

                abrirSmsSos(
                    telefono = contactoSeleccionado.telefono,
                    mensaje = mensaje
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
                txtEstadoSos.text = "Permiso de ubicación denegado."
            }
        }
    }
}

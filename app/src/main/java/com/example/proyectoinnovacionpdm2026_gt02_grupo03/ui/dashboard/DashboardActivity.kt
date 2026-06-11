package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.login.LoginActivity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.util.SesionUsuario

class DashboardActivity : AppCompatActivity() {

    private lateinit var txtBienvenida: TextView
    private lateinit var btnContactos: Button
    private lateinit var btnLugares: Button
    private lateinit var btnMapa: Button
    private lateinit var btnSos: Button
    private lateinit var btnServicios: Button
    private lateinit var btnHistorial: Button
    private lateinit var btnCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!SesionUsuario.estaLogueado(this)) {
            abrirLogin()
            return
        }

        setContentView(R.layout.activity_dashboard)

        inicializarVistas()
        cargarDatosUsuario()
        configurarEventos()
    }

    private fun inicializarVistas() {
        txtBienvenida = findViewById(R.id.txtBienvenida)
        btnContactos = findViewById(R.id.btnContactos)
        btnLugares = findViewById(R.id.btnLugares)
        btnMapa = findViewById(R.id.btnMapa)
        btnSos = findViewById(R.id.btnSos)
        btnServicios = findViewById(R.id.btnServicios)
        btnHistorial = findViewById(R.id.btnHistorial)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
    }

    private fun cargarDatosUsuario() {
        val nombre = SesionUsuario.obtenerNombre(this)

        txtBienvenida.text = if (nombre.isNotBlank()) {
            "Hola, $nombre"
        } else {
            "Hola"
        }
    }

    private fun configurarEventos() {
        btnContactos.setOnClickListener {
            startActivity(Intent(this, com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.contactos.ContactosActivity::class.java))
        }

        btnLugares.setOnClickListener {
            mostrarPendiente("Lugares importantes")
        }

        btnMapa.setOnClickListener {
            mostrarPendiente("Mapa de seguridad")
        }

        btnSos.setOnClickListener {
            mostrarPendiente("Alerta SOS")
        }

        btnServicios.setOnClickListener {
            mostrarPendiente("Servicios de emergencia")
        }

        btnHistorial.setOnClickListener {
            mostrarPendiente("Historial de alertas")
        }

        btnCerrarSesion.setOnClickListener {
            SesionUsuario.cerrarSesion(this)
            abrirLogin()
        }
    }

    private fun mostrarPendiente(nombreModulo: String) {
        Toast.makeText(this, "$nombreModulo pendiente de implementar", Toast.LENGTH_SHORT).show()
    }

    private fun abrirLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}


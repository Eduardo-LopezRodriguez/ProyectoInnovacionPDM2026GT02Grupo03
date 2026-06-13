package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.alertas

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database.AppDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.SosLocalRepository
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.util.SesionUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialAlertasActivity : AppCompatActivity() {

    private lateinit var recyclerHistorialAlertas: RecyclerView
    private lateinit var txtSinAlertas: TextView
    private lateinit var txtEstadoHistorial: TextView
    private lateinit var btnVolverHistorial: Button
    private lateinit var btnVaciarHistorial: Button

    private lateinit var adapter: HistorialAlertasAdapter
    private lateinit var repository: SosLocalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_alertas)

        val db = AppDatabase.obtenerBaseDatos(this)
        repository = SosLocalRepository(db)

        inicializarVistas()
        configurarRecycler()
        configurarEventos()
        cargarHistorial()
    }

    private fun inicializarVistas() {
        recyclerHistorialAlertas = findViewById(R.id.recyclerHistorialAlertas)
        txtSinAlertas = findViewById(R.id.txtSinAlertas)
        txtEstadoHistorial = findViewById(R.id.txtEstadoHistorial)
        btnVolverHistorial = findViewById(R.id.btnVolverHistorial)
        btnVaciarHistorial = findViewById(R.id.btnVaciarHistorial)
    }

    private fun configurarRecycler() {
        adapter = HistorialAlertasAdapter(emptyList())
        recyclerHistorialAlertas.layoutManager = LinearLayoutManager(this)
        recyclerHistorialAlertas.adapter = adapter
    }

    private fun configurarEventos() {
        btnVolverHistorial.setOnClickListener {
            finish()
        }

        btnVaciarHistorial.setOnClickListener {
            confirmarVaciarHistorial()
        }
    }

    private fun cargarHistorial() {
        val idUsuario = SesionUsuario.obtenerIdUsuario(this)

        if (idUsuario <= 0) {
            txtEstadoHistorial.text = "No se encontró sesión activa."
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val alertas = repository.obtenerHistorialAlertas(idUsuario)

            withContext(Dispatchers.Main) {
                adapter.actualizarLista(alertas)

                txtEstadoHistorial.text = "Alertas registradas: ${alertas.size}"

                if (alertas.isEmpty()) {
                    txtSinAlertas.visibility = View.VISIBLE
                    recyclerHistorialAlertas.visibility = View.GONE
                } else {
                    txtSinAlertas.visibility = View.GONE
                    recyclerHistorialAlertas.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun confirmarVaciarHistorial() {
        AlertDialog.Builder(this)
            .setTitle("Vaciar historial")
            .setMessage("Se eliminarán todas las alertas registradas para este usuario.")
            .setPositiveButton("Vaciar") { _, _ ->
                vaciarHistorial()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun vaciarHistorial() {
        val idUsuario = SesionUsuario.obtenerIdUsuario(this)

        if (idUsuario <= 0) {
            Toast.makeText(this, "No se encontró sesión activa.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = repository.vaciarHistorialAlertas(idUsuario)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@HistorialAlertasActivity,
                    resultado.mensaje,
                    Toast.LENGTH_LONG
                ).show()

                cargarHistorial()
            }
        }
    }
}

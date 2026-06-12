package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.servicios

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database.AppDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ServicioEmergenciaEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.api.RetrofitClient
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.CatalogoSyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiciosEmergenciaActivity : AppCompatActivity() {

    private lateinit var recyclerServicios: RecyclerView
    private lateinit var txtSinServicios: TextView
    private lateinit var txtEstadoServicios: TextView
    private lateinit var btnVolverServicios: Button
    private lateinit var btnSincronizarServicios: Button

    private lateinit var adapter: ServicioEmergenciaAdapter
    private lateinit var db: AppDatabase
    private lateinit var syncRepository: CatalogoSyncRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicios_emergencia)

        db = AppDatabase.obtenerBaseDatos(this)

        syncRepository = CatalogoSyncRepository(
            apiService = RetrofitClient.apiService,
            zonaRiesgoDao = db.zonaRiesgoDao(),
            servicioEmergenciaDao = db.servicioEmergenciaDao()
        )

        recyclerServicios = findViewById(R.id.recyclerServicios)
        txtSinServicios = findViewById(R.id.txtSinServicios)
        txtEstadoServicios = findViewById(R.id.txtEstadoServicios)
        btnVolverServicios = findViewById(R.id.btnVolverServicios)
        btnSincronizarServicios = findViewById(R.id.btnSincronizarServicios)

        configurarRecycler()

        btnVolverServicios.setOnClickListener {
            finish()
        }

        btnSincronizarServicios.setOnClickListener {
            sincronizarServicios()
        }

        cargarServicios()
    }

    private fun configurarRecycler() {
        adapter = ServicioEmergenciaAdapter(
            servicios = emptyList(),
            onLlamar = { servicio ->
                llamarServicio(servicio)
            },
            onVerMapa = { servicio ->
                Toast.makeText(
                    this,
                    "Mapa de servicios pendiente: ${servicio.nombre}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        recyclerServicios.layoutManager = LinearLayoutManager(this)
        recyclerServicios.adapter = adapter
    }

    private fun cargarServicios() {
        CoroutineScope(Dispatchers.IO).launch {
            val servicios = db.servicioEmergenciaDao().listarActivos()

            withContext(Dispatchers.Main) {
                adapter.actualizarLista(servicios)

                if (servicios.isEmpty()) {
                    txtSinServicios.visibility = View.VISIBLE
                    recyclerServicios.visibility = View.GONE
                    txtEstadoServicios.text = "No hay servicios guardados. Presiona Sincronizar."
                } else {
                    txtSinServicios.visibility = View.GONE
                    recyclerServicios.visibility = View.VISIBLE
                    txtEstadoServicios.text = "Servicios cargados: ${servicios.size}"
                }
            }
        }
    }

    private fun sincronizarServicios() {
        txtEstadoServicios.text = "Sincronizando servicios..."

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = syncRepository.sincronizarCatalogos()

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ServiciosEmergenciaActivity,
                    resultado.mensaje,
                    Toast.LENGTH_SHORT
                ).show()

                txtEstadoServicios.text = if (resultado.exito) {
                    "Servicios sincronizados: ${resultado.serviciosSincronizados}"
                } else {
                    "No se pudo sincronizar. Revisa internet o la API."
                }

                cargarServicios()
            }
        }
    }

    private fun llamarServicio(servicio: ServicioEmergenciaEntity) {
        val telefonoLimpio = servicio.telefono.trim()

        if (telefonoLimpio.isEmpty()) {
            Toast.makeText(this, "Este servicio no tiene teléfono.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$telefonoLimpio")
        }

        startActivity(intent)
    }
}

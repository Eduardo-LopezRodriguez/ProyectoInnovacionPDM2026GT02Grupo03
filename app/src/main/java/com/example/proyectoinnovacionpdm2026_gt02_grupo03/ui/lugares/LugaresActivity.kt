package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.lugares

import android.content.Intent
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
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.LugarImportanteEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.LugarRepository
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.util.SesionUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LugaresActivity : AppCompatActivity() {

    private lateinit var lugarRepository: LugarRepository
    private lateinit var adapter: LugarAdapter

    private lateinit var recyclerLugares: RecyclerView
    private lateinit var txtSinLugares: TextView
    private lateinit var btnAgregarLugar: Button
    private lateinit var btnVolver: Button

    private var idUsuario: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lugares)

        idUsuario = SesionUsuario.obtenerIdUsuario(this)

        val database = AppDatabase.obtenerBaseDatos(applicationContext)
        lugarRepository = LugarRepository(database.lugarImportanteDao())

        inicializarVistas()
        configurarRecycler()
        configurarEventos()
    }

    override fun onResume() {
        super.onResume()
        cargarLugares()
    }

    private fun inicializarVistas() {
        recyclerLugares = findViewById(R.id.recyclerLugares)
        txtSinLugares = findViewById(R.id.txtSinLugares)
        btnAgregarLugar = findViewById(R.id.btnAgregarLugar)
        btnVolver = findViewById(R.id.btnVolverLugares)
    }

    private fun configurarRecycler() {
        adapter = LugarAdapter(
            lugares = emptyList(),
            onEditar = { lugar -> abrirFormulario(lugar.idLugar) },
            onEliminar = { lugar -> confirmarEliminar(lugar) }
        )

        recyclerLugares.layoutManager = LinearLayoutManager(this)
        recyclerLugares.adapter = adapter
    }

    private fun configurarEventos() {
        btnAgregarLugar.setOnClickListener {
            abrirFormulario(null)
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarLugares() {
        if (idUsuario == 0) {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val lugares = lugarRepository.listarActivosPorUsuario(idUsuario)

            runOnUiThread {
                adapter.actualizarLista(lugares)
                txtSinLugares.visibility = if (lugares.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun abrirFormulario(idLugar: Int?) {
        val intent = Intent(this, FormLugarActivity::class.java)

        if (idLugar != null) {
            intent.putExtra("idLugar", idLugar)
        }

        startActivity(intent)
    }

    private fun confirmarEliminar(lugar: LugarImportanteEntity) {
        AlertDialog.Builder(this)
            .setTitle("Desactivar lugar")
            .setMessage("¿Deseas desactivar ${lugar.nombre}?")
            .setPositiveButton("Sí") { _, _ ->
                desactivarLugar(lugar.idLugar)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun desactivarLugar(idLugar: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            lugarRepository.desactivar(idLugar)

            runOnUiThread {
                Toast.makeText(this@LugaresActivity, "Lugar desactivado", Toast.LENGTH_SHORT).show()
                cargarLugares()
            }
        }
    }
}

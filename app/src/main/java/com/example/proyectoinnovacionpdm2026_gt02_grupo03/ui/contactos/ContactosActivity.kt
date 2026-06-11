package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.contactos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database.AppDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ContactoConfianzaEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.ContactoRepository
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.util.SesionUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactosActivity : AppCompatActivity() {

    private lateinit var contactoRepository: ContactoRepository
    private lateinit var adapter: ContactoAdapter

    private lateinit var recyclerContactos: RecyclerView
    private lateinit var txtSinContactos: TextView
    private lateinit var btnAgregarContacto: Button
    private lateinit var btnVolver: Button

    private var idUsuario: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactos)

        idUsuario = SesionUsuario.obtenerIdUsuario(this)

        val database = AppDatabase.obtenerBaseDatos(applicationContext)
        contactoRepository = ContactoRepository(database.contactoConfianzaDao())

        inicializarVistas()
        configurarRecycler()
        configurarEventos()
    }

    override fun onResume() {
        super.onResume()
        cargarContactos()
    }

    private fun inicializarVistas() {
        recyclerContactos = findViewById(R.id.recyclerContactos)
        txtSinContactos = findViewById(R.id.txtSinContactos)
        btnAgregarContacto = findViewById(R.id.btnAgregarContacto)
        btnVolver = findViewById(R.id.btnVolverContactos)
    }

    private fun configurarRecycler() {
        adapter = ContactoAdapter(
            contactos = emptyList(),
            onEditar = { contacto -> abrirFormulario(contacto.idContacto) },
            onEliminar = { contacto -> confirmarEliminar(contacto) }
        )

        recyclerContactos.layoutManager = LinearLayoutManager(this)
        recyclerContactos.adapter = adapter
    }

    private fun configurarEventos() {
        btnAgregarContacto.setOnClickListener {
            abrirFormulario(null)
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarContactos() {
        if (idUsuario == 0) {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val contactos = contactoRepository.listarActivosPorUsuario(idUsuario)

            runOnUiThread {
                adapter.actualizarLista(contactos)

                txtSinContactos.visibility = if (contactos.isEmpty()) {
                    TextView.VISIBLE
                } else {
                    TextView.GONE
                }
            }
        }
    }

    private fun abrirFormulario(idContacto: Int?) {
        val intent = Intent(this, FormContactoActivity::class.java)

        if (idContacto != null) {
            intent.putExtra("idContacto", idContacto)
        }

        startActivity(intent)
    }

    private fun confirmarEliminar(contacto: ContactoConfianzaEntity) {
        AlertDialog.Builder(this)
            .setTitle("Desactivar contacto")
            .setMessage("¿Deseas desactivar a ${contacto.nombre}?")
            .setPositiveButton("Sí") { _, _ ->
                desactivarContacto(contacto.idContacto)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun desactivarContacto(idContacto: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            contactoRepository.desactivar(idContacto)

            runOnUiThread {
                Toast.makeText(this@ContactosActivity, "Contacto desactivado", Toast.LENGTH_SHORT).show()
                cargarContactos()
            }
        }
    }
}

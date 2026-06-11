package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.contactos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database.AppDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ContactoConfianzaEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.ContactoRepository
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.util.SesionUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.util.Validador

class FormContactoActivity : AppCompatActivity() {

    private lateinit var contactoRepository: ContactoRepository

    private lateinit var edtNombre: EditText
    private lateinit var edtTelefono: EditText
    private lateinit var edtCorreo: EditText
    private lateinit var edtParentesco: EditText
    private lateinit var edtPrioridad: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private var idUsuario: Int = 0
    private var idContacto: Int = 0
    private var contactoActual: ContactoConfianzaEntity? = null
    private var modoEdicion: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_contacto)

        idUsuario = SesionUsuario.obtenerIdUsuario(this)
        idContacto = intent.getIntExtra("idContacto", 0)
        modoEdicion = idContacto > 0

        val database = AppDatabase.obtenerBaseDatos(applicationContext)
        contactoRepository = ContactoRepository(database.contactoConfianzaDao())

        inicializarVistas()
        configurarEventos()

        if (modoEdicion) {
            cargarContacto()
        }
    }

    private fun inicializarVistas() {
        edtNombre = findViewById(R.id.edtNombreContacto)
        edtTelefono = findViewById(R.id.edtTelefonoContacto)
        edtCorreo = findViewById(R.id.edtCorreoContacto)
        edtParentesco = findViewById(R.id.edtParentescoContacto)
        edtPrioridad = findViewById(R.id.edtPrioridadContacto)
        btnGuardar = findViewById(R.id.btnGuardarContacto)
        btnCancelar = findViewById(R.id.btnCancelarContacto)

        btnGuardar.text = if (modoEdicion) "Actualizar contacto" else "Guardar contacto"
    }

    private fun configurarEventos() {
        btnGuardar.setOnClickListener {
            guardarContacto()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun cargarContacto() {
        CoroutineScope(Dispatchers.IO).launch {
            val contacto = contactoRepository.obtenerPorId(idContacto)

            runOnUiThread {
                if (contacto == null) {
                    Toast.makeText(this@FormContactoActivity, "Contacto no encontrado", Toast.LENGTH_SHORT).show()
                    finish()
                    return@runOnUiThread
                }

                contactoActual = contacto
                edtNombre.setText(contacto.nombre)
                edtTelefono.setText(contacto.telefono)
                edtCorreo.setText(contacto.correo)
                edtParentesco.setText(contacto.parentesco)
                edtPrioridad.setText(contacto.prioridad.toString())
            }
        }
    }

    private fun guardarContacto() {
        val nombre = edtNombre.text.toString().trim()
        val telefono = edtTelefono.text.toString().trim()
        val telefonoLimpio = Validador.limpiarTelefono(telefono)
        val correo = edtCorreo.text.toString().trim()
        val parentesco = edtParentesco.text.toString().trim()
        val prioridad = edtPrioridad.text.toString().toIntOrNull() ?: 1

        if (idUsuario == 0) {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        if (nombre.isBlank()) {
            Toast.makeText(this, "Ingrese el nombre del contacto", Toast.LENGTH_SHORT).show()
            return
        }

        if (telefono.isBlank()) {
            Toast.makeText(this, "Ingrese el teléfono del contacto", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Validador.telefonoSvValido(telefono)) {
            Toast.makeText(this, "El teléfono debe tener exactamente 8 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        if (correo.isNotBlank() && !Validador.correoBasicoValido(correo)) {
            Toast.makeText(this, "Ingrese un correo válido o deje el campo vacío", Toast.LENGTH_SHORT).show()
            return
        }

        if (parentesco.isBlank()) {
            Toast.makeText(this, "Ingrese el parentesco del contacto", Toast.LENGTH_SHORT).show()
            return
        }

        if (prioridad <= 0) {
            Toast.makeText(this, "La prioridad debe ser mayor a 0", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (modoEdicion && contactoActual != null) {
                val actualizado = contactoActual!!.copy(
                    nombre = nombre,
                    telefono = telefonoLimpio,
                    correo = correo,
                    parentesco = parentesco,
                    prioridad = prioridad,
                    activo = true
                )

                contactoRepository.actualizar(actualizado)
            } else {
                val nuevoContacto = ContactoConfianzaEntity(
                    idUsuario = idUsuario,
                    nombre = nombre,
                    telefono = telefonoLimpio,
                    correo = correo,
                    parentesco = parentesco,
                    prioridad = prioridad,
                    activo = true
                )

                contactoRepository.insertar(nuevoContacto)
            }

            runOnUiThread {
                val mensaje = if (modoEdicion) "Contacto actualizado" else "Contacto guardado"
                Toast.makeText(this@FormContactoActivity, mensaje, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

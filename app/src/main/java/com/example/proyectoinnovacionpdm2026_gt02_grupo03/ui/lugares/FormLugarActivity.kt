package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.lugares

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database.AppDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.LugarImportanteEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.LugarRepository
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.mapa.MapaSeleccionLugarActivity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.util.SesionUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class FormLugarActivity : AppCompatActivity() {

    private lateinit var lugarRepository: LugarRepository

    private lateinit var edtNombre: EditText
    private lateinit var edtTipo: EditText
    private lateinit var edtDireccion: EditText
    private lateinit var edtDepartamento: EditText
    private lateinit var edtMunicipio: EditText
    private lateinit var txtCoordenadas: TextView
    private lateinit var btnSeleccionarUbicacion: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private var idUsuario: Int = 0
    private var idLugar: Int = 0
    private var lugarActual: LugarImportanteEntity? = null
    private var modoEdicion: Boolean = false

    private var latitudSeleccionada: Double? = null
    private var longitudSeleccionada: Double? = null

    private val seleccionarUbicacionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == Activity.RESULT_OK) {
                val data = resultado.data
                val latitud = data?.getDoubleExtra("latitud", 0.0) ?: 0.0
                val longitud = data?.getDoubleExtra("longitud", 0.0) ?: 0.0

                latitudSeleccionada = latitud
                longitudSeleccionada = longitud
                mostrarCoordenadas()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_lugar)

        idUsuario = SesionUsuario.obtenerIdUsuario(this)
        idLugar = intent.getIntExtra("idLugar", 0)
        modoEdicion = idLugar > 0

        val database = AppDatabase.obtenerBaseDatos(applicationContext)
        lugarRepository = LugarRepository(database.lugarImportanteDao())

        inicializarVistas()
        configurarEventos()

        if (modoEdicion) {
            cargarLugar()
        }
    }

    private fun inicializarVistas() {
        edtNombre = findViewById(R.id.edtNombreLugar)
        edtTipo = findViewById(R.id.edtTipoLugar)
        edtDireccion = findViewById(R.id.edtDireccionLugar)
        edtDepartamento = findViewById(R.id.edtDepartamentoLugar)
        edtMunicipio = findViewById(R.id.edtMunicipioLugar)
        txtCoordenadas = findViewById(R.id.txtCoordenadasLugar)
        btnSeleccionarUbicacion = findViewById(R.id.btnSeleccionarUbicacionLugar)
        btnGuardar = findViewById(R.id.btnGuardarLugar)
        btnCancelar = findViewById(R.id.btnCancelarLugar)

        btnGuardar.text = if (modoEdicion) "Actualizar lugar" else "Guardar lugar"
    }

    private fun configurarEventos() {
        btnSeleccionarUbicacion.setOnClickListener {
            abrirMapaSeleccion()
        }

        btnGuardar.setOnClickListener {
            guardarLugar()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun cargarLugar() {
        CoroutineScope(Dispatchers.IO).launch {
            val lugar = lugarRepository.obtenerPorId(idLugar)

            runOnUiThread {
                if (lugar == null) {
                    Toast.makeText(this@FormLugarActivity, "Lugar no encontrado", Toast.LENGTH_SHORT).show()
                    finish()
                    return@runOnUiThread
                }

                lugarActual = lugar
                edtNombre.setText(lugar.nombre)
                edtTipo.setText(lugar.tipoLugar)
                edtDireccion.setText(lugar.direccion)
                edtDepartamento.setText(lugar.departamento)
                edtMunicipio.setText(lugar.municipio)

                latitudSeleccionada = lugar.latitud
                longitudSeleccionada = lugar.longitud
                mostrarCoordenadas()
            }
        }
    }

    private fun abrirMapaSeleccion() {
        val intent = Intent(this, MapaSeleccionLugarActivity::class.java)

        latitudSeleccionada?.let {
            intent.putExtra("latitud", it)
        }

        longitudSeleccionada?.let {
            intent.putExtra("longitud", it)
        }

        seleccionarUbicacionLauncher.launch(intent)
    }

    private fun mostrarCoordenadas() {
        val latitud = latitudSeleccionada
        val longitud = longitudSeleccionada

        if (latitud == null || longitud == null) {
            txtCoordenadas.text = "Ubicación no seleccionada"
            return
        }

        txtCoordenadas.text = String.format(
            Locale.US,
            "Ubicación seleccionada:\nLatitud: %.6f\nLongitud: %.6f",
            latitud,
            longitud
        )
    }

    private fun guardarLugar() {
        val nombre = edtNombre.text.toString().trim()
        val tipoLugar = edtTipo.text.toString().trim()
        val direccion = edtDireccion.text.toString().trim()
        val departamento = edtDepartamento.text.toString().trim()
        val municipio = edtMunicipio.text.toString().trim()
        val latitud = latitudSeleccionada
        val longitud = longitudSeleccionada

        if (idUsuario == 0) {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        if (nombre.isBlank()) {
            Toast.makeText(this, "Ingrese el nombre del lugar", Toast.LENGTH_SHORT).show()
            return
        }

        if (tipoLugar.isBlank()) {
            Toast.makeText(this, "Ingrese el tipo de lugar", Toast.LENGTH_SHORT).show()
            return
        }

        if (direccion.isBlank()) {
            Toast.makeText(this, "Ingrese la dirección", Toast.LENGTH_SHORT).show()
            return
        }

        if (departamento.isBlank()) {
            Toast.makeText(this, "Ingrese el departamento", Toast.LENGTH_SHORT).show()
            return
        }

        if (municipio.isBlank()) {
            Toast.makeText(this, "Ingrese el municipio", Toast.LENGTH_SHORT).show()
            return
        }

        if (latitud == null || longitud == null) {
            Toast.makeText(this, "Seleccione una ubicación en el mapa", Toast.LENGTH_SHORT).show()
            return
        }

        if (latitud < -90.0 || latitud > 90.0) {
            Toast.makeText(this, "La latitud seleccionada no es válida", Toast.LENGTH_SHORT).show()
            return
        }

        if (longitud < -180.0 || longitud > 180.0) {
            Toast.makeText(this, "La longitud seleccionada no es válida", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (modoEdicion && lugarActual != null) {
                val actualizado = lugarActual!!.copy(
                    nombre = nombre,
                    tipoLugar = tipoLugar,
                    direccion = direccion,
                    departamento = departamento,
                    municipio = municipio,
                    latitud = latitud,
                    longitud = longitud,
                    activo = true
                )

                lugarRepository.actualizar(actualizado)
            } else {
                val nuevoLugar = LugarImportanteEntity(
                    idUsuario = idUsuario,
                    nombre = nombre,
                    tipoLugar = tipoLugar,
                    direccion = direccion,
                    departamento = departamento,
                    municipio = municipio,
                    latitud = latitud,
                    longitud = longitud,
                    activo = true
                )

                lugarRepository.insertar(nuevoLugar)
            }

            runOnUiThread {
                val mensaje = if (modoEdicion) "Lugar actualizado" else "Lugar guardado"
                Toast.makeText(this@FormLugarActivity, mensaje, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

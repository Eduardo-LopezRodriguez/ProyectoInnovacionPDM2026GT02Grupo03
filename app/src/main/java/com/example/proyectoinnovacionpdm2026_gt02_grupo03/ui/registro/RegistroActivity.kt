package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.registro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database.AppDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.UsuarioEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.UsuarioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistroActivity : AppCompatActivity() {

    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val database = AppDatabase.obtenerBaseDatos(applicationContext)
        usuarioRepository = UsuarioRepository(database.usuarioDao())

        val edtNombre = findViewById<EditText>(R.id.edtNombre)
        val edtCorreo = findViewById<EditText>(R.id.edtCorreoRegistro)
        val edtTelefono = findViewById<EditText>(R.id.edtTelefono)
        val edtPassword = findViewById<EditText>(R.id.edtPasswordRegistro)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val txtVolverLogin = findViewById<TextView>(R.id.txtVolverLogin)

        btnRegistrarse.setOnClickListener {
            val nombre = edtNombre.text.toString().trim()
            val correo = edtCorreo.text.toString().trim()
            val telefono = edtTelefono.text.toString().trim()
            val password = edtPassword.text.toString()

            if (nombre.isBlank() || correo.isBlank() || telefono.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!correo.contains("@")) {
                Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 4) {
                Toast.makeText(this, "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarUsuario(nombre, correo, telefono, password)
        }

        txtVolverLogin.setOnClickListener {
            finish()
        }
    }

    private fun registrarUsuario(nombre: String, correo: String, telefono: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val existe = usuarioRepository.correoExiste(correo)

            if (existe) {
                runOnUiThread {
                    Toast.makeText(this@RegistroActivity, "Ese correo ya está registrado", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            usuarioRepository.registrar(
                UsuarioEntity(
                    nombre = nombre,
                    correo = correo,
                    telefono = telefono,
                    password = password,
                    estado = true
                )
            )

            runOnUiThread {
                Toast.makeText(this@RegistroActivity, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

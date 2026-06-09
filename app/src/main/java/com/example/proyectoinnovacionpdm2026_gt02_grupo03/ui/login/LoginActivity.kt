package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.MainActivity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database.AppDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.UsuarioRepository
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.registro.RegistroActivity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.util.SesionUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SesionUsuario.estaLogueado(this)) {
            abrirPantallaPrincipal()
            return
        }

        setContentView(R.layout.activity_login)

        val database = AppDatabase.obtenerBaseDatos(applicationContext)
        usuarioRepository = UsuarioRepository(database.usuarioDao())

        val edtCorreo = findViewById<EditText>(R.id.edtCorreo)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val txtCrearCuenta = findViewById<TextView>(R.id.txtCrearCuenta)

        btnIngresar.setOnClickListener {
            val correo = edtCorreo.text.toString().trim()
            val password = edtPassword.text.toString()

            if (correo.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            iniciarSesion(correo, password)
        }

        txtCrearCuenta.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun iniciarSesion(correo: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val usuario = usuarioRepository.iniciarSesion(correo, password)

            runOnUiThread {
                if (usuario != null) {
                    SesionUsuario.guardarSesion(
                        context = this@LoginActivity,
                        idUsuario = usuario.idUsuario,
                        nombre = usuario.nombre,
                        correo = usuario.correo
                    )

                    Toast.makeText(this@LoginActivity, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()
                    abrirPantallaPrincipal()
                } else {
                    Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun abrirPantallaPrincipal() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

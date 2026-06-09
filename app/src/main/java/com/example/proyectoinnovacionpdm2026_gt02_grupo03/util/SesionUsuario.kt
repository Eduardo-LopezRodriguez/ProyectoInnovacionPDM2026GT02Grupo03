package com.example.proyectoinnovacionpdm2026_gt02_grupo03.util

import android.content.Context

object SesionUsuario {

    private const val PREF_NAME = "sesion_usuario"
    private const val KEY_ID_USUARIO = "id_usuario"
    private const val KEY_NOMBRE = "nombre"
    private const val KEY_CORREO = "correo"
    private const val KEY_LOGUEADO = "logueado"

    fun guardarSesion(context: Context, idUsuario: Int, nombre: String, correo: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_ID_USUARIO, idUsuario)
            .putString(KEY_NOMBRE, nombre)
            .putString(KEY_CORREO, correo)
            .putBoolean(KEY_LOGUEADO, true)
            .apply()
    }

    fun cerrarSesion(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    fun estaLogueado(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LOGUEADO, false)
    }

    fun obtenerIdUsuario(context: Context): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_ID_USUARIO, 0)
    }

    fun obtenerNombre(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_NOMBRE, "") ?: ""
    }

    fun obtenerCorreo(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CORREO, "") ?: ""
    }
}

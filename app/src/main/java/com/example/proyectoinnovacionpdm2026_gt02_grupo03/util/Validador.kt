package com.example.proyectoinnovacionpdm2026_gt02_grupo03.util

object Validador {

    fun limpiarTelefono(telefono: String): String {
        return telefono.filter { it.isDigit() }
    }

    fun telefonoSvValido(telefono: String): Boolean {
        val telefonoLimpio = limpiarTelefono(telefono)
        return telefonoLimpio.length == 8
    }

    fun correoBasicoValido(correo: String): Boolean {
        return correo.contains("@") && correo.contains(".")
    }
}
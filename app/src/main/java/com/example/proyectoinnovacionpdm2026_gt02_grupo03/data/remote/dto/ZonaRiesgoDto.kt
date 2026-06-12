package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.dto

data class ZonaRiesgoDto(
    val idZona: Int,
    val nombre: String,
    val descripcion: String,
    val departamento: String,
    val municipio: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double,
    val radioMetros: Int,
    val nivelRiesgo: String,
    val activa: Boolean
)

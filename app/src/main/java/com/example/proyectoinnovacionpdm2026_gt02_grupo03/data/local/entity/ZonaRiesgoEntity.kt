package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zonas_riesgo")
data class ZonaRiesgoEntity(
    @PrimaryKey
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
    val activa: Boolean = true
)

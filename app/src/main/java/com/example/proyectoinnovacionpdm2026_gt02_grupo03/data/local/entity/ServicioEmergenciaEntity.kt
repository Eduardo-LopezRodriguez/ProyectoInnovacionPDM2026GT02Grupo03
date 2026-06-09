package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servicios_emergencia")
data class ServicioEmergenciaEntity(
    @PrimaryKey
    val idServicio: Int,
    val nombre: String,
    val telefono: String,
    val tipoServicio: String,
    val departamento: String,
    val municipio: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val activo: Boolean = true
)

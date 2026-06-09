package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alertas_emergencia")
data class AlertaEmergenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val idAlerta: Int = 0,
    val idUsuario: Int,
    val idZona: Int?,
    val idUbicacion: Int?,
    val idServicioSugerido: Int?,
    val origenAlerta: String,
    val tipoAlerta: String,
    val mensaje: String,
    val latitud: Double,
    val longitud: Double,
    val fechaHora: String,
    val estado: String
)

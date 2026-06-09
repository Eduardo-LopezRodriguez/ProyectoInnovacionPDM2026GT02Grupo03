package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ubicaciones_compartidas")
data class UbicacionCompartidaEntity(
    @PrimaryKey(autoGenerate = true)
    val idUbicacion: Int = 0,
    val idUsuario: Int,
    val latitud: Double,
    val longitud: Double,
    val fechaHora: String,
    val mensaje: String,
    val estadoEnvio: String,
    val activa: Boolean = true
)

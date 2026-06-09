package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lugares_importantes")
data class LugarImportanteEntity(
    @PrimaryKey(autoGenerate = true)
    val idLugar: Int = 0,
    val idUsuario: Int,
    val nombre: String,
    val tipoLugar: String,
    val direccion: String,
    val departamento: String,
    val municipio: String,
    val latitud: Double,
    val longitud: Double,
    val activo: Boolean = true
)

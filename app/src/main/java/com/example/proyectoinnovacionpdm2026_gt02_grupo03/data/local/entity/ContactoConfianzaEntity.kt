package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contactos_confianza")
data class ContactoConfianzaEntity(
    @PrimaryKey(autoGenerate = true)
    val idContacto: Int = 0,
    val idUsuario: Int,
    val nombre: String,
    val telefono: String,
    val correo: String,
    val parentesco: String,
    val prioridad: Int,
    val activo: Boolean = true
)

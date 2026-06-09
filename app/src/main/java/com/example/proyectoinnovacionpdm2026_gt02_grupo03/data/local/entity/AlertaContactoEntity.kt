package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alertas_contactos")
data class AlertaContactoEntity(
    @PrimaryKey(autoGenerate = true)
    val idAlertaContacto: Int = 0,
    val idAlerta: Int,
    val idContacto: Int,
    val medioEnvio: String,
    val mensajeEnviado: String,
    val estadoEnvio: String,
    val fechaEnvio: String
)

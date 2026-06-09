package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.AlertaEmergenciaEntity

@Dao
interface AlertaEmergenciaDao {

    @Insert
    suspend fun insertar(alerta: AlertaEmergenciaEntity): Long

    @Update
    suspend fun actualizar(alerta: AlertaEmergenciaEntity)

    @Query("SELECT * FROM alertas_emergencia WHERE idAlerta = :idAlerta LIMIT 1")
    suspend fun obtenerPorId(idAlerta: Int): AlertaEmergenciaEntity?

    @Query("SELECT * FROM alertas_emergencia WHERE idUsuario = :idUsuario ORDER BY fechaHora DESC")
    suspend fun listarPorUsuario(idUsuario: Int): List<AlertaEmergenciaEntity>

    @Query("SELECT * FROM alertas_emergencia WHERE idUsuario = :idUsuario ORDER BY fechaHora DESC LIMIT :limite")
    suspend fun listarRecientes(idUsuario: Int, limite: Int): List<AlertaEmergenciaEntity>

    @Query("UPDATE alertas_emergencia SET estado = :estado WHERE idAlerta = :idAlerta")
    suspend fun actualizarEstado(idAlerta: Int, estado: String)
}

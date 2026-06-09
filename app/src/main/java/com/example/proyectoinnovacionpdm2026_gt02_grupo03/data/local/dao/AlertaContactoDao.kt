package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.AlertaContactoEntity

@Dao
interface AlertaContactoDao {

    @Insert
    suspend fun insertar(alertaContacto: AlertaContactoEntity): Long

    @Insert
    suspend fun insertarTodos(alertasContactos: List<AlertaContactoEntity>): List<Long>

    @Query("SELECT * FROM alertas_contactos WHERE idAlerta = :idAlerta ORDER BY fechaEnvio DESC")
    suspend fun listarPorAlerta(idAlerta: Int): List<AlertaContactoEntity>

    @Query("SELECT * FROM alertas_contactos WHERE idContacto = :idContacto ORDER BY fechaEnvio DESC")
    suspend fun listarPorContacto(idContacto: Int): List<AlertaContactoEntity>
}

package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE idUsuario = :idUsuario LIMIT 1")
    suspend fun obtenerPorId(idUsuario: Int): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND password = :password AND estado = 1 LIMIT 1")
    suspend fun iniciarSesion(correo: String, password: String): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int
}

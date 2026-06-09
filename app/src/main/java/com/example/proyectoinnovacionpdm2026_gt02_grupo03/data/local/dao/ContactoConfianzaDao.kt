package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ContactoConfianzaEntity

@Dao
interface ContactoConfianzaDao {

    @Insert
    suspend fun insertar(contacto: ContactoConfianzaEntity): Long

    @Update
    suspend fun actualizar(contacto: ContactoConfianzaEntity)

    @Query("SELECT * FROM contactos_confianza WHERE idUsuario = :idUsuario AND activo = 1 ORDER BY prioridad ASC, nombre ASC")
    suspend fun listarActivosPorUsuario(idUsuario: Int): List<ContactoConfianzaEntity>

    @Query("SELECT * FROM contactos_confianza WHERE idContacto = :idContacto LIMIT 1")
    suspend fun obtenerPorId(idContacto: Int): ContactoConfianzaEntity?

    @Query("UPDATE contactos_confianza SET activo = 0 WHERE idContacto = :idContacto")
    suspend fun desactivar(idContacto: Int)

    @Query("DELETE FROM contactos_confianza WHERE idContacto = :idContacto")
    suspend fun eliminar(idContacto: Int)
}

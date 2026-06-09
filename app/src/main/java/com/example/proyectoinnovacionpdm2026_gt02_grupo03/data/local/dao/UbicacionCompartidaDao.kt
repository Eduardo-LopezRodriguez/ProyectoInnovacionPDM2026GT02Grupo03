package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.UbicacionCompartidaEntity

@Dao
interface UbicacionCompartidaDao {

    @Insert
    suspend fun insertar(ubicacion: UbicacionCompartidaEntity): Long

    @Query("SELECT * FROM ubicaciones_compartidas WHERE idUbicacion = :idUbicacion LIMIT 1")
    suspend fun obtenerPorId(idUbicacion: Int): UbicacionCompartidaEntity?

    @Query("SELECT * FROM ubicaciones_compartidas WHERE idUsuario = :idUsuario ORDER BY fechaHora DESC")
    suspend fun listarPorUsuario(idUsuario: Int): List<UbicacionCompartidaEntity>

    @Query("UPDATE ubicaciones_compartidas SET activa = 0 WHERE idUbicacion = :idUbicacion")
    suspend fun desactivar(idUbicacion: Int)
}

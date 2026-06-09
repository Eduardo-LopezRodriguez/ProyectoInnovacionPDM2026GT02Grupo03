package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.LugarImportanteEntity

@Dao
interface LugarImportanteDao {

    @Insert
    suspend fun insertar(lugar: LugarImportanteEntity): Long

    @Update
    suspend fun actualizar(lugar: LugarImportanteEntity)

    @Query("SELECT * FROM lugares_importantes WHERE idUsuario = :idUsuario AND activo = 1 ORDER BY nombre ASC")
    suspend fun listarActivosPorUsuario(idUsuario: Int): List<LugarImportanteEntity>

    @Query("SELECT * FROM lugares_importantes WHERE idLugar = :idLugar LIMIT 1")
    suspend fun obtenerPorId(idLugar: Int): LugarImportanteEntity?

    @Query("UPDATE lugares_importantes SET activo = 0 WHERE idLugar = :idLugar")
    suspend fun desactivar(idLugar: Int)

    @Query("DELETE FROM lugares_importantes WHERE idLugar = :idLugar")
    suspend fun eliminar(idLugar: Int)
}

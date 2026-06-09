package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ZonaRiesgoEntity

@Dao
interface ZonaRiesgoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(zona: ZonaRiesgoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(zonas: List<ZonaRiesgoEntity>)

    @Query("SELECT * FROM zonas_riesgo WHERE activa = 1 ORDER BY nivelRiesgo DESC, nombre ASC")
    suspend fun listarActivas(): List<ZonaRiesgoEntity>

    @Query("SELECT * FROM zonas_riesgo WHERE idZona = :idZona LIMIT 1")
    suspend fun obtenerPorId(idZona: Int): ZonaRiesgoEntity?

    @Query("SELECT * FROM zonas_riesgo WHERE categoria = :categoria AND activa = 1 ORDER BY nombre ASC")
    suspend fun listarPorCategoria(categoria: String): List<ZonaRiesgoEntity>

    @Query("DELETE FROM zonas_riesgo")
    suspend fun eliminarTodas()
}

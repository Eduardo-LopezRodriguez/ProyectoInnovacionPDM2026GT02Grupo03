package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ServicioEmergenciaEntity

@Dao
interface ServicioEmergenciaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(servicio: ServicioEmergenciaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(servicios: List<ServicioEmergenciaEntity>)

    @Query("SELECT * FROM servicios_emergencia WHERE activo = 1 ORDER BY tipoServicio ASC, nombre ASC")
    suspend fun listarActivos(): List<ServicioEmergenciaEntity>

    @Query("SELECT * FROM servicios_emergencia WHERE idServicio = :idServicio LIMIT 1")
    suspend fun obtenerPorId(idServicio: Int): ServicioEmergenciaEntity?

    @Query("SELECT * FROM servicios_emergencia WHERE tipoServicio = :tipoServicio AND activo = 1 ORDER BY nombre ASC")
    suspend fun listarPorTipo(tipoServicio: String): List<ServicioEmergenciaEntity>

    @Query("SELECT * FROM servicios_emergencia WHERE tipoServicio = :tipoServicio AND departamento = :departamento AND activo = 1 ORDER BY nombre ASC")
    suspend fun listarPorTipoYDepartamento(tipoServicio: String, departamento: String): List<ServicioEmergenciaEntity>

    @Query("DELETE FROM servicios_emergencia")
    suspend fun eliminarTodos()
}

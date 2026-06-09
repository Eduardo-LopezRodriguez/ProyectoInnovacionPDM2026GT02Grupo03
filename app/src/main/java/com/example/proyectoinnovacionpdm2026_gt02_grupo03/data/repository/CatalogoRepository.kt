package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository

import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.ServicioEmergenciaDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.ZonaRiesgoDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ServicioEmergenciaEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ZonaRiesgoEntity

class CatalogoRepository(
    private val zonaRiesgoDao: ZonaRiesgoDao,
    private val servicioEmergenciaDao: ServicioEmergenciaDao
) {

    suspend fun listarZonasActivas(): List<ZonaRiesgoEntity> {
        return zonaRiesgoDao.listarActivas()
    }

    suspend fun obtenerZonaPorId(idZona: Int): ZonaRiesgoEntity? {
        return zonaRiesgoDao.obtenerPorId(idZona)
    }

    suspend fun listarServiciosActivos(): List<ServicioEmergenciaEntity> {
        return servicioEmergenciaDao.listarActivos()
    }

    suspend fun obtenerServicioPorId(idServicio: Int): ServicioEmergenciaEntity? {
        return servicioEmergenciaDao.obtenerPorId(idServicio)
    }

    suspend fun guardarZonas(zonas: List<ZonaRiesgoEntity>) {
        zonaRiesgoDao.insertarTodas(zonas)
    }

    suspend fun guardarServicios(servicios: List<ServicioEmergenciaEntity>) {
        servicioEmergenciaDao.insertarTodos(servicios)
    }

    fun obtenerTiposServicioSugeridos(categoria: String): List<String> {
        return when (categoria.uppercase()) {
            "ROBO" -> listOf("POLICIA")
            "INCENDIO" -> listOf("BOMBEROS")
            "INUNDACION" -> listOf("PROTECCION_CIVIL", "BOMBEROS")
            "ANIMALES" -> listOf("ALCALDIA", "UNIDAD_SALUD")
            "ACCIDENTE" -> listOf("CRUZ_ROJA", "BOMBEROS")
            "SALUD" -> listOf("HOSPITAL", "CRUZ_ROJA")
            else -> emptyList()
        }
    }

    suspend fun sugerirServicioPorCategoria(
        categoria: String,
        departamento: String? = null
    ): ServicioEmergenciaEntity? {
        val tipos = obtenerTiposServicioSugeridos(categoria)

        for (tipo in tipos) {
            val servicios = if (departamento.isNullOrBlank()) {
                servicioEmergenciaDao.listarPorTipo(tipo)
            } else {
                servicioEmergenciaDao.listarPorTipoYDepartamento(tipo, departamento)
            }

            if (servicios.isNotEmpty()) {
                return servicios.first()
            }
        }

        return null
    }
}

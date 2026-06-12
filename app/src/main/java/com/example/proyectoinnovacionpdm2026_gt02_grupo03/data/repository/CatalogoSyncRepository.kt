package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository

import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.ServicioEmergenciaDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.ZonaRiesgoDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.api.ApiService
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.mapper.toEntity

data class ResultadoSincronizacion(
    val exito: Boolean,
    val mensaje: String,
    val zonasSincronizadas: Int = 0,
    val serviciosSincronizados: Int = 0
)

class CatalogoSyncRepository(
    private val apiService: ApiService,
    private val zonaRiesgoDao: ZonaRiesgoDao,
    private val servicioEmergenciaDao: ServicioEmergenciaDao
) {

    suspend fun sincronizarZonasRiesgo(): ResultadoSincronizacion {
        return try {
            val zonasRemotas = apiService.obtenerZonasRiesgo()
            val zonasLocales = zonasRemotas.map { it.toEntity() }

            zonaRiesgoDao.insertarTodas(zonasLocales)

            ResultadoSincronizacion(
                exito = true,
                mensaje = "Zonas de riesgo sincronizadas correctamente.",
                zonasSincronizadas = zonasLocales.size
            )
        } catch (e: Exception) {
            ResultadoSincronizacion(
                exito = false,
                mensaje = "Error al sincronizar zonas de riesgo: ${e.message ?: "error desconocido"}"
            )
        }
    }

    suspend fun sincronizarServiciosEmergencia(): ResultadoSincronizacion {
        return try {
            val serviciosRemotos = apiService.obtenerServiciosEmergencia()
            val serviciosLocales = serviciosRemotos.map { it.toEntity() }

            servicioEmergenciaDao.insertarTodos(serviciosLocales)

            ResultadoSincronizacion(
                exito = true,
                mensaje = "Servicios de emergencia sincronizados correctamente.",
                serviciosSincronizados = serviciosLocales.size
            )
        } catch (e: Exception) {
            ResultadoSincronizacion(
                exito = false,
                mensaje = "Error al sincronizar servicios de emergencia: ${e.message ?: "error desconocido"}"
            )
        }
    }

    suspend fun sincronizarCatalogos(): ResultadoSincronizacion {
        return try {
            val zonasRemotas = apiService.obtenerZonasRiesgo()
            val serviciosRemotos = apiService.obtenerServiciosEmergencia()

            val zonasLocales = zonasRemotas.map { it.toEntity() }
            val serviciosLocales = serviciosRemotos.map { it.toEntity() }

            zonaRiesgoDao.insertarTodas(zonasLocales)
            servicioEmergenciaDao.insertarTodos(serviciosLocales)

            ResultadoSincronizacion(
                exito = true,
                mensaje = "Catálogos sincronizados correctamente.",
                zonasSincronizadas = zonasLocales.size,
                serviciosSincronizados = serviciosLocales.size
            )
        } catch (e: Exception) {
            ResultadoSincronizacion(
                exito = false,
                mensaje = "Error al sincronizar catálogos: ${e.message ?: "error desconocido"}"
            )
        }
    }
}

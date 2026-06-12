package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.api

import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.dto.HealthDto
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.dto.ServicioEmergenciaDto
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.dto.ZonaRiesgoDto
import retrofit2.http.GET

interface ApiService {

    @GET("zonas-riesgo.json")
    suspend fun obtenerZonasRiesgo(): List<ZonaRiesgoDto>

    @GET("servicios-emergencia.json")
    suspend fun obtenerServiciosEmergencia(): List<ServicioEmergenciaDto>

    @GET("health.json")
    suspend fun verificarApi(): HealthDto
}

package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.mapper

import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ServicioEmergenciaEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ZonaRiesgoEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.dto.ServicioEmergenciaDto
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.dto.ZonaRiesgoDto

fun ZonaRiesgoDto.toEntity(): ZonaRiesgoEntity {
    return ZonaRiesgoEntity(
        idZona = idZona,
        nombre = nombre,
        descripcion = descripcion,
        departamento = departamento,
        municipio = municipio,
        categoria = categoria,
        latitud = latitud,
        longitud = longitud,
        radioMetros = radioMetros,
        nivelRiesgo = nivelRiesgo,
        activa = activa
    )
}

fun ServicioEmergenciaDto.toEntity(): ServicioEmergenciaEntity {
    return ServicioEmergenciaEntity(
        idServicio = idServicio,
        nombre = nombre,
        telefono = telefono,
        tipoServicio = tipoServicio,
        departamento = departamento,
        municipio = municipio,
        direccion = direccion,
        latitud = latitud,
        longitud = longitud,
        activo = activo
    )
}

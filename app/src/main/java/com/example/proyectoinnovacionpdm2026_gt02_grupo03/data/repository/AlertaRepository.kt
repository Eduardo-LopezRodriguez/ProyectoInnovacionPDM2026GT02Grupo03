package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository

import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.AlertaContactoDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.AlertaEmergenciaDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.ContactoConfianzaDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.UbicacionCompartidaDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.AlertaContactoEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.AlertaEmergenciaEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.UbicacionCompartidaEntity

class AlertaRepository(
    private val ubicacionDao: UbicacionCompartidaDao,
    private val alertaDao: AlertaEmergenciaDao,
    private val alertaContactoDao: AlertaContactoDao,
    private val contactoDao: ContactoConfianzaDao
) {

    suspend fun registrarUbicacion(ubicacion: UbicacionCompartidaEntity): Long {
        return ubicacionDao.insertar(ubicacion)
    }

    suspend fun registrarAlerta(alerta: AlertaEmergenciaEntity): Long {
        return alertaDao.insertar(alerta)
    }

    suspend fun registrarAlertaContacto(alertaContacto: AlertaContactoEntity): Long {
        return alertaContactoDao.insertar(alertaContacto)
    }

    suspend fun listarAlertasPorUsuario(idUsuario: Int): List<AlertaEmergenciaEntity> {
        return alertaDao.listarPorUsuario(idUsuario)
    }

    suspend fun listarAlertasRecientes(idUsuario: Int, limite: Int): List<AlertaEmergenciaEntity> {
        return alertaDao.listarRecientes(idUsuario, limite)
    }

    suspend fun obtenerAlertaPorId(idAlerta: Int): AlertaEmergenciaEntity? {
        return alertaDao.obtenerPorId(idAlerta)
    }

    suspend fun listarContactosNotificados(idAlerta: Int): List<AlertaContactoEntity> {
        return alertaContactoDao.listarPorAlerta(idAlerta)
    }

    suspend fun actualizarEstadoAlerta(idAlerta: Int, estado: String) {
        alertaDao.actualizarEstado(idAlerta, estado)
    }

    suspend fun registrarAlertaConContactos(
        idUsuario: Int,
        latitud: Double,
        longitud: Double,
        fechaHora: String,
        mensaje: String,
        origenAlerta: String,
        tipoAlerta: String,
        idZona: Int? = null,
        idServicioSugerido: Int? = null
    ): Long {
        val ubicacionId = ubicacionDao.insertar(
            UbicacionCompartidaEntity(
                idUsuario = idUsuario,
                latitud = latitud,
                longitud = longitud,
                fechaHora = fechaHora,
                mensaje = mensaje,
                estadoEnvio = "REGISTRADA",
                activa = true
            )
        ).toInt()

        val alertaId = alertaDao.insertar(
            AlertaEmergenciaEntity(
                idUsuario = idUsuario,
                idZona = idZona,
                idUbicacion = ubicacionId,
                idServicioSugerido = idServicioSugerido,
                origenAlerta = origenAlerta,
                tipoAlerta = tipoAlerta,
                mensaje = mensaje,
                latitud = latitud,
                longitud = longitud,
                fechaHora = fechaHora,
                estado = "GENERADA"
            )
        ).toInt()

        val contactos = contactoDao.listarActivosPorUsuario(idUsuario)

        val notificaciones = contactos.map { contacto ->
            AlertaContactoEntity(
                idAlerta = alertaId,
                idContacto = contacto.idContacto,
                medioEnvio = "APP",
                mensajeEnviado = mensaje,
                estadoEnvio = "REGISTRADO",
                fechaEnvio = fechaHora
            )
        }

        if (notificaciones.isNotEmpty()) {
            alertaContactoDao.insertarTodos(notificaciones)
        }

        return alertaId.toLong()
    }
}

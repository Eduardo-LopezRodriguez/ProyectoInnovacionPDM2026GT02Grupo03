package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository

import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.ContactoConfianzaDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ContactoConfianzaEntity

class ContactoRepository(
    private val contactoDao: ContactoConfianzaDao
) {

    suspend fun insertar(contacto: ContactoConfianzaEntity): Long {
        return contactoDao.insertar(contacto)
    }

    suspend fun actualizar(contacto: ContactoConfianzaEntity) {
        contactoDao.actualizar(contacto)
    }

    suspend fun listarActivosPorUsuario(idUsuario: Int): List<ContactoConfianzaEntity> {
        return contactoDao.listarActivosPorUsuario(idUsuario)
    }

    suspend fun obtenerPorId(idContacto: Int): ContactoConfianzaEntity? {
        return contactoDao.obtenerPorId(idContacto)
    }

    suspend fun desactivar(idContacto: Int) {
        contactoDao.desactivar(idContacto)
    }

    suspend fun eliminar(idContacto: Int) {
        contactoDao.eliminar(idContacto)
    }
}

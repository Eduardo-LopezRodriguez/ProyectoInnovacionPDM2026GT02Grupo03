package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository

import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.LugarImportanteDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.LugarImportanteEntity

class LugarRepository(
    private val lugarDao: LugarImportanteDao
) {

    suspend fun insertar(lugar: LugarImportanteEntity): Long {
        return lugarDao.insertar(lugar)
    }

    suspend fun actualizar(lugar: LugarImportanteEntity) {
        lugarDao.actualizar(lugar)
    }

    suspend fun listarActivosPorUsuario(idUsuario: Int): List<LugarImportanteEntity> {
        return lugarDao.listarActivosPorUsuario(idUsuario)
    }

    suspend fun obtenerPorId(idLugar: Int): LugarImportanteEntity? {
        return lugarDao.obtenerPorId(idLugar)
    }

    suspend fun desactivar(idLugar: Int) {
        lugarDao.desactivar(idLugar)
    }

    suspend fun eliminar(idLugar: Int) {
        lugarDao.eliminar(idLugar)
    }
}

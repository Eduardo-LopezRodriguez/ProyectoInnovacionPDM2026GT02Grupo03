package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository

import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.UsuarioDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.UsuarioEntity

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    suspend fun registrar(usuario: UsuarioEntity): Long {
        return usuarioDao.insertar(usuario)
    }

    suspend fun actualizar(usuario: UsuarioEntity) {
        usuarioDao.actualizar(usuario)
    }

    suspend fun obtenerPorId(idUsuario: Int): UsuarioEntity? {
        return usuarioDao.obtenerPorId(idUsuario)
    }

    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity? {
        return usuarioDao.obtenerPorCorreo(correo.trim())
    }

    suspend fun iniciarSesion(correo: String, password: String): UsuarioEntity? {
        return usuarioDao.iniciarSesion(correo.trim(), password)
    }

    suspend fun correoExiste(correo: String): Boolean {
        return usuarioDao.obtenerPorCorreo(correo.trim()) != null
    }

    suspend fun contarUsuarios(): Int {
        return usuarioDao.contarUsuarios()
    }
}

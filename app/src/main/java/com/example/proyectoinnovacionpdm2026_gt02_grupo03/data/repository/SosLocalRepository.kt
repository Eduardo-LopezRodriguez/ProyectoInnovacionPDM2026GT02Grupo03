package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database.AppDatabase

data class ResultadoSos(
    val exito: Boolean,
    val mensaje: String,
    val idAlerta: Long = 0,
    val contactosRegistrados: Int = 0
)

data class HistorialAlertaItem(
    val idAlerta: Int,
    val tipoAlerta: String,
    val origenAlerta: String,
    val mensaje: String,
    val latitud: Double,
    val longitud: Double,
    val fechaHora: String,
    val estado: String
)

data class ContactoEnvioSos(
    val idContacto: Int,
    val nombre: String,
    val telefono: String
)


class SosLocalRepository(
    private val db: AppDatabase
) {

    fun registrarAlertaSos(
        idUsuario: Int,
        latitud: Double,
        longitud: Double,
        fechaHora: String
    ): ResultadoSos {
        val database = db.openHelper.writableDatabase

        return try {
            database.beginTransaction()

            val ubicacionValues = ContentValues().apply {
                put("idUsuario", idUsuario)
                put("latitud", latitud)
                put("longitud", longitud)
                put("fechaHora", fechaHora)
                put("mensaje", "Ubicación registrada por alerta SOS")
                put("estadoEnvio", "REGISTRADA")
                put("activa", 1)
            }

            val idUbicacion = database.insert(
                "ubicaciones_compartidas",
                SQLiteDatabase.CONFLICT_REPLACE,
                ubicacionValues
            )

            val mensajeAlerta = "Alerta SOS activada por el usuario. Ubicación: $latitud, $longitud"

            val alertaValues = ContentValues().apply {
                put("idUsuario", idUsuario)
                putNull("idZona")
                put("idUbicacion", idUbicacion)
                putNull("idServicioSugerido")
                put("origenAlerta", "SOS_MANUAL")
                put("tipoAlerta", "SOS")
                put("mensaje", mensajeAlerta)
                put("latitud", latitud)
                put("longitud", longitud)
                put("fechaHora", fechaHora)
                put("estado", "ACTIVA")
            }

            val idAlerta = database.insert(
                "alertas_emergencia",
                SQLiteDatabase.CONFLICT_REPLACE,
                alertaValues
            )

            val contactos = obtenerContactosActivos(idUsuario)
            var contactosInsertados = 0

            contactos.forEach { contacto ->
                val alertaContactoValues = ContentValues().apply {
                    put("idAlerta", idAlerta)
                    put("idContacto", contacto.idContacto)
                    put("medioEnvio", "SMS")
                    put(
                        "mensajeEnviado",
                        "Alerta SOS registrada para ${contacto.nombre}. Teléfono: ${contacto.telefono}"
                    )
                    put("estadoEnvio", "PREPARADO")
                    put("fechaEnvio", fechaHora)
                }

                database.insert(
                    "alertas_contactos",
                    SQLiteDatabase.CONFLICT_REPLACE,
                    alertaContactoValues
                )

                contactosInsertados++
            }

            database.setTransactionSuccessful()

            ResultadoSos(
                exito = true,
                mensaje = "Alerta SOS registrada. Contactos asociados: $contactosInsertados",
                idAlerta = idAlerta,
                contactosRegistrados = contactosInsertados
            )
        } catch (e: Exception) {
            ResultadoSos(
                exito = false,
                mensaje = "Error al registrar alerta SOS: ${e.message ?: "error desconocido"}"
            )
        } finally {
            database.endTransaction()
        }
    }

    fun obtenerContactosActivosParaEnvio(idUsuario: Int): List<ContactoEnvioSos> {
        val database = db.openHelper.readableDatabase

        val cursor = database.query(
            """
        SELECT idContacto, nombre, telefono
        FROM contactos_confianza
        WHERE idUsuario = ? AND activo = 1
        ORDER BY prioridad ASC
        """.trimIndent(),
            arrayOf(idUsuario)
        )

        val contactos = mutableListOf<ContactoEnvioSos>()

        cursor.use {
            while (it.moveToNext()) {
                contactos.add(
                    ContactoEnvioSos(
                        idContacto = it.getInt(0),
                        nombre = it.getString(1),
                        telefono = it.getString(2)
                    )
                )
            }
        }

        return contactos
    }

    fun obtenerHistorialAlertas(idUsuario: Int): List<HistorialAlertaItem> {
        val database = db.openHelper.readableDatabase

        val cursor = database.query(
            """
            SELECT idAlerta, tipoAlerta, origenAlerta, mensaje, latitud, longitud, fechaHora, estado
            FROM alertas_emergencia
            WHERE idUsuario = ?
            ORDER BY idAlerta DESC
            """.trimIndent(),
            arrayOf(idUsuario)
        )

        val lista = mutableListOf<HistorialAlertaItem>()

        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    HistorialAlertaItem(
                        idAlerta = it.getInt(0),
                        tipoAlerta = it.getString(1),
                        origenAlerta = it.getString(2),
                        mensaje = it.getString(3),
                        latitud = it.getDouble(4),
                        longitud = it.getDouble(5),
                        fechaHora = it.getString(6),
                        estado = it.getString(7)
                    )
                )
            }
        }

        return lista
    }

    private fun obtenerContactosActivos(idUsuario: Int): List<ContactoSos> {
        val database = db.openHelper.readableDatabase

        val cursor = database.query(
            """
            SELECT idContacto, nombre, telefono
            FROM contactos_confianza
            WHERE idUsuario = ? AND activo = 1
            ORDER BY prioridad ASC
            """.trimIndent(),
            arrayOf(idUsuario)
        )

        val contactos = mutableListOf<ContactoSos>()

        cursor.use {
            while (it.moveToNext()) {
                contactos.add(
                    ContactoSos(
                        idContacto = it.getInt(0),
                        nombre = it.getString(1),
                        telefono = it.getString(2)
                    )
                )
            }
        }

        return contactos
    }

    private data class ContactoSos(
        val idContacto: Int,
        val nombre: String,
        val telefono: String
    )
}

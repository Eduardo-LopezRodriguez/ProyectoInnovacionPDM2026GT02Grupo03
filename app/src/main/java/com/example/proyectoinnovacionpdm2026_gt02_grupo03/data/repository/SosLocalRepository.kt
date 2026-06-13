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

data class ResultadoOperacion(
    val exito: Boolean,
    val mensaje: String
)

data class HistorialAlertaItem(
    val idAlerta: Int,
    val tipoAlerta: String,
    val origenAlerta: String,
    val mensaje: String,
    val latitud: Double,
    val longitud: Double,
    val fechaHora: String,
    val estado: String,
    val contactoNombre: String?,
    val contactoTelefono: String?,
    val medioEnvio: String?,
    val estadoEnvio: String?
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
                put("estado", "PENDIENTE")
            }

            val idAlerta = database.insert(
                "alertas_emergencia",
                SQLiteDatabase.CONFLICT_REPLACE,
                alertaValues
            )

            database.setTransactionSuccessful()

            ResultadoSos(
                exito = true,
                mensaje = "Alerta SOS registrada. Selecciona un contacto para enviar.",
                idAlerta = idAlerta,
                contactosRegistrados = 0
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

    fun registrarEnvioContacto(
        idAlerta: Long,
        contacto: ContactoEnvioSos,
        mensajeEnviado: String,
        fechaEnvio: String
    ): ResultadoOperacion {
        val database = db.openHelper.writableDatabase

        return try {
            database.beginTransaction()

            val alertaContactoValues = ContentValues().apply {
                put("idAlerta", idAlerta)
                put("idContacto", contacto.idContacto)
                put("medioEnvio", "SMS")
                put("mensajeEnviado", mensajeEnviado)
                put("estadoEnvio", "ENVIADO")
                put("fechaEnvio", fechaEnvio)
            }

            database.insert(
                "alertas_contactos",
                SQLiteDatabase.CONFLICT_REPLACE,
                alertaContactoValues
            )

            database.execSQL(
                "UPDATE alertas_emergencia SET estado = ? WHERE idAlerta = ?",
                arrayOf("ENVIADA", idAlerta)
            )

            database.setTransactionSuccessful()

            ResultadoOperacion(
                exito = true,
                mensaje = "Alerta asociada a ${contacto.nombre} y marcada como enviada."
            )
        } catch (e: Exception) {
            ResultadoOperacion(
                exito = false,
                mensaje = "No se pudo registrar el envío: ${e.message ?: "error desconocido"}"
            )
        } finally {
            database.endTransaction()
        }
    }

    fun obtenerHistorialAlertas(idUsuario: Int): List<HistorialAlertaItem> {
        val database = db.openHelper.readableDatabase

        val cursor = database.query(
            """
            SELECT 
                ae.idAlerta,
                ae.tipoAlerta,
                ae.origenAlerta,
                ae.mensaje,
                ae.latitud,
                ae.longitud,
                ae.fechaHora,
                ae.estado,
                cc.nombre,
                cc.telefono,
                ac.medioEnvio,
                ac.estadoEnvio
            FROM alertas_emergencia ae
            LEFT JOIN alertas_contactos ac ON ae.idAlerta = ac.idAlerta
            LEFT JOIN contactos_confianza cc ON ac.idContacto = cc.idContacto
            WHERE ae.idUsuario = ?
            ORDER BY ae.idAlerta DESC
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
                        estado = it.getString(7),
                        contactoNombre = if (it.isNull(8)) null else it.getString(8),
                        contactoTelefono = if (it.isNull(9)) null else it.getString(9),
                        medioEnvio = if (it.isNull(10)) null else it.getString(10),
                        estadoEnvio = if (it.isNull(11)) null else it.getString(11)
                    )
                )
            }
        }

        return lista
    }

    fun vaciarHistorialAlertas(idUsuario: Int): ResultadoOperacion {
        val database = db.openHelper.writableDatabase

        return try {
            database.beginTransaction()

            database.execSQL(
                """
                DELETE FROM alertas_contactos
                WHERE idAlerta IN (
                    SELECT idAlerta FROM alertas_emergencia WHERE idUsuario = ?
                )
                """.trimIndent(),
                arrayOf(idUsuario)
            )

            database.execSQL(
                "DELETE FROM alertas_emergencia WHERE idUsuario = ?",
                arrayOf(idUsuario)
            )

            database.execSQL(
                """
                DELETE FROM ubicaciones_compartidas
                WHERE idUsuario = ? AND mensaje LIKE '%alerta SOS%'
                """.trimIndent(),
                arrayOf(idUsuario)
            )

            database.setTransactionSuccessful()

            ResultadoOperacion(
                exito = true,
                mensaje = "Historial de alertas vaciado correctamente."
            )
        } catch (e: Exception) {
            ResultadoOperacion(
                exito = false,
                mensaje = "No se pudo vaciar el historial: ${e.message ?: "error desconocido"}"
            )
        } finally {
            database.endTransaction()
        }
    }
}

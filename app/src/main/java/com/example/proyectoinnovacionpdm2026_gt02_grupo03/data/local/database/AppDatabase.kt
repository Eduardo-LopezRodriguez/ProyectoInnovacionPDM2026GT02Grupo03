package com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.AlertaContactoDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.AlertaEmergenciaDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.ContactoConfianzaDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.LugarImportanteDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.ServicioEmergenciaDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.UbicacionCompartidaDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.UsuarioDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.dao.ZonaRiesgoDao
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.AlertaContactoEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.AlertaEmergenciaEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ContactoConfianzaEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.LugarImportanteEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ServicioEmergenciaEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.UbicacionCompartidaEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.UsuarioEntity
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ZonaRiesgoEntity

@Database(
    entities = [
        UsuarioEntity::class,
        ContactoConfianzaEntity::class,
        LugarImportanteEntity::class,
        ZonaRiesgoEntity::class,
        ServicioEmergenciaEntity::class,
        UbicacionCompartidaEntity::class,
        AlertaEmergenciaEntity::class,
        AlertaContactoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun contactoConfianzaDao(): ContactoConfianzaDao
    abstract fun lugarImportanteDao(): LugarImportanteDao
    abstract fun zonaRiesgoDao(): ZonaRiesgoDao
    abstract fun servicioEmergenciaDao(): ServicioEmergenciaDao
    abstract fun ubicacionCompartidaDao(): UbicacionCompartidaDao
    abstract fun alertaEmergenciaDao(): AlertaEmergenciaDao
    abstract fun alertaContactoDao(): AlertaContactoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerBaseDatos(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "seguridad_personal_db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instancia
                instancia
            }
        }
    }
}

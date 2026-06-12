package com.example.proyectoinnovacionpdm2026_gt02_grupo03.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.database.AppDatabase
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.remote.api.RetrofitClient
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.CatalogoSyncRepository

class CatalogoSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.obtenerBaseDatos(applicationContext)

            val repository = CatalogoSyncRepository(
                apiService = RetrofitClient.apiService,
                zonaRiesgoDao = db.zonaRiesgoDao(),
                servicioEmergenciaDao = db.servicioEmergenciaDao()
            )

            val resultado = repository.sincronizarCatalogos()

            if (resultado.exito) {
                Result.success(
                    workDataOf(
                        "mensaje" to resultado.mensaje,
                        "zonas" to resultado.zonasSincronizadas,
                        "servicios" to resultado.serviciosSincronizados
                    )
                )
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

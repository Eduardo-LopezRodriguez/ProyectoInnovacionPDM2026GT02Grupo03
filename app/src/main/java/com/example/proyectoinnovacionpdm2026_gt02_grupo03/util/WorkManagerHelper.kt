package com.example.proyectoinnovacionpdm2026_gt02_grupo03.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.worker.CatalogoSyncWorker
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

    private const val WORK_SYNC_UNICO = "sync_catalogos_unico"
    private const val WORK_SYNC_PERIODICO = "sync_catalogos_periodico"

    fun sincronizarCatalogosUnaVez(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<CatalogoSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_SYNC_UNICO,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun programarSincronizacionPeriodica(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<CatalogoSyncWorker>(
            6,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_SYNC_PERIODICO,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}

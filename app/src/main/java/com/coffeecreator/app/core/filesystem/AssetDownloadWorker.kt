package com.coffeecreator.app.core.filesystem

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.coffeecreator.app.CoffeeApplication
import java.io.IOException
import kotlinx.coroutines.CancellationException

class AssetDownloadWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        val graph = (applicationContext as CoffeeApplication).graph
        graph.repository.initialize()
        val id = inputData.getString("assetId") ?: return Result.failure()
        val asset = graph.repository.models.value.find { it.id == id } ?: return Result.failure()
        return try {
            setForeground(getForegroundInfo())
            graph.assets.transfer(asset)
            Result.success()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: IOException) {
            // Retry only when the user selects Resume; no silent fresh network transfer.
            Result.failure()
        } catch (boundaryFailure: RuntimeException) {
            Result.failure(androidx.work.workDataOf("error" to boundaryFailure.message.orEmpty()))
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel("asset-download", "Requested model transfers", NotificationManager.IMPORTANCE_LOW)
        )
        val notification = NotificationCompat.Builder(applicationContext, "asset-download")
            .setContentTitle("Coffee Creator: model transfer")
            .setContentText("A model download you requested is running. Pause or cancel in Models.")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .build()
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ForegroundInfo(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }
    companion object {
        private const val NOTIFICATION_ID = 410
    }
}

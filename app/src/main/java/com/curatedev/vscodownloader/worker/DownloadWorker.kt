package com.curatedev.vscodownloader.worker

import android.Manifest
import android.app.Notification
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.curatedev.vscodownloader.R
import com.curatedev.vscodownloader.cache.transferToOutputStream
import com.curatedev.vscodownloader.vsco.VscoHandler
import com.curatedev.vscodownloader.vsco.dataclasses.VscoMedia
import com.curatedev.vscodownloader.vsco.dataclasses.VscoProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadWorker(
    private val applicationContext: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(applicationContext, workerParameters) {
    val contentResolver = applicationContext.contentResolver
    val notificationManager = NotificationManagerCompat.from(applicationContext)
    val channel = NotificationChannelCompat.Builder("523lk4jvbh7", NotificationManagerCompat.IMPORTANCE_LOW)
        .setName("Vsco Downloader Service")
        .setLightColor(Color.BLUE)
        .setVibrationEnabled(false)
        .build()
    init {

        notificationManager.createNotificationChannel(channel)
    }
    val notificationId = 185796725
    private suspend fun List<VscoMedia>.uniqueOnly(relativePath: String) = withContext(Dispatchers.IO) {
        val mutable = this@uniqueOnly.toMutableList()
        val downloadsUri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)
        contentResolver.query(
            downloadsUri,
            arrayOf(MediaStore.Downloads.DISPLAY_NAME, MediaStore.Downloads.RELATIVE_PATH),
            null,
            null,
            null
        )?.use { cursor ->
            val displayNameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                mutable.removeIf { it.filename == cursor.getString(displayNameIndex).dropLast(4) }
            }
        }
        return@withContext mutable.toList()
    }

    private suspend fun VscoMedia.download(relativePath: String) = withContext(Dispatchers.IO) {
        val meta = ContentValues().apply {
            put(MediaStore.DownloadColumns.DISPLAY_NAME, filename)
            put(MediaStore.DownloadColumns.MIME_TYPE, mime)
            put(MediaStore.DownloadColumns.RELATIVE_PATH, relativePath)
        }
        contentResolver.insert(MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL), meta)?.let {  fileUri ->
            contentResolver.openOutputStream(fileUri)?.use { fos ->
                VscoHandler.getMediaBytes(this@download)?.use { bis ->
                    bis.transferToOutputStream(fos)
                }
            }
        }

    }

    override suspend fun doWork(): Result {
        val vscoProfile = VscoProfile(
            inputData.getString("name")!!,
            inputData.getLong("siteId", 0),
            inputData.getString("imageId"),
            Uri.parse(inputData.getString("uri")),

        )

        var page = 0
        var cursor: String? = null
        while (true) {
            page ++

            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(notificationId, createPageNotification(page))
            }
            val result = VscoHandler.getMediaResult(vscoProfile.siteId, cursor) ?: return Result.failure()
            val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/Vsco Downloader/${vscoProfile.name}"
            val mediaToDownload = result.media.map { it.toVscoMedia() }.uniqueOnly(relativePath)

            mediaToDownload.forEach { media ->
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notificationManager.notify(notificationId, createDownloadNotification(media))
                }

                media.download(relativePath)
            }
            if (result.nextCursor == null || result.nextCursor == cursor) {
                break
            } else {
                cursor = result.nextCursor
            }
        }
        return Result.success()
    }

    private fun createPageNotification(page: Int): Notification {
        val title = "Vsco Downloader"
        val notification = NotificationCompat.Builder(applicationContext, "523lk4jvbh7")
            .setContentTitle(title)
            .setContentText("Getting page $page")
            .setSmallIcon(R.drawable.baseline_boy_24)
            .setPriority(NotificationManagerCompat.IMPORTANCE_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()
        return notification
    }

    private fun createDownloadNotification(vscoMedia: VscoMedia): Notification {
        val title = "Vsco Downloader"
        val notification = NotificationCompat.Builder(applicationContext, "523lk4jvbh7")
            .setContentTitle(title)
            .setContentText("Downloading ${vscoMedia.filename}")
            .setSmallIcon(R.drawable.baseline_boy_24)
            .setOngoing(true)
            .setPriority(NotificationManagerCompat.IMPORTANCE_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
        return notification
    }

}
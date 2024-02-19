package com.example.vscodownloader.cache

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.vscodownloader.vsco.VscoHandler
import com.example.vscodownloader.vsco.dataclasses.VscoMedia
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import java.io.File
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoilCacheDownloader @Inject constructor(
    @ApplicationContext private val context: Context
){
    val diskCache = DiskCache.Builder()
        .build()
    val memoryCache = MemoryCache.Builder(context)
        .build()
    val mediaMemoryKeys = hashMapOf<VscoMedia, MemoryCache.Key>()
    val mediaDiskKeys = hashMapOf<VscoMedia, String>()

    @OptIn(ExperimentalCoilApi::class)
    fun imageLoaded(vscoMedia: VscoMedia, memoryKey: MemoryCache.Key?, diskKey: String?) {
        if (memoryKey != null) {
            mediaMemoryKeys[vscoMedia] = memoryKey
        }
        if (diskKey != null) {
            mediaMemoryKeys[vscoMedia] = diskKey
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    suspend fun getImageFromMedia(vscoMedia: VscoMedia, outputStream: OutputStream) = withContext(Dispatchers.IO) {
        mediaMemoryKeys[vscoMedia]?.let {
            memoryCache[it]
        }?.let {
            it.bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                outputStream
            )
            outputStream.close()
            return@withContext
        }

        mediaDiskKeys[vscoMedia]?.let {
            diskCache.openSnapshot(it)
        }?.use {
            it.data.toFile().inputStream().use { fis ->
                val buffer = ByteArray(1024*8)
                var n = fis.read(buffer)
                while (n!=-1) {
                    outputStream.write(buffer, 0, n)
                    n = fis.read(buffer)
                }
                outputStream.close()
                return@withContext
            }
        }

        VscoHandler.getMediaBytes(vscoMedia)?.let { bis ->
            val meta = ContentValues().apply {
                put(MediaStore.DownloadColumns.DISPLAY_NAME, vscoMedia)
            }
        }
    }
}
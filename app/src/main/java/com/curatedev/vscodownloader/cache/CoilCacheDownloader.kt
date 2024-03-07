package com.curatedev.vscodownloader.cache

import android.content.Context
import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.curatedev.vscodownloader.vsco.dataclasses.VscoMedia
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoilCacheDownloader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val diskCache = DiskCache.Builder()
        .directory(context.cacheDir)
        .build()
    val memoryCache = MemoryCache.Builder(context)
        .build()
    private val mediaMemoryKeys = hashMapOf<String, MemoryCache.Key>()
    private val mediaDiskKeys = hashMapOf<String, String>()

    @OptIn(ExperimentalCoilApi::class)
    fun imageLoaded(vscoMedia: VscoMedia, memoryKey: MemoryCache.Key?, diskKey: String?) {
        if (memoryKey != null) {
            mediaMemoryKeys[vscoMedia.id] = memoryKey
        }
        if (diskKey != null) {
            mediaDiskKeys[vscoMedia.id] = diskKey
        }
    }
}
fun InputStream.transferToOutputStream(outputStream: OutputStream) {
    val buffer = ByteArray(1024*8)
    var n = this.read(buffer)
    while (n!=-1) {
        outputStream.write(buffer, 0, n)
        n = this.read(buffer)
    }
    this.close()
    outputStream.close()
}
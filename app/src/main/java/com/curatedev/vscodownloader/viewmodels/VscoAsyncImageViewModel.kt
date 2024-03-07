package com.curatedev.vscodownloader.viewmodels

import androidx.lifecycle.ViewModel
import coil.memory.MemoryCache
import com.curatedev.vscodownloader.cache.CoilCacheDownloader
import com.curatedev.vscodownloader.vsco.dataclasses.VscoMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class VscoAsyncImageViewModel @Inject constructor(
    private val coilCacheDownloader: CoilCacheDownloader
): ViewModel() {
    val memoryCache = coilCacheDownloader.memoryCache
    val diskCache = coilCacheDownloader.diskCache
    fun imageLoaded(vscoMedia: VscoMedia, memoryKey: MemoryCache.Key?, diskKey: String?) = coilCacheDownloader.imageLoaded(vscoMedia, memoryKey, diskKey)
}
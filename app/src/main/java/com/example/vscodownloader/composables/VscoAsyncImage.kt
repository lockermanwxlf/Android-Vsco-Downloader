package com.example.vscodownloader.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import com.example.vscodownloader.cache.CoilCacheDownloader
import com.example.vscodownloader.vsco.dataclasses.VscoMedia

@Composable
fun VscoAsyncImage(
    vscoMedia: VscoMedia,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val context = LocalContext.current
    var loaded by rememberSaveable {
        mutableStateOf(false)
    }
    Box(contentAlignment = Alignment.Center) {
        AsyncImage(
            model = vscoMedia.downloadUri,
            contentDescription = contentDescription,
            modifier = Modifier,
            contentScale = contentScale,
            onSuccess = {
                loaded = true
                CoilCacheDownloader.imageLoaded(
                    vscoMedia,
                    it.result.memoryCacheKey,
                    it.result.diskCacheKey
                )
            },
            imageLoader = ImageLoader.Builder(context)
                .memoryCache(CoilCacheDownloader.memoryCache)
                .diskCache(CoilCacheDownloader.diskCache)
                .build()
        )
        if (!loaded){
            CircularProgressIndicator()
        }
    }
}
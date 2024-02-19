package com.example.vscodownloader.vsco

import com.example.vscodownloader.vsco.gson.VscoGson
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

object VscoHandler {
    val client = OkHttpClient()
    val gson = Gson()
    val headers = Headers.headersOf(
        "authority", "vsco.co",
        "accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
        "accept-language", "en-US,en;q=0.9",
        "authorization", "Bearer 7356455548d0a1d886db010883388d08be84d0c9",
        "content-type", "application/json",
        "cache-control", "no-cache",
        "dnt", "1",
        "pragma", "no-cache",
        "sec-ch-ua", "^^",
        "sec-ch-ua-mobile", "?0",
        "sec-fetch-dest", "document",
        "sec-fetch-mode", "navigate",
        "sec-fetch-site", "none",
        "sec-fetch-user", "?1",
        "upgrade-insecure-requests", "1",
        "user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
    )

    suspend fun search(username: String) = withContext(Dispatchers.IO) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("vsco.co")
            .addPathSegments("api/2.0/search/grids")
            .addQueryParameter("query", username)
            .addQueryParameter("page", "0")
            .addQueryParameter("size", "7")
            .build()
        val request = Request.Builder()
            .headers(headers)
            .url(url)
            .build()
        val response = client.newCall(request).execute()
        return@withContext response.body?.string()?.let {
            gson.fromJson(it, VscoGson.GridsResult::class.java).results.map { it.toVscoProfile() }
        }
    }

    suspend fun getMedia(siteId: Long, cursor: String? = null) = withContext(Dispatchers.IO) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("vsco.co")
            .addPathSegments("api/3.0/medias/profile")
            .addQueryParameter("site_id", siteId.toString())
            .addQueryParameter("limit", "100")
            .let { builder ->
                cursor?.let { builder.addQueryParameter("cursor", it) }?: builder
            }
            .build()
        val request = Request.Builder()
            .headers(headers)
            .url(url)
            .build()
        val response = client.newCall(request).execute()
        return@withContext response.body?.string()?.let {
            gson.fromJson(it, VscoGson.MediaResult::class.java).media.map { it.toVscoMedia() }
        }
    }

}
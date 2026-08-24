package com.cogsworth.records.data

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.HttpException

class DiscogsRepository(context: Context) {
    val credentials = CredentialStore(context)

    private fun api(): DiscogsApi {
        val token = requireNotNull(credentials.token())
        val client = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            chain.proceed(chain.request().newBuilder()
                .header("Authorization", "Discogs token=$token")
                .header("User-Agent", "CogsworthAndroid/0.1")
                .build())
        }).build()
        return Retrofit.Builder().baseUrl("https://api.discogs.com/")
            .client(client).addConverterFactory(GsonConverterFactory.create()).build()
            .create(DiscogsApi::class.java)
    }

    suspend fun folders() = api().folders(credentials.username).folders.filter { it.id != 0 }

    suspend fun releases(folderId: Int): List<CollectionItem> {
        val result = mutableListOf<CollectionItem>()
        var page = 1
        do {
            val response = api().releases(credentials.username, folderId, page)
            result += response.releases
            page++
        } while (page <= response.pagination.pages)
        return result
    }

    suspend fun move(item: CollectionItem, destinationFolderId: Int) {
        val response = api().moveRelease(
            credentials.username,
            item.folderId,
            item.id,
            item.instanceId,
            MoveCollectionItemRequest(destinationFolderId)
        )
        if (!response.isSuccessful) throw HttpException(response)
    }
}

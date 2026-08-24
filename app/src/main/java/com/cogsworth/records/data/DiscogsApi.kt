package com.cogsworth.records.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscogsApi {
    @GET("users/{username}/collection/folders")
    suspend fun folders(@Path("username") username: String): FolderResponse

    @GET("users/{username}/collection/folders/{folderId}/releases")
    suspend fun releases(
        @Path("username") username: String,
        @Path("folderId") folderId: Int,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 100
    ): CollectionResponse
}

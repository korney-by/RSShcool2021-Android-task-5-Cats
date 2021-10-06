package com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val API_KEY = "b26ab8aa-7ee7-408e-b653-93164171b8a7"
private const val BASE_URL = "https://api.thecatapi.com"

interface TheCatApiService {
    @Headers("x-api-key:$API_KEY")
    @GET("/v1/images/search")
    suspend fun getCatDataListFromAPI(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE,
        @Query("order") order: String = "asc"
    ): Response<List<CatDto>>

    companion object {
        const val DEFAULT_PAGE_SIZE = 24
    }
}

object TheCatApiImpl {
    val service: TheCatApiService = initialiseRetrofit().create(TheCatApiService::class.java)

    private fun initialiseRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}

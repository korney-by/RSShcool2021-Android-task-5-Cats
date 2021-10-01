package com.korneysoft.rsshcool2021_android_task_5_cats

import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val API_KEY = "b26ab8aa-7ee7-408e-b653-93164171b8a7"
private const val API_KEY_HEADER_NAME = "x-api-key"
private const val API_KEY_QUERY_PARAM_NAME = "api_key"
private const val BASE_URL = "https://api.thecatapi.com"
private const val IMAGES_PATH_URL = "/v1/images/search"

interface TheCatApi {
    // @GET("$IMAGES_PATH_URL?$API_KEY_QUERY_PARAM_NAME=$API_KEY")
    @GET("/v1/images/search?limit=24&api_key=b26ab8aa-7ee7-408e-b653-93164171b8a7")
    suspend fun getListOfCatsData(): List<CatData>
}

object TheCatApiImpl {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val theCatService = retrofit.create(TheCatApi::class.java)

    suspend fun getListOfCats(): List<Cat> {
        return withContext(Dispatchers.IO) {
            theCatService.getListOfCatsData()
                .map { catData ->
                    Cat(catData.id, catData.url, catData.width, catData.height)
                }
        }
    }
}

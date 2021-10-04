package com.korneysoft.rsshcool2021_android_task_5_cats

import androidx.annotation.IntRange
import com.korneysoft.rsshcool2021_android_task_5_cats.data.RepositoryInterface
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.CatsResponseDto
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.TimeoutInterceptor
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.TimeoutListener
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.toCat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


private const val API_KEY = "b26ab8aa-7ee7-408e-b653-93164171b8a7"
private const val API_KEY_HEADER_NAME = "x-api-key"
private const val API_KEY_QUERY_PARAM_NAME = "api_key"
private const val BASE_URL = "https://api.thecatapi.com"
private const val IMAGES_PATH_URL = "/v1/images/search"

interface TheCatApiService {
    // @GET("$IMAGES_PATH_URL?$API_KEY_QUERY_PARAM_NAME=$API_KEY")
    //@GET("/v1/images/search?limit=24&api_key=b26ab8aa-7ee7-408e-b653-93164171b8a7")
    @GET("/v1/images/search")
    suspend fun getCatDataListFromAPI(
        @Query("page") @IntRange(from = 1) page: Int = 1,
        @Query("limit") @IntRange(
            from = 1,
            to = MAX_PAGE_SIZE.toLong()
        ) pageSize: Int = DEFAULT_PAGE_SIZE,
        @Query("x-api-key") query: String = API_KEY
    ): Response<CatsResponseDto>

    companion object {
        const val DEFAULT_PAGE_SIZE = 12
        const val MAX_PAGE_SIZE = 50
    }
}

object TheCatApiImpl {
    private val retrofit by lazy { initialiseRetrofit() }
    val service: TheCatApiService = retrofit.create(TheCatApiService::class.java)

    private var timeoutListener: TimeoutListener? = null
    private fun initialiseRetrofit(): Retrofit {

        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(TimeoutInterceptor(timeoutListener))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }



    override fun setTimeoutListener(timeoutListener: TimeoutListener) {
        this.timeoutListener = timeoutListener
    }

//    private suspend fun getListOfCats(): List<Cat> {
//        return withContext(Dispatchers.IO) {
//            theCatService.getCatDataListFromAPI().body()?.catsDto?.let { catsDto ->
//                catsDto.map { it.toCat() }
//            }
//        } ?: emptyList()
//    }

//    override suspend fun getCatList(): List<Cat> {
//        return getListOfCats()
//    }


}

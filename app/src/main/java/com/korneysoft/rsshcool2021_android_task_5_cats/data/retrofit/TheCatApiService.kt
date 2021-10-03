package com.korneysoft.rsshcool2021_android_task_5_cats

import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.data.RepositoryInterface
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.TimeoutInterceptor
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.TimeoutListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import retrofit2.http.Query
import androidx.annotation.IntRange
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.CatData


private const val API_KEY = "b26ab8aa-7ee7-408e-b653-93164171b8a7"
private const val API_KEY_HEADER_NAME = "x-api-key"
private const val API_KEY_QUERY_PARAM_NAME = "api_key"
private const val BASE_URL = "https://api.thecatapi.com"
private const val IMAGES_PATH_URL = "/v1/images/search"

interface TheCatApiService {
    // @GET("$IMAGES_PATH_URL?$API_KEY_QUERY_PARAM_NAME=$API_KEY")
    //@GET("/v1/images/search?limit=24&api_key=b26ab8aa-7ee7-408e-b653-93164171b8a7")
    @GET("/v1/images/search")
    suspend fun getListOfCatsData(
        @Query("page") @IntRange(from=1) page:Int=1,
        @Query("limit") @IntRange(from = 1, to = MAX_PAGE_SIZE.toLong()) pageSize: Int = DEFAULT_PAGE_SIZE,
        @Query("x-api-key") query: String=API_KEY
    ): Response<CatData>

    companion object {
        const val DEFAULT_PAGE_SIZE = 12
        const val MAX_PAGE_SIZE = 50
    }

}


object TheCatApiImpl : RepositoryInterface {
    private val retrofit by lazy { initialiseRetrofit() }
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

    private val theCatService = retrofit.create(TheCatApiService::class.java)

    override fun setTimeoutListener(timeoutListener: TimeoutListener) {
        this.timeoutListener = timeoutListener
    }

    private suspend fun getListOfCats(): List<Cat> {
        return withContext(Dispatchers.IO) {
            theCatService.getListOfCatsData()
                .map { catData ->
                    catData.toCat()
                }
        }
    }

    override suspend fun getCatList(): List<Cat> {
        return getListOfCats()
    }


}

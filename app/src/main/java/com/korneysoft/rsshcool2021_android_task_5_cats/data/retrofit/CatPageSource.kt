package com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.korneysoft.rsshcool2021_android_task_5_cats.TheCatApiService
import retrofit2.HttpException

class CatPageSource(
    val apiService: TheCatApiService
) : PagingSource<Int, Cat>() {

    override fun getRefreshKey(state: PagingState<Int, Cat>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cat> {
        val pageNumber = params.key ?: INITIAL_PAGE_NUMBER
        val pageSize = params.loadSize.coerceAtMost(TheCatApiService.MAX_PAGE_SIZE)
        val response = apiService.getCatDataListFromAPI(pageNumber, pageSize)
        try {
            if (response.isSuccessful) {
                val cats = response.body()!!.catsDto.map { it.toCat() }
                val nextPage = if (cats.size < pageSize) null else pageNumber + 1
                val prevPage = if (pageNumber == 1) null else pageNumber - 1
                return LoadResult.Page(cats, prevPage, nextPage)
            } else {
                return LoadResult.Error(HttpException(response))
            }
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    companion object {
        const val INITIAL_PAGE_NUMBER = 1
    }
}

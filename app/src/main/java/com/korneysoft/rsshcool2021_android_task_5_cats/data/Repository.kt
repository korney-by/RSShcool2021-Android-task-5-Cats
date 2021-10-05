package com.korneysoft.rsshcool2021_android_task_5_cats.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.korneysoft.rsshcool2021_android_task_5_cats.TheCatApiImpl
import com.korneysoft.rsshcool2021_android_task_5_cats.TheCatApiService
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.CatPagingSource

class Repository : RepositoryInterface {
    private val service: TheCatApiService = TheCatApiImpl.service

    override fun getDataPager(): Pager<Int, Cat> {
        val differentPageSize = (20..35).random()
        return Pager(config = PagingConfig(pageSize = differentPageSize),
            pagingSourceFactory = { CatPagingSource(service, differentPageSize) })
    }

    companion object {
        private var INSTANCE: Repository? = null

        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = Repository()
            }
        }

        fun get(): Repository {
            return INSTANCE ?: throw IllegalStateException("ItemListRepository must be initialised")
        }
    }
}

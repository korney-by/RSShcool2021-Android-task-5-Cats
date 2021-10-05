package com.korneysoft.rsshcool2021_android_task_5_cats.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.korneysoft.rsshcool2021_android_task_5_cats.TheCatApiImpl
import com.korneysoft.rsshcool2021_android_task_5_cats.TheCatApiService
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.CatPagingSource

class Repository : RepositoryInterface {
    private val service: TheCatApiService = TheCatApiImpl.service

    private val pager: Pager<Int, Cat> by lazy { initDataPager() }
    override fun getDataPager(): Pager<Int, Cat> = pager

    private fun initDataPager(): Pager<Int, Cat> {
        val differentPageSize = 24// (20..35).random()
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

package com.korneysoft.rsshcool2021_android_task_5_cats.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.CatPagingSource
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.TheCatApiImpl
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.TheCatApiService

class Repository : RepositoryInterface {
    private val service: TheCatApiService = TheCatApiImpl.service

    private val pager: Pager<Int, Cat> by lazy { initDataPager() }
    override fun getDataPager(): Pager<Int, Cat> = pager

    private fun initDataPager(): Pager<Int, Cat> {

        return Pager(config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { CatPagingSource(service, PAGE_SIZE) })
    }

    companion object {
        private var INSTANCE: Repository? = null
        private var PAGE_SIZE = 24

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

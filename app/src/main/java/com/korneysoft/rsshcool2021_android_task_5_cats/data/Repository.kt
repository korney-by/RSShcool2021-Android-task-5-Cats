package com.korneysoft.rsshcool2021_android_task_5_cats.data

import com.korneysoft.rsshcool2021_android_task_5_cats.TheCatApiImpl
import com.korneysoft.rsshcool2021_android_task_5_cats.TheCatApiService
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.TimeoutListener

class Repository : RepositoryInterface {

    override val service: TheCatApiService = TheCatApiImpl.service

    override fun setTimeoutListener(timeoutListener: TimeoutListener) {
        TheCatApiImpl.setTimeoutListener(timeoutListener)
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

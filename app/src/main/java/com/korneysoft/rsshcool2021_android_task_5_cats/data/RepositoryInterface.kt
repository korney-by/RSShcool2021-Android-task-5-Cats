package com.korneysoft.rsshcool2021_android_task_5_cats.data

import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.TimeoutListener

interface RepositoryInterface {
    suspend fun getCatList():List<Cat>
    fun setTimeoutListener(timeoutListener: TimeoutListener)
}

package com.korneysoft.rsshcool2021_android_task_5_cats.data

import androidx.paging.Pager
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.Cat

interface RepositoryInterface {
    fun getDataPager() : Pager<Int, Cat>
}

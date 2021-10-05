package com.korneysoft.rsshcool2021_android_task_5_cats.data

import androidx.paging.Pager

interface RepositoryInterface {
    fun getDataPager() : Pager<Int, Cat>
}

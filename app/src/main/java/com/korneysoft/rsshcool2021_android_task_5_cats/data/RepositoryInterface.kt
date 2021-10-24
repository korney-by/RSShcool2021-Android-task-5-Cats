package com.korneysoft.rsshcool2021_android_task_5_cats.data

import androidx.paging.Pager

//TODO Naming - это и так interface
interface RepositoryInterface {

    fun getDataPager(): Pager<Int, Cat>
}

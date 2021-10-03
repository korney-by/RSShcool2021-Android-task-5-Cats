package com.korneysoft.rsshcool2021_android_task_5_cats.interfaces

import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.Cat

interface SaveImageInterface {
    fun saveImage(cat: Cat?)
}

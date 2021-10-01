package com.korneysoft.rsshcool2021_android_task_5_cats.data

data class Cat(
    val id: String,
    val imageUrl: String?,
    val width: Int?,
    val height: Int?,
    var isReady: Boolean = false
)

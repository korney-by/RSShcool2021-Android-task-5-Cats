package com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit

data class CatList(
    val results: List<Cat>
)

data class Cat(
    val id: String,
    val imageUrl: String?,
    val width: Int?,
    val height: Int?
)

data class PageInfo(
    val count: Int?,
    val pages: String?,
    val next: String?,
    val prev: String?
)



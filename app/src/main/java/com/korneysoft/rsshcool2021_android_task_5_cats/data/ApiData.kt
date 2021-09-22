package com.korneysoft.rsshcool2021_android_task_5_cats.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiData(
    @Json(name = "") val catsData: List<CatData>
)

@JsonClass(generateAdapter = true)
data class CatData(
    @Json(name = "id") val id: String,
    @Json(name = "url") val url: String?,
    @Json(name = "width") val width: String?,
    @Json(name = "height") val height: String?
)


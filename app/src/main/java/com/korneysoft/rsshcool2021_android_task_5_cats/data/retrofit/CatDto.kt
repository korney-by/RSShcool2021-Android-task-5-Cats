package com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CatDto(
    @Json(name = "id") val id: String,
    @Json(name = "url") val url: String?,
    @Json(name = "width") val width: Int?,
    @Json(name = "height") val height: Int?
)

internal fun CatDto.toCat(): Cat {
    return Cat(
        id = this.id,
        imageUrl = this.url,
        width = this.width,
        height = this.height
    )
}

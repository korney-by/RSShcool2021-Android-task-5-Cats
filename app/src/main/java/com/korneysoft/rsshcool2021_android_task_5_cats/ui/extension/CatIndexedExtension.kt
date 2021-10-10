package com.korneysoft.rsshcool2021_android_task_5_cats.ui.extension

import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatIndexed

fun CatIndexed.getFlipCardName(): String {
    return "flip-$id"
}

fun CatIndexed.getFilename(): String {
    imageUrl?.let {
        return imageUrl.substringAfterLast("/")
    }
    return ""
}

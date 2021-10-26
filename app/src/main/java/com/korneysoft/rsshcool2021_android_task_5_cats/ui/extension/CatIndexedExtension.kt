package com.korneysoft.rsshcool2021_android_task_5_cats.ui.extension

import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatIndexed

//TODO убрать CatIndexed
fun CatIndexed.getFlipCardName(): String {
    return "flip-$id"
}

//TODO убрать CatIndexed
fun CatIndexed.getFilename(): String {
    imageUrl?.let {
        return imageUrl.substringAfterLast("/")
    }
    return ""
}

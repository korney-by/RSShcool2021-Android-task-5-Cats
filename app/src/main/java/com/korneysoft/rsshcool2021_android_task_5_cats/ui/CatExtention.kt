package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatIndexed

fun CatIndexed.getFlipCardName():String{
    return "flip-%s".format(id)
}

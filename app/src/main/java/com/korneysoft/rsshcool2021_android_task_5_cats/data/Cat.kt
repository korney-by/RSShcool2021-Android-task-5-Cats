package com.korneysoft.rsshcool2021_android_task_5_cats.data

data class Cat(
    val id: String,
    val imageUrl: String?,
    val width: Int?,
    val height: Int?
) {
    override fun toString(): String {
        return "id=$id"
    }
//TODO сложная логика использования, замена алгоритма на более простой
    fun toCatIndexed(index: Int): CatIndexed {
        return CatIndexed(
            index = index,
            id = this.id,
            imageUrl = this.imageUrl,
            width = this.width,
            height = this.height
        )
    }
}

//TODO сложная логика использования, замена алгоритма на более простой
data class CatIndexed(
    val index: Int,
    val id: String,
    val imageUrl: String?,
    val width: Int?,
    val height: Int?
) {
    override fun toString(): String {
        return "id=$id, index=$index"
    }

    fun toCat(): Cat {
        return Cat(
            id = this.id,
            imageUrl = this.imageUrl,
            width = this.width,
            height = this.height
        )
    }
}

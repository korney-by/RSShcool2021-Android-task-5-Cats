package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

interface OnLayoutCompleteCallback {
    fun onLayoutComplete()
}

class NotifyingGridLayoutManager(context: Context?, columnCount: Int) :
    GridLayoutManager(context, columnCount) {
    var onLayoutCompleteCallback: OnLayoutCompleteCallback? = null

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        onLayoutCompleteCallback?.onLayoutComplete()
    }

    fun isLastItemCompletelyVisible() = findLastCompletelyVisibleItemPosition() == itemCount - 1
}

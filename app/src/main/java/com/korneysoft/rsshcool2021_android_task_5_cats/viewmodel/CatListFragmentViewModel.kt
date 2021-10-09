package com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korneysoft.rsshcool2021_android_task_5_cats.ui.CatListRecyclerViewAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CatListFragmentViewModel() : ViewModel() {

    private var _isInitialised = false
    val isNotInitialised get() = !_isInitialised

    private var adapter: CatListRecyclerViewAdapter? = null
    private var mainViewModel: CatViewModel? = null

    fun initViewModel(adapter: CatListRecyclerViewAdapter?, mainViewModel: CatViewModel?) {
        if (adapter != null && mainViewModel != null) {
            this.adapter ?: run { this.adapter = adapter }
            this.mainViewModel ?: run { this.mainViewModel = mainViewModel }
            startCollectItems()
            _isInitialised = true
        }
    }

    private fun startCollectItems() {
        viewModelScope.launch {
            mainViewModel?.getListData()?.collectLatest {
                adapter?.submitData(it)
            }
        }
    }
}

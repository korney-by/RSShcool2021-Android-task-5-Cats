package com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korneysoft.rsshcool2021_android_task_5_cats.TheCatApiImpl
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import kotlinx.coroutines.launch

class CatViewModel : ViewModel() {
    private var _items = MutableLiveData<List<Cat>>()
    val items: LiveData<List<Cat>> get() = _items

    private val showingCat = MutableLiveData<Int?>()
    var lastShowingCat: Int? = null

    var firstGridVisiblePosition: Int = -1
    var lastGridVisiblePosition: Int = -1

    init {
        showingCat.value = null
        viewModelScope.launch {
            _items.value = TheCatApiImpl.getListOfCats()
        }
    }

    fun setShowingCat(index: Int?) {
        showingCat.value = index
        lastShowingCat = null
    }

    fun getPositionShowingCat(): LiveData<Int?> = showingCat
}
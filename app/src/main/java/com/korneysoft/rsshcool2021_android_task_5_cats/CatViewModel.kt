package com.korneysoft.rsshcool2021_android_task_5_cats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import kotlinx.coroutines.launch

class CatViewModel : ViewModel() {
    private var _items = MutableLiveData<List<Cat>>()
    val items: LiveData<List<Cat>> get() = _items

    init {
        viewModelScope.launch {
            _items.value=TheCatApiImpl.getListOfCats()
        }
    }
}
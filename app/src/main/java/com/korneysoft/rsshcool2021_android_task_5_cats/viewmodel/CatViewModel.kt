package com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.korneysoft.rsshcool2021_android_task_5_cats.TheCatApiImpl
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import kotlinx.coroutines.launch

class CatViewModel : ViewModel() {
    private var _items = MutableLiveData<List<Cat>>()
    val items: LiveData<List<Cat>> get() = _items

    private val _showingCat = MutableLiveData<String?>()

    init {
        _showingCat.value=null
        viewModelScope.launch {
            _items.value = TheCatApiImpl.getListOfCats()
        }
    }

    fun setUrlShowingCat(url:String?){
        _showingCat.value=url
    }

    fun getUrlShowingCat():LiveData<String?> = _showingCat
}
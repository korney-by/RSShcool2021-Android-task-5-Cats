package com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatIndexed
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Repository
import com.korneysoft.rsshcool2021_android_task_5_cats.internet.isInternetAvailable
import kotlinx.coroutines.flow.Flow

private const val TAG = "T5-CatViewModel"

class CatViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext
    private val repository by lazy { Repository.get() }

    private val _showingCat = MutableLiveData<CatIndexed?>()
    val showingCat: LiveData<CatIndexed?> get() = _showingCat
    var lastShowingCat: CatIndexed? = null

    val _isOnline = MutableLiveData<Boolean>(true)
    val isOnline: LiveData<Boolean> get() = _isOnline

    val _downloadUrl = MutableLiveData<String?>(null)
    val downloadUrl: LiveData<String?> get() = _downloadUrl

    init {
        Repository.initialize()
        _showingCat.value = null
    }

    fun getListData(): Flow<PagingData<Cat>> {
        return repository.getDataPager().flow.cachedIn(viewModelScope)
    }

    fun setShowingCat(catIndexed: CatIndexed?) {
        _showingCat.value = catIndexed
    }

    fun getShownCat(): LiveData<CatIndexed?> = showingCat

    fun startDownload(cat: Cat?) {
        cat?.imageUrl?.let { url ->
            _downloadUrl.value = url
        }
    }

    fun checkOnlineState(): Boolean {
        val isOnlineValue = isInternetAvailable(context)
        _isOnline.value = isOnlineValue
        return isOnlineValue
    }
}

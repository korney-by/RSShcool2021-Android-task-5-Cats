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

class MainViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")

    //TODO возможная утечка памяти, ViewModel не должна хранить в себе контекст
    private val context: Context = application.applicationContext

    //TODO должен передоваться как параметр при создании ViewModel (требуется ViewModelFactory либо DI)
    private val repository by lazy { Repository.get() }

    private val _showingCat = MutableLiveData<CatIndexed?>()
    private val showingCat: LiveData<CatIndexed?> get() = _showingCat

    //TODO лучше избигать nullability, текущая логика с CatIndexed и Cat слишком сложная, возможна реализация без CatIndexed
    var lastShowingCat: CatIndexed? = null

    private val _isOnline = MutableLiveData(true)
    val isOnline: LiveData<Boolean> get() = _isOnline

    private val _downloadUrl = MutableLiveData<String?>(null)
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

    //TODO прокидывается из активности cat, приходит сюда, чтобы вернуться обратно в активность через liveData- слишком сложная логика, запутывает код
    fun startDownload(cat: Cat?) {
        if (checkOnlineState()) {
            cat?.imageUrl?.let { url ->
                _downloadUrl.value = url
            }
        }
    }

    fun checkOnlineState(): Boolean {
        val isOnlineValue = isInternetAvailable(context)
        if (_isOnline.value != isOnlineValue) {
            _isOnline.value = isOnlineValue
        }
        return isOnlineValue
    }
}

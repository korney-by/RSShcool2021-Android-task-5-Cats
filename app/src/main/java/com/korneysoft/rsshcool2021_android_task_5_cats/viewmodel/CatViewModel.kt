package com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatIndexed
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Repository
import com.korneysoft.rsshcool2021_android_task_5_cats.internet_utils.isInternetAvailable
import com.korneysoft.rsshcool2021_android_task_5_cats.ui.CatListFragment
import kotlinx.coroutines.flow.Flow


private const val TAG = "T5-CatViewModel"

class CatViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext
    private val repository by lazy { Repository.get() }

    private val _showingCat = MutableLiveData<CatIndexed?>()
    val showingCat: LiveData<CatIndexed?> get() = _showingCat
    var lastShowingCat: CatIndexed? = null

    var firstGridVisiblePosition: Int = -1
    var lastGridVisiblePosition: Int = -1

    val _isOnline = MutableLiveData<Boolean>(true)
    val isOnline: LiveData<Boolean> get() = _isOnline

    private val catIndexMap = mutableMapOf<Int, String>()

    private var _getGridFragment: (() -> CatListFragment?)? = null
    val getGridFragment get() = _getGridFragment?.invoke()

    init {
        Repository.initialize()
        _showingCat.value = null
    }


    suspend fun getListData(): Flow<PagingData<Cat>> {
        return repository.getDataPager().flow.cachedIn(viewModelScope)
    }


    fun setShowingCat(catIndexed: CatIndexed?, getGridFragment: () -> CatListFragment?) {
        _getGridFragment = getGridFragment
        _showingCat.value = catIndexed
    }

    fun getShownCat(): LiveData<CatIndexed?> = showingCat

//    fun getCatFromPosition(position: Int): Cat? {
//        return null
//        // return _items.value?.get(position)
//    }

    fun checkOnlineState(): Boolean {
        val isOnlineValue = isInternetAvailable(context)
        if (isOnlineValue && isOnlineValue != _isOnline.value) {
            //updateData()
        }
        _isOnline.value = isOnlineValue
        return isOnlineValue
    }

    fun getUrl(position: Int): String? {
        return if (catIndexMap.containsKey(position)) {
            catIndexMap.getValue(position)
        } else null
    }

    fun toRememberUrl(position: Int, url: String?) {
        url ?: return
        var tag = Uri.parse(url).getLastPathSegment()!!
        tag=tag.substring(0, tag.lastIndexOf('.'))
        if (!catIndexMap.containsKey(position)) catIndexMap.put(position, tag)
    //        if (!catIndexMap.containsKey(position)) catIndexMap.put(position, url)
    }

}

package com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Repository
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.internet_utils.isInternetAvailable
import com.korneysoft.rsshcool2021_android_task_5_cats.ui.CatListFragment
import kotlinx.coroutines.flow.Flow


private const val TAG = "T5-CatViewModel"

class CatViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext
    private val repository by lazy { Repository.get() }

    private val _showingCat = MutableLiveData<Int?>()
    val showingCat: LiveData<Int?> get() = _showingCat
    var lastShowingCat: Int? = null

    var firstGridVisiblePosition: Int = -1
    var lastGridVisiblePosition: Int = -1

    val _isOnline = MutableLiveData<Boolean>(true)
    val isOnline: LiveData<Boolean> get() = _isOnline

    private var _getGridFragment: (() -> CatListFragment?)? = null
    val getGridFragment get() = _getGridFragment?.invoke()

    init {
        Repository.initialize()
        _showingCat.value = null
    }


    suspend fun getListData(): Flow<PagingData<Cat>> {
        return repository.getDataPager().flow.cachedIn(viewModelScope)
    }

//    private fun updateData() {
//        // kick LiveData
//        if (_items.value == null) {
//            getData()
//        } else {
//            _items.value = _items.value?.toList()
//        }
//    }

//    fun loadImagesToCash(catList: List<Cat>) {
//        //load images to cash in coroutine
//        val firstVisiblePosition = firstGridVisiblePosition
//        val lastVisiblePosition = lastGridVisiblePosition
//        viewModelScope.launch {
//            catList.forEach() {
//                if (it.width != null && it.height != null)
//                if {
//                    Glide.with(context)
//                        .load(it.imageUrl)
//                        .submit(it.width, it.height)
//                    //.preload()
////                    .diskCacheStrategy(DiskCacheStrategy.ALL))
////                .apply(requestOptions)
////                .submit();
//                    //.downloadOnly(500, 500);
//                }
//            }
//        }
//
//    }

    fun setShowingCat(index: Int?, getGridFragment: () -> CatListFragment?) {
        _getGridFragment = getGridFragment
        _showingCat.value = index
    }

    fun getPositionShowingCat(): LiveData<Int?> = showingCat

    fun getCatFromPosition(position: Int): Cat? {
        return null
        // return _items.value?.get(position)
    }

    fun checkOnlineState(): Boolean {
        val isOnlineValue = isInternetAvailable(context)
        if (isOnlineValue && isOnlineValue != _isOnline.value) {
            //updateData()
        }
        _isOnline.value = isOnlineValue
        return isOnlineValue
    }
}

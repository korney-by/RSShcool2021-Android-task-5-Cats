package com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Repository
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.TimeoutListener
import com.korneysoft.rsshcool2021_android_task_5_cats.ui.CatListFragment
import com.korneysoft.rsshcool2021_android_task_5_cats.internet_utils.isInternetAvailable
import kotlinx.coroutines.launch

private const val TAG = "T5-CatViewModel"

class CatViewModel(application: Application) : AndroidViewModel(application), TimeoutListener {

    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext
    private val repository by lazy { Repository.get() }

    private val _items = MutableLiveData<List<Cat>>()
    val items: LiveData<List<Cat>> get() = _items

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
        repository.setTimeoutListener(this)
        _showingCat.value = null
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            if (checkOnlineState()) {
                _items.value = repository.getCatList()
            }
        }
    }

    private fun updateData() {
        // kick LiveData
        if (_items.value == null) {
            getData()
        } else {
            _items.value = _items.value?.toList()
        }
    }

    override fun onConnectionTimeout() {
        Log.d(TAG, "onConnectionTimeout")
        checkOnlineState()
    }

    fun setShowingCat(index: Int?, getGridFragment: () -> CatListFragment?) {
        _getGridFragment = getGridFragment
        _showingCat.value = index
    }

    fun getPositionShowingCat(): LiveData<Int?> = showingCat

    fun getCatFromPosition(position: Int): Cat? {
        return _items.value?.get(position)
    }

    fun checkOnlineState(): Boolean {
        val isOnlineValue = isInternetAvailable(context)
        if (isOnlineValue && isOnlineValue != _isOnline.value) {
            updateData()
        }
        _isOnline.value = isOnlineValue
        return isOnlineValue
    }
}

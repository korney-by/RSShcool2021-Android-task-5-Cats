package com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korneysoft.rsshcool2021_android_task_5_cats.ui.CatListRecyclerViewAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//TODO не требуется разделять по типу вьюмодели от места их использования, усложняет поиск компонентов, которые взаимодействуют между собой
class CatListFragmentViewModel : ViewModel() {

    private var _isInitialised = false
    val isNotInitialised get() = !_isInitialised

    //TODO не должен храниться в ViewModel, т.к. может содержать в себе контекст
    private var adapter: CatListRecyclerViewAdapter? = null

    //TODO не должны содержать ViewModel в ViewModel, логика должна быть организована независимо
    private var mainViewModel: MainViewModel? = null

    fun initViewModel(adapter: CatListRecyclerViewAdapter?, mainViewModel: MainViewModel?) {
        if (adapter != null && mainViewModel != null) {
            this.adapter ?: run { this.adapter = adapter }
            this.mainViewModel ?: run { this.mainViewModel = mainViewModel }
            startCollectItems()
            _isInitialised = true
        }
    }

    private fun startCollectItems() {
        viewModelScope.launch {
            //TODO бесконечный цикл - ошибка, лучше такие вещи как проверка статуса соединения и т.п.
            // системные вещи хранить в сервисах
            while (true) {
                //TODO не безопасное использование nullable type ?
                try {
                    mainViewModel?.getListData()?.collectLatest {
                        adapter?.submitData(it)
                    }
                } catch (e: Exception) {
                    mainViewModel?.checkOnlineState() // connect failed
                }
                // wait 500 ms and check connect again
                delay(500)
                mainViewModel?.checkOnlineState()
            }
        }
    }
}

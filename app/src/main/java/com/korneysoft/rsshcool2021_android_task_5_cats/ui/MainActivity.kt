package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.ActivityMainBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel

class MainActivity : AppCompatActivity(), NavigationBarColor {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CatViewModel by viewModels()
    private val screenSettings by lazy { ScreenSettings() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO("NIGHT Theme")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerObserverShowingCat()
        loadCatListFragment()

    }

    private fun registerObserverShowingCat() {
        viewModel.getUrlShowingCat().observe(this,
            Observer {
                it ?: return@Observer
                loadCatDetailsFragment(it)
            })
    }

    private fun loadCatListFragment() {
        val fragment: Fragment =
            CatListFragment.newInstance(screenSettings.columnCount, screenSettings.cellSize)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    private fun loadCatDetailsFragment(photoUrl: String) {
        val fragment: Fragment = CatDetailsFragment.newInstance(photoUrl)
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            viewModel.setUrlShowingCat(null)
        super.onBackPressed()
    }

    override fun setNavigationBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryColor)
    }

    inner class ScreenSettings() {
        private var _width: Int = 0
        private var _height: Int = 0
        private var _columnCount: Int = 0
        private var _cellSize: Int = 0

        val columnCount get() = _columnCount
        val cellSize get() = _cellSize

        init {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                _width = this@MainActivity.windowManager.currentWindowMetrics.bounds.width()
                _height = this@MainActivity.windowManager.currentWindowMetrics.bounds.height()
            } else {
                val displayMetrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                this@MainActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                _width = displayMetrics.widthPixels
                _height = displayMetrics.heightPixels
            }

            if (_height > _width) {
                _columnCount = 2
            } else {
                _columnCount = (_width / (_height / 2)).toInt()
            }
            _cellSize = _width / _columnCount
        }
    }

}
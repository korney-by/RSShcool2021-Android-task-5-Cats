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
    private val gridSettings by lazy { GridSettings() }

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
        viewModel.getPositionShowingCat().observe(this,
            Observer {
                it ?: return@Observer
                     loadCatDetailsFragment()
            })
    }

    private fun isCatDetailsFragmentHide(): Boolean {
        return (supportFragmentManager.findFragmentById(R.id.cat_details_fragment)==null)
    }


    private fun loadCatListFragment() {
        val fragment: Fragment =
            CatListFragment.newInstance(gridSettings.columnCount, gridSettings.cellSize)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()

    }

    private fun loadCatDetailsFragment() {
        val fragment: Fragment = CatDetailsFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0)
            viewModel.setShowingCat(null)
    }

    override fun setNavigationBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryColor)
    }

    inner class GridSettings() {
        val columnCount: Int
        val cellSize: Int

        init {
            val width: Int
            val height: Int

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                width = this@MainActivity.windowManager.currentWindowMetrics.bounds.width()
                height = this@MainActivity.windowManager.currentWindowMetrics.bounds.height()
            } else {
                val displayMetrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                this@MainActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                width = displayMetrics.widthPixels
                height = displayMetrics.heightPixels
            }

            if (height > width) {
                columnCount = 2
            } else {
                columnCount = (width / (height / 2)).toInt()
            }
            cellSize = width / columnCount
        }
    }

}
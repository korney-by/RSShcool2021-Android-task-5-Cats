package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),NavigationBarColor {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO("NIGHT Theme")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: CatViewModel by viewModels()

        loadCatListFragment()
    }

    private fun loadCatListFragment() {
        val fragment: Fragment = CatListFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    override fun setNavigationBarColor(){
        window.navigationBarColor = ContextCompat.getColor(this,R.color.primaryColor)
    }

}
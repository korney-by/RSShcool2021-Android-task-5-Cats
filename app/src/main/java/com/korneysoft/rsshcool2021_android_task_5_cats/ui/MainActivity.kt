package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.ActivityMainBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.ui.interfaces.ShowFragmentInterface
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel

class MainActivity : AppCompatActivity(), NavigationBarColor, ShowFragmentInterface {
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

    private fun loadCatDetailsFragment(photoUrl:String) {
        val fragment: Fragment = CatDetailsFragment.newInstance(photoUrl)
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }


    override fun setNavigationBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryColor)
    }

    override fun showCatDetailsFragment(photoUrl:String){
        loadCatDetailsFragment(photoUrl)
    }

}
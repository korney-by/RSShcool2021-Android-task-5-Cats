package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.ActivityMainBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel

class MainActivity : AppCompatActivity(), SetNavigationBarColor {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CatViewModel by viewModels()

    private var isRecoveryAfterRotate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO("NIGHT Theme")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerObserverShowingCat()
        if (savedInstanceState == null) {
            loadCatListFragment()
        }
    }

    private fun registerObserverShowingCat() {
        viewModel.getPositionShowingCat().observe(this,
            Observer {
                it ?: return@Observer
                loadCatDetailsFragment(it)
                viewModel.setShowingCat(null)
            })
    }

    private fun isCatDetailsFragmentHide(): Boolean {
        return (supportFragmentManager.findFragmentById(R.id.cat_details_fragment) == null)
    }

    private fun loadCatListFragment() {
        val fragment: Fragment =
            CatListFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    private fun loadCatDetailsFragment(position: Int) {
        viewModel.lastShowingCat = position
//        val fragment: Fragment = CatDetailsFragment.newInstance()
//        supportFragmentManager
//            .beginTransaction()
//            .setReorderingAllowed(true)
//            .addSharedElement(imageView, imageView.getTransitionName())
//            .replace(R.id.fragmentContainerView, fragment,CatDetailsFragment.javaClass.simpleName)
//            .addToBackStack(CatDetailsFragment.javaClass.simpleName)
//            .commit()

//        viewModel.lastShowingCat = position
//        val fragment: Fragment = CatDetailsFragment.newInstance()
//        supportFragmentManager
//            .beginTransaction()
//            .addToBackStack("CatDetailsFragment")
//            .replace(R.id.fragmentContainerView, fragment)
//            .commit()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0)
            viewModel.setShowingCat(null)
    }

    override fun setNavigationBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryColor)
    }
}
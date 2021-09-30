package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Bundle
import android.transition.TransitionSet
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
                loadCatDetailsFragment(it,viewModel.getGridFragment)
                viewModel.setShowingCat(null) { null }
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

    private fun loadCatDetailsFragment(position: Int,sourceFragment:CatListFragment?) {
        sourceFragment ?: return
        viewModel.lastShowingCat = position
        val destinationFragment: Fragment = CatDetailsFragment.newInstance()

        sourceFragment.getSelectedView()?.let { view ->
            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
            sourceFragment.exitTransition?.let { transition ->
                if (transition is TransitionSet) transition.excludeTarget(view, true)
            }
            sourceFragment.parentFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(view, view.transitionName)
                .replace(
                    R.id.fragmentContainerView,
                    destinationFragment,
                    CatDetailsFragment.javaClass.simpleName
                )
                .addToBackStack(CatDetailsFragment.javaClass.simpleName)
                .commit()
        }

    }

    override fun setNavigationBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryColor)
    }
}

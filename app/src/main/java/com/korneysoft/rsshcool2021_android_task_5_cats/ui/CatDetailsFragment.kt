package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatDetailsBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.set


private val TAG = "T5-CatDetailsFragment"

class CatDetailsFragment : Fragment() {
    private var _binding: FragmentCatDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.catViewPager2.adapter = CatDetailsViewPagerAdapter { getCurrentFragment() }

        registerObserverItems()
        showCatAtCurrentPosition()

        binding.catViewPager2.apply {
            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    saveLastShowingCat(position)
                    val cat=viewModel.getCatFromPosition(position)
                    Log.d(TAG,"$cat")
                    Log.d(TAG,binding.catViewPager2.findViewWithTag<View>(cat?.id).toString())
                }
            })
        }

        prepareSharedElementTransition()

        if (savedInstanceState==null){
            postponeEnterTransition()
        }

        return view
    }

    private fun prepareSharedElementTransition() {
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.cat_shared_element_transition)

        setEnterSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String?>, sharedElements: MutableMap<String?, View?>
                ) {
                    getCurrentPosition()?.let { position ->
                        showCatAtCurrentPosition()
                        val view = getView(position)
                        view?.let { view ->
                            // Map the first shared element name to the child ImageView.
                            sharedElements[names[0]] = view
                        }
                    }
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getView(position: Int): View? {
        val cat=viewModel.getCatFromPosition(position)
        return binding.catViewPager2.findViewWithTag(cat?.id)
    }

    private fun getCurrentFragment(): Fragment = this

    private fun saveLastShowingCat(position: Int) {
        viewModel.lastShowingCat = position
    }

    private fun showCatAtCurrentPosition() {
        getCurrentPosition()?.let { position ->
            binding.catViewPager2.setCurrentItem(position, false)
        }
    }

    private fun getCurrentPosition(): Int? {
        return viewModel.lastShowingCat
    }

    private fun registerObserverItems() {
        activity?.let { activity ->
            viewModel.items.observe(activity,
                Observer {
                    it ?: return@Observer
                    _binding ?: return@Observer
                    updateUI(it)
                })
        }
    }

    private fun updateUI(items: List<Cat>) {
        binding.catViewPager2.adapter.apply {
            if (this is CatDetailsViewPagerAdapter) update(items)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CatDetailsFragment()
    }
}
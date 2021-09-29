package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatDetailsBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel

private val TAG = "T5-CatDetailsFragment"

class CatDetailsFragment : Fragment() {
    private var _binding: FragmentCatDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        setViewPagerSettings()
        registerObserverItems()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        showCatAtCurrentPosition()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setViewPagerSettings() {
        binding.catViewPager2.apply {
            adapter = CatDetailsViewPagerAdapter()

            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    setLastShowingCat(position)
                }
            })
        }
    }

    private fun setLastShowingCat(position:Int){
        viewModel.lastShowingCat = position
    }

    private fun showCatAtCurrentPosition() {
        getCurrentPosition()?.let { position ->
            binding.catViewPager2.setCurrentItem(position, false)
        }
    }

    private fun getCurrentPosition(): Int? {
        // for set to cerrent position on rotate
        viewModel.lastShowingCat?.let {
            return it
        }

        //for set to current position from grid
//        viewModel.getPositionShowingCat().value?.let {
//            return it
//        }
        return null
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
        fun newInstance()=
            CatDetailsFragment()
    }
}

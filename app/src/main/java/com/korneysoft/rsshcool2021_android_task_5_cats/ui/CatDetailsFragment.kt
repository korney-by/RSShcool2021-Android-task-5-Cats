package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        val transition: Transition = TransitionInflater.from(context)
            .inflateTransition(R.transition.cat_shared_element_transition)
        sharedElementEnterTransition = transition

        setViewPagerSettings()
        registerObserverItems()

        showCatAtCurrentPosition()

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


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // showCatAtCurrentPosition()


    }

    override fun onResume() {
        super.onResume()
        //showCatAtCurrentPosition()

    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getView(position: Int): View? {
//        val holder =
//            (binding.catViewPager2[0] as RecyclerView).layoutManager?.findViewByPosition(position)
//        return holder?.itemView
        //val view =binding.catViewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(position)

        binding.catViewPager2.adapter?.let { adapter ->
            val currentFragment =
                childFragmentManager.findFragmentByTag("f" + adapter.getItemId(position))
            return currentFragment?.view
        }
        return null
    }

    private fun setViewPagerSettings() {
        binding.catViewPager2.apply {
            adapter = CatDetailsViewPagerAdapter { getCurrentFragment() }

            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    saveLastShowingCat(position)
                }
            })
        }
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
package com.korneysoft.rsshcool2021_android_task_5_cats.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatDetailsBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.interfaces.SaveImageInterface
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel
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
        setCallbackForViewPager2()
        setListenerForSaveButton()
        prepareSharedElementTransition()
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        showCatAtCurrentPosition()
    }

    private fun setListenerForSaveButton() {
        binding.floatingButtonSave.setOnClickListener() {
            activity?.let { activity ->
                if (activity !is SaveImageInterface) {
                    return@setOnClickListener
                }
                if (viewModel.checkOnlineState()) {
                    getCurrentPosition()?.let { position ->
                        val cat = viewModel.getCatFromPosition(position)
                        activity.saveImage(cat)
                    }
                }

            }
        }
    }

    private fun setCallbackForViewPager2() {
        binding.catViewPager2.apply {
            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    saveLastShowingCat(position)
                }
            })
        }
    }

    private fun prepareSharedElementTransition() {
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.cat_shared_element_transition)

        setEnterSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String?>,
                    sharedElements: MutableMap<String?, View?>
                ) {
                    getCurrentPosition()?.let { position ->
                        val view = getView(position)
                        view?.let {
                            // Map the first shared element name to the child ImageView.
                            sharedElements[names[0]] = view
                        }
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getView(position: Int): View? {
        val cat = viewModel.getCatFromPosition(position)
        return binding.catViewPager2.findViewWithTag<View>(cat?.id)
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

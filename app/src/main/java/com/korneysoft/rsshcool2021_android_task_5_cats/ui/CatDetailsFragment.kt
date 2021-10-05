package com.korneysoft.rsshcool2021_android_task_5_cats.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.SharedElementCallback
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatIndexed
import com.korneysoft.rsshcool2021_android_task_5_cats.data.toCat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatDetailsBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.interfaces.SaveImageInterface
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlin.collections.set


private val TAG = "T5-CatDetailsFragment"

class CatDetailsFragment : Fragment() {

    private var _binding: FragmentCatDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()
    private val adapter by lazy {
        CatDetailsViewPagerAdapter { getCurrentFragment() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.catViewPager2.adapter = adapter

        startCollectItems()
        showCatAtCurrentPosition()


        setCallbackForViewPager2()
        setListenerForSaveButton()

        prepareSharedElementTransition()
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        showCatAtCurrentPosition()
        startPostponedEnterTransition()
    }

    private fun startCollectItems() {
        Log.d(TAG, "startCollectItems")
        lifecycleScope.launchWhenCreated {
            viewModel.getListData().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun setListenerForSaveButton() {
        binding.floatingButtonSave.setOnClickListener() {
            activity?.let { activity ->
                if (activity !is SaveImageInterface) {
                    return@setOnClickListener
                }
                if (viewModel.checkOnlineState()) {
                    val catIndexed = viewModel.lastShowingCat
                    activity.saveImage(catIndexed?.toCat())
//                    getCurrentPosition()?.let { position ->
//                        val cat = viewModel.getCatFromPosition(position)
//                        activity.saveImage(cat)
//                    }
                }

            }
        }
    }

    private fun setCallbackForViewPager2() {
        binding.catViewPager2.apply {
            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    //saveLastShowingCat(position)
                    Log.d(
                        TAG,
                        "OnPageChangeCallback - $position - " + viewModel.getUrl(position) ?: ""
                    )
                }
            })
        }
    }

    fun toRememberUrl(position: Int, url: String?) {
        viewModel.toRememberUrl(position, url)
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
                    viewModel.lastShowingCat?.let { catIndexed ->
                        //val view = getView(catIndexed)
                        showCatAtCurrentPosition()
                        val view = getView(catIndexed)
                        Log.d(TAG, "sharedElementEnterTransition - ${catIndexed.toString()}")
                        view?.let {
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

    private fun getView(catIndexed: CatIndexed): View? {
        return binding.catViewPager2.findViewWithTag<ImageView>(catIndexed.id)
//        val v = binding.catViewPager2.findViewById<ImageView>()(R.id.cat_card_detail)
//        return v.findViewWithTag(catIndexed.id)

    }

    private fun getCurrentFragment() = this


    private fun showCatAtCurrentPosition() {
        viewModel.lastShowingCat?.let { catIndexed ->
            //binding.catViewPager2.currentItem = (catIndexed.index, false)
            binding.catViewPager2.setCurrentItem(catIndexed.index+1, false)
            //binding.catViewPager2.setCurrentItem(catIndexed.index, false)
            //adapter.setData(it)
            //pagedList.loadAround(position)
            //binding.catViewPager2. .currentItem scrollToPosition
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CatDetailsFragment()
    }
}

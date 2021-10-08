package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.transition.TransitionSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatIndexed
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatListBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatListFragmentViewModel
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "T5-CatListFragment: "

class CatListFragment : Fragment(), CatListRecyclerViewAdapter.OnCatListener {

    private var _binding: FragmentCatListBinding? = null
    private val binding get() = _binding!!
    private val gridSettings by lazy { GridSettings() }
    private val viewModel: CatViewModel by activityViewModels()
    private val viewModelFragment: CatListFragmentViewModel by viewModels()
    private var layoutManager: GridLayoutManager? = null
    private var selectedView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        // startCollectItems()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatListBinding.inflate(inflater, container, false)
        val view = binding.root

        //showLoadAnimation()
        hideLoadAnimation()
        showCatsRecyclerView()


        if (staticAdapter == null) {
            staticAdapter = CatListRecyclerViewAdapter(
                gridSettings.cellSize,
                onCatListener = this,
                { getCurrentFragment() })
        } else {
            staticAdapter?.reinitAdapter(
                gridSettings.cellSize,
                onCatListener = this,
                { this })
        }

        if (viewModelFragment.isNotInitialised) {
            viewModelFragment.initViewModel(staticAdapter, viewModel)
        }

        layoutManager = GridLayoutManager(context, gridSettings.columnCount)

        binding.catListRecyclerView.layoutManager = layoutManager
        binding.catListRecyclerView.adapter = staticAdapter


        // setScrollRVListener()
        prepareSharedElementTransition()
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return view
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        layoutManager?.let {
//            outState.putParcelable(LIST_STATE_KEY, it.onSaveInstanceState())
//        }
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        savedInstanceState?.let {
//            catListState = it.getParcelable(LIST_STATE_KEY)
//        }
//    }

    private fun prepareSharedElementTransition() {
        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)

        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String?>,
                    sharedElements: MutableMap<String?, View?>
                ) {
                    viewModel.lastShowingCat?.let { cat ->
                        val view = getView(cat)
                        view?.let {
                            if (exitTransition is TransitionSet) {
                                (exitTransition as TransitionSet).excludeTarget(view, true)
                            }

                            sharedElements[names[0]] = view
                        }
                    }
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // prescrollForCorrectAnimation()
    }

    override fun onPause() {
        super.onPause()
//        mBundleRecyclerViewState = Bundle()
//        val listState = binding.catListRecyclerView.layoutManager?.onSaveInstanceState()
//        mBundleRecyclerViewState?.putParcelable(LIST_STATE_KEY, listState)
    }

    override fun onResume() {
        super.onResume()

//        mBundleRecyclerViewState?.let {
//            val listState: Parcelable? = it.getParcelable(LIST_STATE_KEY)
//            binding.catListRecyclerView.layoutManager?.onRestoreInstanceState(listState)
//        }


        scrollToPositionCurrentCat()
//        catListState?.let{
//            layoutManager?.onRestoreInstanceState(it);
//        }
    }

    override fun onDestroyView() {
        saveVisiblePosition()
        super.onDestroyView()
        _binding = null
    }

    private fun setScrollRVListener() {
        binding.catListRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // if (newState == RecyclerView.SCROLL_STATE_SETTLING) startPostponedEnterTransition()
            }
        })
    }

    fun getSelectedView(): View? = selectedView

    private fun getView(catIndexed: CatIndexed): View? {
        return binding.catListRecyclerView.findViewWithTag(catIndexed.id)
    }

    private fun getCurrentFragment() = this

    private fun saveVisiblePosition() {
        (binding.catListRecyclerView.layoutManager as GridLayoutManager).let {
            firstGridVisiblePosition = it.findFirstVisibleItemPosition()
            lastGridVisiblePosition = it.findLastVisibleItemPosition()
        }
    }

    private fun isCatPositionVisible(position: Int): Boolean {
        return position in firstGridVisiblePosition..lastGridVisiblePosition
    }

    fun prescrollForCorrectAnimation() {
        val catIndexed = viewModel.lastShowingCat ?: return
        if (!isCatPositionVisible(catIndexed.index)) {
            binding.catListRecyclerView.scrollToPosition(catIndexed.index)
        }
    }

    private fun scrollToPositionCurrentCat() {
        val catIndexed = viewModel.lastShowingCat ?: return
        val layoutManager = binding.catListRecyclerView.layoutManager ?: return

        val viewAtPosition = layoutManager.findViewByPosition(catIndexed.index)
        if (viewAtPosition == null) {
            binding.catListRecyclerView.scrollToPosition(catIndexed.index)
            saveVisiblePosition()
            return
        }

        if (layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)) {
            layoutManager.scrollToPosition(catIndexed.index)
        }
    }

    private fun showLoadAnimation() {
        if (binding.imageViewBackground.visibility == View.VISIBLE) {
            Glide
                .with(this)
                .asGif()
                .load(R.raw.black_cat)
                .into(binding.imageViewBackground)
        }
    }

    fun imageLoadFailed() {
        viewModel.checkOnlineState()
    }

    private fun hideLoadAnimation() {
        binding.imageViewBackground.apply {
            if (visibility != View.GONE) visibility = View.GONE
        }
    }

    private fun showCatsRecyclerView() {
        binding.catListRecyclerView.apply {
            if (visibility != View.VISIBLE) {
                visibility = View.VISIBLE
                activity.apply {
                    if (this is SetNavigationBarColor) setNavigationBarColor()
                }
            }
        }
    }

    override fun onCatClick(catIndexed: CatIndexed) {
        Log.d(TAG, "OnClick $catIndexed")
        selectedView = getView(catIndexed)
        if (selectedView != null) {
            viewModel.setShowingCat(catIndexed)
        }
    }

    companion object {
        const val LIST_STATE_KEY = "LIST_STATE_KEY"
        private var mBundleRecyclerViewState: Bundle? = null
        private var staticAdapter: CatListRecyclerViewAdapter? = null

        var firstGridVisiblePosition: Int = -1
        var lastGridVisiblePosition: Int = -1

        @JvmStatic
        fun newInstance() = CatListFragment()
    }

    inner class GridSettings {
        private var _columnCount: Int = 0
        private var _cellSize: Int = 0

        val columnCount: Int get() = _columnCount
        val cellSize: Int get() = _cellSize

        init {
            val width: Int
            val height: Int
            activity?.let { activity ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    width = activity.windowManager.currentWindowMetrics.bounds.width()
                    height = activity.windowManager.currentWindowMetrics.bounds.height()
                } else {
                    val displayMetrics = DisplayMetrics()
                    @Suppress("DEPRECATION")
                    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                    width = displayMetrics.widthPixels
                    height = displayMetrics.heightPixels
                }

                _columnCount = if (height > width) 2
                else (width / (height / 2))

                _cellSize = width / _columnCount
            }
        }
    }
}

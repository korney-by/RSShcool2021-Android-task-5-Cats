package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatIndexed
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatListBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "T5-CatListFragment: "

class CatListFragment : Fragment(), CatListRecyclerViewAdapter.OnCatListener {

    private var _binding: FragmentCatListBinding? = null
    private val binding get() = _binding!!
    private val gridSettings by lazy { GridSettings() }
    private val viewModel: CatViewModel by activityViewModels()
    private val adapter by lazy {
        CatListRecyclerViewAdapter(
            holderSize,
            this
        ) { getCurrentFragment() }
    }

    private var columnCount = 0
    private var holderSize = 0
    private var selectedView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatListBinding.inflate(inflater, container, false)
        val view = binding.root

        showLoadAnimation()

        columnCount = gridSettings.columnCount
        holderSize = gridSettings.cellSize

        val layout = GridLayoutManager(context, columnCount)
        // NotifyingGridLayoutManager(context, columnCount)
        // layout.onLayoutCompleteCallback = onLayoutCompleted(layout)
        binding.catListRecyclerView.layoutManager = layout
        binding.catListRecyclerView.adapter = adapter
        startCollectItems()

        // setScrollRVListener()
        prepareSharedElementTransition()
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return view
    }

    private fun onLayoutCompleted(layout: NotifyingGridLayoutManager): OnLayoutCompleteCallback {
        return object : OnLayoutCompleteCallback {
            override fun onLayoutComplete() {
//                binding.catListRecyclerView.isNestedScrollingEnabled = !(layout.isLastItemCompletelyVisible())
//                startPostponedEnterTransition()
//                scrollToPositionCurrentCat()
                val i = 1
            }
        }
    }

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

    override fun onResume() {
        super.onResume()
        scrollToPositionCurrentCat()
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
            viewModel.firstGridVisiblePosition = it.findFirstVisibleItemPosition()
            viewModel.lastGridVisiblePosition = it.findLastVisibleItemPosition()
        }
    }

    private fun isCatPositionVisible(position: Int): Boolean {
        return position in viewModel.firstGridVisiblePosition..viewModel.lastGridVisiblePosition
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

    private fun startCollectItems() {
        Log.d(TAG, "startCollectItems")
        lifecycleScope.launchWhenStarted { // }WhenCreated {
            viewModel.getListData().collectLatest { //
                showCatsRecyclerView()
                hideLoadAnimation()
                Log.d(TAG, "submitData(it)")
                adapter.submitData(it)
            }
        }
    }

    override fun onCatClick(catIndexed: CatIndexed) {
        Log.d(TAG, "OnClick $catIndexed")
        selectedView = getView(catIndexed)
        if (selectedView != null) {
            viewModel.setShowingCat(catIndexed) { this@CatListFragment }
        }
    }

    companion object {
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

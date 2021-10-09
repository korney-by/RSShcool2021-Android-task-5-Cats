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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatListBinding.inflate(inflater, container, false)
        val view = binding.root

        initialiseAdapter()

        if (viewModelFragment.isNotInitialised) {
            viewModelFragment.initViewModel(staticAdapter, viewModel)
        }

        layoutManager = GridLayoutManager(context, gridSettings.columnCount)

        binding.catListRecyclerView.layoutManager = layoutManager
        binding.catListRecyclerView.adapter = staticAdapter

        prepareSharedElementTransition()
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return view
    }

    private fun initialiseAdapter() {
        if (staticAdapter == null) {
            staticAdapter = CatListRecyclerViewAdapter(
                gridSettings.cellSize,
                onCatListener = this
            ) { getCurrentFragment() }
        } else {
            staticAdapter?.reinitAdapter(
                gridSettings.cellSize,
                onCatListener = this
            ) { getCurrentFragment() }
        }
    }

    override fun onResume() {
        super.onResume()
        scrollToPositionCurrentCat()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    fun getSelectedView(): View? = selectedView

    private fun getView(catIndexed: CatIndexed): View? {
        return binding.catListRecyclerView.findViewWithTag(catIndexed.id)
    }

    private fun getCurrentFragment() = this

    private fun scrollToPositionCurrentCat() {
        val catIndexed = viewModel.lastShowingCat ?: return
        val layoutManager = binding.catListRecyclerView.layoutManager ?: return

        val viewAtPosition = layoutManager.findViewByPosition(catIndexed.index)
        if (viewAtPosition == null) {
            binding.catListRecyclerView.scrollToPosition(catIndexed.index)
            return
        }

        if (layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)) {
            layoutManager.scrollToPosition(catIndexed.index)
        }
    }

    fun imageLoadFailed() {
        viewModel.checkOnlineState()
    }

    override fun onCatClick(catIndexed: CatIndexed) {
        Log.d(TAG, "OnClick $catIndexed")
        selectedView = getView(catIndexed)
        if (selectedView != null) {
            viewModel.setShowingCat(catIndexed)
        }
    }

    companion object {
        private var staticAdapter: CatListRecyclerViewAdapter? = null

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

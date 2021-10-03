package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.graphics.drawable.Drawable
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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatListBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel

private const val TAG = "T5-CatListFragment: "

class CatListFragment : Fragment(), CatListRecyclerViewAdapter.OnCatListener {
    private var _binding: FragmentCatListBinding? = null
    private val binding get() = _binding!!
    private val gridSettings by lazy { GridSettings() }
    private val viewModel: CatViewModel by activityViewModels()

    private var columnCount = 0
    private var holderSize = 0
    private var selectedView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatListBinding.inflate(inflater, container, false)
        val view = binding.root

        columnCount = gridSettings.columnCount
        holderSize = gridSettings.cellSize

        showLoadAnimation()
        setRecyclerViewSettings()
        registerObserverItems()

        prepareTransition()
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return view
    }

    private fun setRecyclerViewSettings() {
        binding.catListRecyclerView.layoutManager = GridLayoutManager(context, columnCount)
        binding.catListRecyclerView.adapter = CatListRecyclerViewAdapter(
            holderSize,
            this,
            { getCurrentFragment() }
        )
    }

    private fun prepareTransition() {
        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.grid_exit_transition)

        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String?>,
                    sharedElements: MutableMap<String?, View?>
                ) {
                    viewModel.lastShowingCat?.let { position ->
                        val view = getView(position)
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
        prescrollForCorrectAnimation()
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

    fun getSelectedView(): View? = selectedView

    private fun getView(position: Int): View? {
        val cat = viewModel.getCatFromPosition(position)
        return binding.catListRecyclerView.findViewWithTag(cat?.id)
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
        val position = viewModel.lastShowingCat ?: return
        if (!isCatPositionVisible(position)) {
            binding.catListRecyclerView.scrollToPosition(position)
        }
    }

    private fun scrollToPositionCurrentCat() {
        val position = viewModel.lastShowingCat ?: return
        val layoutManager = binding.catListRecyclerView.layoutManager ?: return

        val viewAtPosition = layoutManager.findViewByPosition(position)
        if (viewAtPosition == null) {
            binding.catListRecyclerView.scrollToPosition(position)
            saveVisiblePosition()
            return
        }

        if (layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)) {
            layoutManager.scrollToPosition(position)
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

    fun imageLoadFailed(){
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

//    private fun setRecycleViewSettings() {
//        binding.catListRecyclerView.apply {
//            layoutManager = GridLayoutManager(context, columnCount)
//            adapter = CatListRecyclerViewAdapter(
//                holderSize,
//                { onClickOnCat(it) },
//                { getCurrentFragment() })
//        }
//    }

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
        showCatsRecyclerView()
        hideLoadAnimation()
        binding.catListRecyclerView.adapter.apply {
            if (this is CatListRecyclerViewAdapter) update(items)
        }
    }

    override fun onCatClick(position: Int) {
        Log.d(TAG, "OnClick $position")
        selectedView = getView(position)
        if (selectedView != null) {
            viewModel.setShowingCat(position) { this@CatListFragment }
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

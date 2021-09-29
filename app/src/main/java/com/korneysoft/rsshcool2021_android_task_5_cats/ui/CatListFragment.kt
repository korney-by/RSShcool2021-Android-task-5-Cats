package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatListBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel


class CatListFragment : Fragment() {
    private var _binding: FragmentCatListBinding? = null
    private val binding get() = _binding!!
    private val gridSettings by lazy { GridSettings() }

    private val viewModel: CatViewModel by activityViewModels()

    private var columnCount = 0
    private var holderSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
//            columnCount = it.getInt(ARG_COLUMN_COUNT)
//            holderSize = it.getInt(ARG_HOLDER_SIZE)
        }
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
        setRecycleViewSettings()
        registerObserverItems()


        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String?>, sharedElements: MutableMap<String?, View?>
                ) {
                    // Locate the ViewHolder for the clicked position.
                    viewModel.lastShowingCat?.let { position ->
                        val view = getView(position)
                        view?.let { view ->
                            // Map the first shared element name to the child ImageView.
                            sharedElements[names[0]] = view//.findViewById(R.id.cat_card)
                        }
                    }
                }
            }
        )


        return view
    }

    override fun onResume() {
        super.onResume()
        showCurrentCat()
    }

    override fun onDestroyView() {
        saveVisiblePosition()
        super.onDestroyView()
        _binding = null
    }

    private fun getView(position: Int): View? {
        val holder = binding.catListRecyclerView.findViewHolderForAdapterPosition(position)
        return holder?.itemView
    }

    private fun ShowDetailsFragment_TMP(position: Int) {
        val fragment: Fragment = CatDetailsFragment.newInstance()
        activity?.supportFragmentManager?.let { fragmentManager ->
            getView(position)?.let { view ->
                fragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addSharedElement(view, view.transitionName)
                    .replace(
                        R.id.fragmentContainerView,
                        fragment,
                        CatDetailsFragment.javaClass.simpleName
                    )
                    .addToBackStack(CatDetailsFragment.javaClass.simpleName)
                    .commit()
            }
        }
    }


    private fun showCurrentCat() {
        viewModel.lastShowingCat?.let {
            if (!isCatPositionVisible(it)) {
                binding.catListRecyclerView.scrollToPosition(it)
            }
        }
    }

    private fun saveVisiblePosition() {
        (binding.catListRecyclerView.layoutManager as GridLayoutManager).let {
            viewModel.firstGridVisiblePosition = it.findFirstCompletelyVisibleItemPosition()
            viewModel.lastGridVisiblePosition = it.findLastCompletelyVisibleItemPosition()
        }
    }

    private fun isCatPositionVisible(position: Int): Boolean {
        return position in viewModel.firstGridVisiblePosition..viewModel.lastGridVisiblePosition
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

    private fun hideLoadAnimation() {
        binding.imageViewBackground.apply {
            if (visibility != View.GONE) visibility = View.GONE
        }
    }

    private fun showCatsRecyclerView() {
        activity.apply {
            if (this is SetNavigationBarColor) setNavigationBarColor()
        }

        binding.catListRecyclerView.apply {
            if (visibility != View.VISIBLE) visibility = View.VISIBLE
        }
    }

    private fun setRecycleViewSettings() {
        binding.catListRecyclerView.apply {
            layoutManager = GridLayoutManager(context, columnCount)
            adapter = CatListRecyclerViewAdapter(holderSize) { onClickOnCat(it) }
        }
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

    private fun onClickOnCat(position: Int?) {
        position?.let { position ->
            viewModel.setShowingCat(position)
            ShowDetailsFragment_TMP(position)
        }
    }


    private fun updateUI(items: List<Cat>) {
        showCatsRecyclerView()
        hideLoadAnimation()
        binding.catListRecyclerView.adapter.apply {
            if (this is CatListRecyclerViewAdapter) update(items)
        }
    }

    companion object {
        const val ARG_COLUMN_COUNT = "ARG_COLUMN_COUNT"
        const val ARG_HOLDER_SIZE = "ARG_HOLDER_SIZE"

        @JvmStatic
        fun newInstance() = CatListFragment()

//        fun newInstance(columnCount: Int, holderSize: Int) =
//            CatListFragment().apply {
//                arguments = Bundle().apply {
//                    putInt(ARG_COLUMN_COUNT, columnCount)
//                    putInt(ARG_HOLDER_SIZE, holderSize)
//                }
//            }
    }

    inner class GridSettings() {
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

                if (height > width) {
                    _columnCount = 2
                } else {
                    _columnCount = (width / (height / 2)).toInt()
                }
                _cellSize = width / _columnCount
            }
        }
    }
}
package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val viewModel: CatViewModel by activityViewModels()

    private var columnCount = 0
    private var holderSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            holderSize = it.getInt(ARG_HOLDER_SIZE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatListBinding.inflate(inflater, container, false)
        val view = binding.root

        showLoadAnimation()
        setRecycleViewSettings()
        registerObserverItems()
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
                .into(binding.imageViewBackground);
        }
    }

    private fun hideLoadAnimation() {
        binding.imageViewBackground.apply {
            if (visibility != View.GONE) visibility = View.GONE
        }
    }

    private fun showCatsRecyclerView() {
        activity.apply {
            if (this is NavigationBarColor) setNavigationBarColor()
        }

        binding.catListRecyclerView.apply {
            if (visibility != View.VISIBLE) visibility = View.VISIBLE
        }
    }

    private fun setRecycleViewSettings() {
        binding.catListRecyclerView.apply {
            layoutManager = GridLayoutManager(context, columnCount)
            //if (columnCount <= 1) LinearLayoutManager(context)  else GridLayoutManager(context, columnCount)
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

    private fun onClickOnCat(index: Int?) {
        index?.let {
            viewModel.setShowingCat(it)
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
        fun newInstance(columnCount: Int, holderSize: Int) =
            CatListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putInt(ARG_HOLDER_SIZE, holderSize)
                }
            }
    }
}
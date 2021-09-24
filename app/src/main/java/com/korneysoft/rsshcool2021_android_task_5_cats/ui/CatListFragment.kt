package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatListBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel


class CatListFragment : Fragment() {
    private var _binding: FragmentCatListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CatViewModel by activityViewModels()

    private var columnCount = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoadAnimation() {
        if (binding.imageViewBackground.visibility == View.VISIBLE) {
            Glide.with(this).asGif().load(R.raw.black_cat).into(binding.imageViewBackground);
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
            layoutManager = if (columnCount <= 1) LinearLayoutManager(context)
            else GridLayoutManager(context, columnCount)

            val holderSize = (getWidthDisplay() / columnCount).toInt()
            adapter = CatListRecyclerViewAdapter(holderSize)
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

    private fun updateUI(items: List<Cat>) {
        showCatsRecyclerView()
        hideLoadAnimation()
        binding.catListRecyclerView.adapter.apply {
            if (this is CatListRecyclerViewAdapter) update(items)
        }
    }

    private fun getWidthDisplay(): Int {
        activity?.let { activity ->
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.windowManager.currentWindowMetrics.bounds.width()
            } else {
                val displayMetrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                displayMetrics.widthPixels
            }
        }
        return 0
    }

    companion object {
        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int = 2) =
            CatListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
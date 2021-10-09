package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentOfflineBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.ui.toolbar.clearExceptTitle
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel

class OfflineFragment : Fragment() {
    private var _binding: FragmentOfflineBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfflineBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.buttonTryAgain.setOnClickListener {
            viewModel.checkOnlineState()
        }
        setToolbar()
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setToolbar() {
        (activity?.findViewById(R.id.toolbar) as Toolbar).clearExceptTitle()
    }

    companion object {
        @JvmStatic
        fun newInstance() = OfflineFragment()
    }
}

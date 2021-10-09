package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatIndexed
import com.korneysoft.rsshcool2021_android_task_5_cats.data.toCat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatDetailsBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel
import kotlin.collections.set

private const val TAG = "T5-CatDetailsFragment"

class CatDetailsFragment : Fragment() {

    private var _binding: FragmentCatDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()

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

        setListenerForSaveButton()
        prepareSharedElementTransition()
        loadSharedImage(viewModel.lastShowingCat)
        InitialiseToolbar()

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loadSharedImage(shownCat: CatIndexed?) {
        val view = binding.imageViewDetails
        shownCat ?: run {
            startPostponedEnterTransition()
            return
        }
        view.transitionName = shownCat.imageUrl
        view.tag = shownCat.id
        Glide
            .with(view.context)
            .load(shownCat.imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            })
            .into(view)
    }

    private fun setListenerForSaveButton() {
        binding.floatingButtonSave.setOnClickListener {
            activity?.let { activity ->
//                if (activity !is SaveImageInterface) {
//                    return@setOnClickListener
//                }
//                if (viewModel.checkOnlineState()) {
//                    val catIndexed = viewModel.lastShowingCat
//                    //activity.saveImage(catIndexed?.toCat())
//                }
            }
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
                    sharedElements[names[0]] = binding.imageViewDetails
                    Log.d(TAG, "sharedElementEnterTransition - imageViewDetails")
                }
            }
        )
    }




    private fun InitialiseToolbar() {
        //val toolBar = (activity as AppCompatActivity).supportActionBar
        val toolBar: Toolbar? = activity?.findViewById(R.id.toolbar)
        toolBar?.let {
            toolBar.inflateMenu(R.menu.menu_toolbar_details)
            val menuItem: MenuItem = toolBar.menu.getItem(0) // findViewById(R.id.save_image)
            menuItem.setOnMenuItemClickListener {
                val catIndexed = viewModel.lastShowingCat
                viewModel.startDownload(catIndexed?.toCat())
                true
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CatDetailsFragment()
    }
}

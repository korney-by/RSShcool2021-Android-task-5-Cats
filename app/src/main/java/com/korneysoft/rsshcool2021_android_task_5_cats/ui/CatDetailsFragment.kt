package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatDetailsBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.ui.extension.getFilename
import com.korneysoft.rsshcool2021_android_task_5_cats.ui.extension.setToolBarMenu
import com.korneysoft.rsshcool2021_android_task_5_cats.ui.extension.setToolbarHamburgerButton
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.MainViewModel
import kotlin.collections.set

private const val TAG = "T5-CatDetailsFragment"

//TODO комментарии
// TODO фрагмент слишком много знает о том, что его не касается в частности loadSharedImage.
// TODO Методы слишком нагруженные, невозможно прочитать и понять с первого раза за что они отвечают или их конечную цель.
// TODO лучше отказать от статического object в сторону отдельного класса
class CatDetailsFragment : Fragment() {

    private var _binding: FragmentCatDetailsBinding? = null
    private val binding get() = _binding!! //TODO краш прилы - проверка на null
    private val viewModel: MainViewModel by activityViewModels()

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

        prepareSharedElementTransition()
        loadSharedImage(viewModel.lastShowingCat)
        setToolbar()

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loadSharedImage(shownCat: CatIndexed?) {
        //Todo инкапсуляция, излишние properties
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
            //TODO Inner class
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

    private fun setToolbar() {
        (activity?.findViewById(R.id.toolbar) as Toolbar).let { toolbar ->
            val catIndexed = viewModel.lastShowingCat
            catIndexed?.let {
                val filename = catIndexed.getFilename()
                toolbar.subtitle = getString(
                    R.string.image_details_info,
                    filename,
                    catIndexed.width,
                    catIndexed.height
                )
                toolbar.setToolbarHamburgerButton(
                    R.drawable.ic_baseline_arrow_back_24
                ) { activity?.onBackPressed() }

                toolbar.setToolBarMenu(
                    R.menu.menu_toolbar_details,
                    arrayOf({ viewModel.startDownload(catIndexed.toCat()) })
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CatDetailsFragment()
    }
}

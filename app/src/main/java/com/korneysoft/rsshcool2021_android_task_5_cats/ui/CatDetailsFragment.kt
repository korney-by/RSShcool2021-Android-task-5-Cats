package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatDetailsBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel
import kotlin.collections.set

private val TAG = "T5-CatDetailsFragment"

class CatDetailsFragment : Fragment() {

    private var _binding: FragmentCatDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.catViewPager2.adapter = CatDetailsViewPagerAdapter { getCurrentFragment() }
        registerObserverItems()
        showCatAtCurrentPosition()
        binding.catViewPager2.apply {
            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    saveLastShowingCat(position)
                }
            })
        }

        binding.floatingButtonSave.setOnClickListener() {
            saveImage()
        }

        prepareSharedElementTransition()
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        showCatAtCurrentPosition()
    }

//    private fun saveImageAs() {
//        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.type = "YOUR FILETYPE" //not needed, but maybe usefull
//        intent.putExtra(Intent.EXTRA_TITLE, "YOUR FILENAME") //not needed, but maybe usefull
//        startActivityForResult(intent, SOME_INTEGER)
//
//        override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
//            super.onActivityResult(requestCode, resultCode, resultData)
//
//            if (requestCode == 4711 && resultCode == Activity.RESULT_OK) {
//                resultData?.data?.also { documentUri ->
//
//                    try {
//                        val db = DBBackend(context!!)
//                        val dbFile = File(db.getDatabaseFilename())
//
//                        var exportFile = File(documentUri.path)
//                        exportFile.createNewFile()
//                        dbFile.copyTo(exportFile)
//                    }
//                    catch( error : Exception )
//                    {
//                        Log.e("FileError", error.toString() )
//                        Toast.makeText(context!!,"error: ${error.toString()}", Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
//        super.onActivityResult(requestCode, resultCode, resultData)
//
//        if (requestCode == 4711 && resultCode == Activity.RESULT_OK) {
//            resultData?.data?.also { documentUri ->
//
//                try {
//                    val db = DBBackend(context!!)
//                    val dbFile = File(db.getDatabaseFilename())
//
//                    var exportFile = File(documentUri.path)
//                    exportFile.createNewFile()
//                    dbFile.copyTo(exportFile)
//                }
//                catch( error : Exception )
//                {
//                    Log.e("FileError", error.toString() )
//                    Toast.makeText(context!!,"error: ${error.toString()}", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }

    private fun saveImage() {
        var cat: Cat? = null
        getCurrentPosition()?.let {
            cat = viewModel.getCatFromPosition(it)
        }
        cat?.imageUrl?.let { url ->
            val filename = url.substringAfterLast("/")
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(filename)
                .setDestinationInExternalFilesDir(
                    context,
                    context?.getString(R.string.app_name),
                    filename
                )
            (context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
                .enqueue(request)
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
                    getCurrentPosition()?.let { position ->
                        showCatAtCurrentPosition()
                        val view = getView(position)
                        view?.let {
                            // Map the first shared element name to the child ImageView.
                            sharedElements[names[0]] = view
                        }
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getView(position: Int): View? {
        val cat = viewModel.getCatFromPosition(position)
        return binding.catViewPager2.findViewWithTag<View>(cat?.id)
    }

    private fun getCurrentFragment(): Fragment = this

    private fun saveLastShowingCat(position: Int) {
        viewModel.lastShowingCat = position
    }

    private fun showCatAtCurrentPosition() {
        getCurrentPosition()?.let { position ->
            binding.catViewPager2.setCurrentItem(position, false)
        }
    }

    private fun getCurrentPosition(): Int? {
        return viewModel.lastShowingCat
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
        binding.catViewPager2.adapter.apply {
            if (this is CatDetailsViewPagerAdapter) update(items)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CatDetailsFragment()
    }
}

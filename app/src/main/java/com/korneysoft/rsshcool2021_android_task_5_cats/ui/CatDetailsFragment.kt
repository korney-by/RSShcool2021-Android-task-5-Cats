package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatDetailsBinding

private const val PHOTO_ID = "PHOTO_ID"
private val TAG="T5-CatDetailsFragment"

class CatDetailsFragment : Fragment() {
    private var _binding: FragmentCatDetailsBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var photoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            photoUrl = it.getString(PHOTO_ID)
            Log.d(TAG,": $photoUrl")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoUrl?.let{ url ->
            showCat(url)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    private fun showCat(url:String){
        Glide
            .with(this)
            .load(url)
            .into(binding.imageView)
    }


    companion object {
        @JvmStatic
        fun newInstance(photoId: String) =
            CatDetailsFragment().apply {
                this.arguments =
                    bundleOf(
                        PHOTO_ID to photoId
                    )
            }
    }
}

package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.data.toCatIndexed
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.ViewCatDetailsBinding

private const val TAG = "T5-CatDetViewPagerAdapt"

class CatDetailsViewPagerAdapter(private val getParentFragment: () -> CatDetailsFragment) :
    PagingDataAdapter<Cat, CatDetailsViewPagerAdapter.PagerHolder>(itemComparator) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerHolder {
        return PagerHolder(
            ViewCatDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    private fun toRememberHolder(position: Int, url: String?) {
        getParentFragment().toRememberUrl(position, url)
    }

    override fun onBindViewHolder(holder: PagerHolder, position: Int) {
        val cat: Cat? = getItem(position)

        Log.d(TAG, cat?.toCatIndexed(position).toString())
        toRememberHolder(position, cat?.imageUrl)
        holder.bind(cat)
    }


    inner class PagerHolder(private val binding: ViewCatDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cat: Cat?) {
            binding.apply {
                if (cat == null) {
                    imageViewDetail.transitionName = ""
                    imageViewDetail.tag = ""
                    //showCat(imageViewDetail, cat)
                } else {
                    Log.d(TAG,"BIND - "+ cat.toString())
                    imageViewDetail.transitionName = cat.imageUrl
                    imageViewDetail.tag = cat.id
                    showCat(imageViewDetail, cat)
                }
            }
        }

        private fun showCat(imageView: ImageView, cat: Cat) {
            Glide
                .with(imageView.context)
                .load(cat.imageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        getParentFragment().startPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        getParentFragment().startPostponedEnterTransition()
                        return false
                    }
                })
                .into(imageView)
        }
    }

    companion object {
        private val itemComparator = object : DiffUtil.ItemCallback<Cat>() {

            override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean =
                oldItem == newItem
        }
    }
}

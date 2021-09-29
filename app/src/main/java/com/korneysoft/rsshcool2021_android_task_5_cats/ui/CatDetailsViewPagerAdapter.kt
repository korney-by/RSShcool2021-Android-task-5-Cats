package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.ViewCatDetailsBinding

class CatDetailsViewPagerAdapter(private val getParentFragment: () -> Fragment) :
    ListAdapter<Cat, CatDetailsViewPagerAdapter.PagerHolder>(itemComparator) {

    //private var itemsSize = 0
    private val items = mutableListOf<Cat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerHolder {
        return PagerHolder(
            ViewCatDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PagerHolder, position: Int) {
        val cat: Cat = getItem(position)
        holder.bind(cat)
    }

    override fun getItemCount(): Int {
        //return itemsSize
        return items.size
    }

    fun update(newItems: List<Cat>) {
        //itemsSize = newItems.size
        //submitList(newItems)
         items.addAll(newItems)
         notifyDataSetChanged()
    }

    inner class PagerHolder(private val binding: ViewCatDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cat: Cat) {
            binding.apply {
                itemView.transitionName = cat.imageUrl
                showCat(imageViewDetail, cat)
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
                        getParentFragment().startPostponedEnterTransition();
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        getParentFragment().startPostponedEnterTransition();
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
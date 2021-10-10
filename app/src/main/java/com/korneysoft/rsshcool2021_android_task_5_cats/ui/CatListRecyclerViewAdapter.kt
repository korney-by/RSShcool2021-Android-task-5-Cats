package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatIndexed
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.ViewCatBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.ui.extension.getFlipCardName

private const val TAG = "T5 - CatListRVAdapter"

class CatListRecyclerViewAdapter(
    private var holderSize: Int,
    private var onCatListener: OnCatListener,
    private var getParentFragment: () -> CatListFragment
) : PagingDataAdapter<Cat, CatListRecyclerViewAdapter.CatHolder>(itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatHolder {
        return CatHolder(
            ViewCatBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onCatListener
        ).apply {
//            itemView.minimumHeight = holderSize
//            itemView.minimumWidth = holderSize
        }
    }

    override fun onBindViewHolder(holder: CatHolder, position: Int) {
        val cat = getItem(position)
        holder.bind(cat, holderSize)
    }

    fun resetAdapter(
        holderSize: Int,
        onCatListener: OnCatListener,
        getParentFragment: () -> CatListFragment
    ) {
        this.holderSize = holderSize
        this.onCatListener = onCatListener
        this.getParentFragment = getParentFragment
    }

    interface OnCatListener {
        fun onCatClick(catIndexed: CatIndexed)
    }

    inner class CatHolder(
        private val binding: ViewCatBinding,
        private val onCatListener: OnCatListener
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            Log.d(TAG, " Click - $bindingAdapterPosition")
            val catIndexed = getItem(bindingAdapterPosition)?.toCatIndexed(bindingAdapterPosition)
            itemView.tag = catIndexed?.getFlipCardName()
            catIndexed?.let {
                onCatListener.onCatClick(catIndexed)
            }
        }

        fun bind(cat: Cat?, holderSize: Int) {
            setSizeImageView(binding.imageView, holderSize)
            setTagContentHolder(cat)
            setViewContentHolder(null)
            cat?.let {
                loadImage(it)
            }
        }

        private fun setTagContentHolder(cat: Cat?) {
            binding.apply {
                if (cat == null) {
                    imageView.transitionName = null
                    imageView.tag = null
                } else {
                    imageView.transitionName = cat.imageUrl
                    imageView.tag = cat.id
                }
            }
        }

        private fun setViewContentHolder(cat: Cat?) {
            binding.apply {
                if (cat == null) {
                    binding.textLoading.visibility = View.VISIBLE
                    binding.textSize.text = null
                } else {
                    binding.textLoading.visibility = View.INVISIBLE
                    binding.textSize.text = getParentFragment().getString(
                        R.string.image_info,
                        bindingAdapterPosition + 1,
                        cat.width,
                        cat.height
                    )
                }
            }
        }

        private fun loadImage(cat: Cat) {
            val parentFragment = getParentFragment()
            cat.imageUrl ?: return
            val imageView = binding.imageView
            Glide.with(imageView.context.applicationContext)
                .load(cat.imageUrl)
                .centerCrop()
                .error(R.drawable.ic_baseline_close_24)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.textLoading.visibility = View.INVISIBLE
                        parentFragment.imageLoadFailed()
                        parentFragment.startPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        p0: Drawable?,
                        p1: Any?,
                        p2: Target<Drawable>?,
                        p3: DataSource?,
                        p4: Boolean
                    ): Boolean {
                        setViewContentHolder(cat)
                        parentFragment.startPostponedEnterTransition()
                        return false
                    }
                })
                .into(imageView)
        }

        private fun setSizeImageView(imageView: ImageView, size: Int) {
            imageView.minimumWidth = size
            imageView.maxWidth = size
            imageView.minimumHeight = size
            imageView.maxHeight = size
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

package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
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
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.ViewCatBinding

private const val TAG = "T5 - CatListRVAdapter"

class CatListRecyclerViewAdapter(
    private val holderSize: Int,
    private val onCatListener: OnCatListener,
    private val getParentFragment: () -> Fragment
) : ListAdapter<Cat, CatListRecyclerViewAdapter.CatHolder>(itemComparator) {

    private var itemsSize = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatHolder {
        return CatHolder(
            ViewCatBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onCatListener
        ).apply {
            itemView.minimumHeight = holderSize
            itemView.minimumWidth = holderSize
        }
    }

    override fun onBindViewHolder(holder: CatHolder, position: Int) {
        // val cat: Cat = items[position] //getItem(position)
        val cat: Cat = getItem(position)
        holder.bind(cat, holderSize)
    }

    override fun getItemCount(): Int {
        // items.size
        return itemsSize
    }

    fun update(newItems: List<Cat>) {
        submitList(newItems)
        itemsSize = newItems.size
        // items.addAll(newItems)
        // notifyDataSetChanged()
    }

    interface OnCatListener {
        fun onCatClick(position: Int)
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
            onCatListener.onCatClick(bindingAdapterPosition)
        }

        fun bind(cat: Cat, holderSize: Int) {
            setContentHolder(null)
            setSizeImageView(binding.imageView, holderSize)
            loadImage(cat)
        }

        private fun setContentHolder(cat: Cat?) {
            binding.apply {
                if (cat == null) {
                    imageView.transitionName = null
                    imageView.tag = null
                    binding.textLoading.visibility = View.VISIBLE
                    binding.textSize.text = null
                } else {
                    imageView.transitionName = cat.imageUrl
                    imageView.tag = cat.id
                    binding.textLoading.visibility = View.GONE
                    binding.textSize.text = "${cat.id} - ${cat.width}x${cat.height}"
                }
            }
        }

        private fun loadImage(cat: Cat) {
            cat.imageUrl ?: return
            val imageView = binding.imageView
            Glide.with(imageView.context)
                .load(cat.imageUrl)
                .apply(RequestOptions.centerCropTransform())
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
                        p0: Drawable?,
                        p1: Any?,
                        p2: Target<Drawable>?,
                        p3: DataSource?,
                        p4: Boolean
                    ): Boolean {
                        getParentFragment().startPostponedEnterTransition()
                        setContentHolder(cat)
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

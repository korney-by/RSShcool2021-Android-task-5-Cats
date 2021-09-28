package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
    private val onHolderClickListener: (Int) -> Unit
) : ListAdapter<Cat, CatListRecyclerViewAdapter.CatHolder>(itemComparator) {
    //RecyclerView.Adapter<CatListRecyclerViewAdapter.ViewHolder>() {

    //private var items = mutableListOf<Cat>()
    private var itemsSize = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatHolder {
        return CatHolder(
            ViewCatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CatHolder, position: Int) {
        //val cat: Cat = items[position] //getItem(position)
        val cat: Cat = getItem(position)
        holder.bind(cat, holderSize, position)

        setHolderOnClickListener(holder)
    }

    private fun setHolderOnClickListener(holder: CatHolder) {
        holder.parent.setOnClickListener {
            holder.position?.let { it ->
                Log.d(TAG, "Cat position at List : $it")
                onHolderClickListener(it)
            }
        }
    }

    override fun getItemCount(): Int {
        //items.size
        return itemsSize
    }

    fun update(newItems: List<Cat>) {
        submitList(newItems)
        itemsSize = newItems.size
        //items.addAll(newItems)
        //notifyDataSetChanged()
    }


    inner class CatHolder(private val binding: ViewCatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var cat: Cat? = null
        private var _position: Int? = null
        val position get() = _position
        //val cat get() = _cat

        val parent = binding.root

        fun bind(cat: Cat, holderSize: Int,position:Int) {
            _position=position
            this.cat = cat
            binding.apply {
                setSizeImageView(imageView, holderSize)
                loadImage(imageView, cat)
            }
        }

        private fun setTextHolder(cat: Cat) {
            binding.textLoading.visibility = View.GONE
            binding.textSize.text = "${cat.width} x ${cat.height}"
        }

        private fun loadImage(imageView: ImageView, cat: Cat) {
            cat.imageUrl ?: return

            Glide.with(imageView.context)
                .load(cat.imageUrl)
                .apply(RequestOptions.centerCropTransform())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        p0: GlideException?,
                        p1: Any?,
                        p2: Target<Drawable>?,
                        p3: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        p0: Drawable?,
                        p1: Any?,
                        p2: Target<Drawable>?,
                        p3: DataSource?,
                        p4: Boolean
                    ): Boolean {
                        setTextHolder(cat)
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
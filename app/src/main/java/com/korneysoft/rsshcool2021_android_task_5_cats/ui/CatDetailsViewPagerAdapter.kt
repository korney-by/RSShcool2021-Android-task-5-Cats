package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.ViewCatDetailsBinding

class CatDetailsViewPagerAdapter() :
    ListAdapter<Cat, CatDetailsViewPagerAdapter.PagerHolder>(itemComparator) {
    //RecyclerView.Adapter<CatDetailsViewPagerAdapter.PagerVH>() {

    private var itemsSize = 0

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
        return itemsSize
        //items.size
    }

    fun update(newItems: List<Cat>) {
        itemsSize = newItems.size
        submitList(newItems)
        //items.addAll(newItems)
        //notifyDataSetChanged()
    }

    inner class PagerHolder(private val binding: ViewCatDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cat: Cat) {
            binding.apply {
                showCat(imageViewDetail, cat)
            }
        }

        private fun showCat(imageView: ImageView, cat: Cat) {
            Glide
                .with(imageView.context)
                .load(cat.imageUrl)
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
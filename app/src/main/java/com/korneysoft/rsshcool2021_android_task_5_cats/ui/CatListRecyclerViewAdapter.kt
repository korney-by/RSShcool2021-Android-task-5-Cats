package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.korneysoft.rsshcool2021_android_task_5_cats.R

import com.korneysoft.rsshcool2021_android_task_5_cats.placeholder.PlaceholderContent.PlaceholderItem
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.FragmentCatBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class CatListRecyclerViewAdapter(
    private val cats: List<PlaceholderItem>,
    private val holderSize: Int
) : RecyclerView.Adapter<CatListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentCatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cat: PlaceholderItem = cats[position]
        holder.bind(cat, holderSize)
    }

    override fun getItemCount(): Int = cats.size

    inner class ViewHolder(private val binding: FragmentCatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber

        fun bind(cat: PlaceholderItem, holderSize: Int) {
            binding.itemNumber.text = "${cat.id}"
                //cat.content

            binding.imageView.minimumWidth = holderSize
            binding.imageView.maxWidth = holderSize
            binding.imageView.minimumHeight = holderSize
            binding.imageView.maxHeight = holderSize
        }

//        override fun toString(): String {
//            return super.toString() + " '" + contentView.text + "'"
//        }
    }

}
package com.example.taskdemo.feature.home.presentation.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.taskdemo.colorStateListOf
import com.example.taskdemo.databinding.ItemSimpleAnalyticsBinding
import com.example.taskdemo.setOnSingleClickListener

data class SimpleAnalyticsItem(
    val id: String,
    val title: String,
    val subtitle: String,
    @DrawableRes val icon: Int,
    @ColorInt val tint: Int,
) {

    fun iconBackground(alpha: Float = 0.2f): Int {
        return ColorUtils.setAlphaComponent(tint, (alpha * 255.0f).toInt())
    }
}

class SimpleAnalyticsAdapter(
    private val onItemClick: (SimpleAnalyticsItem) -> Unit
) : ListAdapter<SimpleAnalyticsItem, SimpleAnalyticsAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model, onItemClick)
    }

    class ItemViewHolder private constructor(
        private val binding: ItemSimpleAnalyticsBinding
    ) : ViewHolder(binding.root) {
        var analyticsItem: SimpleAnalyticsItem? = null

        fun bind(item: SimpleAnalyticsItem, onItemClick: (SimpleAnalyticsItem) -> Unit) = with(binding) {
            analyticsItem = item
            tvTitle.text = item.title
            tvSubtitle.text = item.subtitle
            ivIcon.setImageResource(item.icon)
            ivIcon.imageTintList = colorStateListOf(item.tint)
            iconGroup.backgroundTintList = colorStateListOf(item.iconBackground())

            root.setOnSingleClickListener { onItemClick(item) }
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSimpleAnalyticsBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }

    companion object {
        private val DiffCallback = object : ItemCallback<SimpleAnalyticsItem>() {
            override fun areItemsTheSame(
                oldItem: SimpleAnalyticsItem,
                newItem: SimpleAnalyticsItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SimpleAnalyticsItem,
                newItem: SimpleAnalyticsItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}
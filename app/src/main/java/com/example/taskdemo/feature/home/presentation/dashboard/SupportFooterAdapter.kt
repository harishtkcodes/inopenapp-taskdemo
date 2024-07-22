package com.example.taskdemo.feature.home.presentation.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.taskdemo.databinding.ItemSupportBinding

class SupportFooterAdapter(
    private val onWhatsappSupportClick: () -> Unit,
    private val onFaqClick: () -> Unit,
) : ListAdapter<DashboardUiModel.DashboardSupportFooter, SupportFooterAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(
            model,
            onWhatsappSupportClick,
            onFaqClick
        )
    }

    class ItemViewHolder(
        private val binding: ItemSupportBinding
    ) : ViewHolder(binding.root) {

        fun bind(
            dashboardTab: DashboardUiModel.DashboardSupportFooter,
            onWhatsappSupportClick: () -> Unit,
            onFaqClick: () -> Unit,
        ) = with(binding) {
            btnContactWhatsapp.setOnClickListener { onWhatsappSupportClick() }
            btnFaq.setOnClickListener { onFaqClick() }
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val binding = ItemSupportBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ItemViewHolder(binding)
            }
        }

    }

    companion object {
        private val DiffCallback = object : ItemCallback<DashboardUiModel.DashboardSupportFooter>() {
            override fun areItemsTheSame(
                oldItem: DashboardUiModel.DashboardSupportFooter,
                newItem: DashboardUiModel.DashboardSupportFooter
            ): Boolean {
                return true
            }

            override fun areContentsTheSame(
                oldItem: DashboardUiModel.DashboardSupportFooter,
                newItem: DashboardUiModel.DashboardSupportFooter
            ): Boolean {
                return true
            }
        }
    }
}
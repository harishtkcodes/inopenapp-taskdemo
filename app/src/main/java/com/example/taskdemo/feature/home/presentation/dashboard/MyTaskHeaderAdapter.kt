package com.example.taskdemo.feature.home.presentation.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.taskdemo.R
import com.example.taskdemo.commons.util.AnimationUtil.animationListener
import com.example.taskdemo.databinding.ItemDashboardHeaderBinding
import com.example.taskdemo.setOnSingleClickListener

private class DashboardHeaderAdapter(
    private val glide: RequestManager,
    private val onViewAnalyticsClick: () -> Unit = {},
    ) : ListAdapter<DashboardUiModel.DashboardHeader, DashboardHeaderAdapter.ItemViewHolder>(DiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            return ItemViewHolder.from(parent)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val model = getItem(position)
            holder.bind(model, glide, onViewAnalyticsClick)
        }

        class ItemViewHolder private constructor(
            private val binding: ItemDashboardHeaderBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(
                data: DashboardUiModel.DashboardHeader,
                glide: RequestManager,
                onViewAnalyticsClick: () -> Unit
            ) = with(binding) {
                greetingTitle.text = data.greetingMessage.title
                greetingLeadingIcon.text = data.greetingMessage.leadingEmojiText

                val taskUser = data.userProfile
                tvTitle.text = taskUser.fullName
                tvSubtitle.isVisible = false


                greetingLeadingIcon.clearAnimation()
                AnimationUtils.loadAnimation(root.context, R.anim.pop_enter).apply {
                    animationListener(
                        onAnimationStart = {
                            greetingLeadingIcon.isVisible = true
                        },
                    )
                }.also(greetingLeadingIcon::startAnimation)

                btnViewAnalytics.setOnSingleClickListener { onViewAnalyticsClick() }
            }

            companion object {
                fun from(parent: ViewGroup): ItemViewHolder {
                    val binding = ItemDashboardHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return ItemViewHolder(binding)
                }
            }
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<DashboardUiModel.DashboardHeader>() {
                override fun areItemsTheSame(
                    oldItem: DashboardUiModel.DashboardHeader,
                    newItem: DashboardUiModel.DashboardHeader
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: DashboardUiModel.DashboardHeader,
                    newItem: DashboardUiModel.DashboardHeader
                ): Boolean {
                    return true
                }
            }
        }
    }
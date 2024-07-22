package com.example.taskdemo.feature.home.presentation.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.taskdemo.R
import com.example.taskdemo.commons.util.AnimationUtil.animationListener
import com.example.taskdemo.databinding.ItemDashboardHeaderBinding
import com.example.taskdemo.feature.home.domain.model.DashboardData
import com.example.taskdemo.setOnSingleClickListener

class DashboardHeaderAdapter(
    private val glide: RequestManager,
    private val onViewAnalyticsClick: () -> Unit = {},
) : ListAdapter<DashboardUiModel.DashboardHeader, DashboardHeaderAdapter.ItemViewHolder>(
    DiffCallback
) {

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

            val analyticsAdapter = SimpleAnalyticsAdapter {
                // TODO: handle clicks
            }.apply {
                submitList(data.dashboardData?.asSimpleAnalyticsItems(root.context))
            }
            analyticsOverviewList.adapter = analyticsAdapter

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

            private fun DashboardData.asSimpleAnalyticsItems(context: Context): List<SimpleAnalyticsItem> {
                val resources = context.resources
                return listOf(
                    SimpleAnalyticsItem(
                        id = "total_clicks",
                        title = totalClicks.toString(),
                        subtitle = context.getString(R.string.total_clicks),
                        icon = R.drawable.ic_clicks_outline,
                        tint = ResourcesCompat.getColor(
                            resources,
                            R.color.purple_200,
                            context.theme
                        )
                    ),
                    SimpleAnalyticsItem(
                        id = "top_location",
                        title = topLocation ?: "N/A",
                        subtitle = context.getString(R.string.top_location),
                        icon = R.drawable.ic_location_pin_outline,
                        tint = ResourcesCompat.getColor(
                            resources,
                            R.color.brand_blue,
                            context.theme
                        )
                    ),
                    SimpleAnalyticsItem(
                        id = "top_source",
                        title = topSource ?: "N/A",
                        subtitle = context.getString(R.string.top_source),
                        icon = R.drawable.ic_globe_outline,
                        tint = ResourcesCompat.getColor(resources, R.color.red_200, context.theme)
                    ),
                )
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
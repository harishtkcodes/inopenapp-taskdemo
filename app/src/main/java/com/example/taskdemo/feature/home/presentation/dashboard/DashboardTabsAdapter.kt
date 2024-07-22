package com.example.taskdemo.feature.home.presentation.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.RequestManager
import com.example.taskdemo.commons.util.imageloader.GlideImageLoader.Companion.disposeGlideLoad
import com.example.taskdemo.commons.util.recyclerview.Recyclable
import com.example.taskdemo.databinding.ItemDashboardTabLayoutBinding
import com.example.taskdemo.databinding.ItemLinkCardBinding
import com.example.taskdemo.extensions.safeCall
import com.example.taskdemo.feature.home.domain.model.OpenAppLink
import com.example.taskdemo.loadWithGlide
import com.example.taskdemo.setOnSingleClickListener
import com.google.android.material.tabs.TabLayout

class DashboardTabsAdapter(
    private val glide: RequestManager,
    private val onItemClick: (OpenAppLink) -> Unit,
    private val onCopyLink: (String) -> Unit,
    private val onSearch: () -> Unit,
    private val onTabSelected: (DashboardTab) -> Unit,
) : ListAdapter<DashboardUiModel.DashboardTabLayout, DashboardTabsAdapter.ItemViewHolder>(
    DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model, glide, onItemClick, onCopyLink, onSearch, onTabSelected)
    }

    class ItemViewHolder(
        private val binding: ItemDashboardTabLayoutBinding
    ) : ViewHolder(binding.root) {
        private var currentTabPosition: Int = 0

        fun bind(
            data: DashboardUiModel.DashboardTabLayout,
            glide: RequestManager,
            onItemClick: (OpenAppLink) -> Unit,
            onCopyLink: (String) -> Unit,
            onSearch: () -> Unit,
            onTabSelected: (DashboardTab) -> Unit
        ) = with(binding) {

            when (data.selectedTab) {
                DashboardTab.TopLinks -> {
                    safeCall { tabLayout.getTabAt(TAB_TYPE_TOP_LINKS)?.select() }
                }
                DashboardTab.RecentLinks -> {
                    safeCall { tabLayout.getTabAt(TAB_TYPE_RECENT_LINKS)?.select() }
                }
                DashboardTab.FavoriteLinks -> {
                    safeCall { tabLayout.getTabAt(TAB_TYPE_FAV_LINKS)?.select() }
                }
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    // Noop.
                    tab?.position?.let { tabPosition ->
                        currentTabPosition = tabPosition
                        when (tabPosition) {
                            TAB_TYPE_TOP_LINKS -> onTabSelected.invoke(DashboardTab.TopLinks)
                            TAB_TYPE_RECENT_LINKS -> onTabSelected.invoke(DashboardTab.RecentLinks)
                            TAB_TYPE_FAV_LINKS -> onTabSelected.invoke(DashboardTab.FavoriteLinks)
                        }
                    }
                    /*if (defaultFriendsTab) {
                        defaultFriendsTab = false
                    }*/
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // Noop.
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // Noop.
                }
            })

            val linksAdapter = LinksAdapter(
                glide = glide,
                onItemClick = onItemClick,
                onCopyLink = onCopyLink
            ).apply {
                submitList(data.getLinksForActiveTab())
            }
            listView.adapter = linksAdapter

            btnSearch.setOnSingleClickListener { onSearch() }
        }

        companion object {
            const val TAB_TYPE_TOP_LINKS = 0
            private const val TAB_TYPE_RECENT_LINKS = 1
            private const val TAB_TYPE_FAV_LINKS = 2

            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDashboardTabLayoutBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }

    companion object {
        private val DiffCallback = object : ItemCallback<DashboardUiModel.DashboardTabLayout>() {
            override fun areItemsTheSame(
                oldItem: DashboardUiModel.DashboardTabLayout,
                newItem: DashboardUiModel.DashboardTabLayout
            ): Boolean {
                return oldItem.selectedTab == newItem.selectedTab
            }

            override fun areContentsTheSame(
                oldItem: DashboardUiModel.DashboardTabLayout,
                newItem: DashboardUiModel.DashboardTabLayout
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

internal class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val fragmentList: List<Fragment>,
) : FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}

class LinksAdapter(
    private val glide: RequestManager,
    private val onItemClick: (OpenAppLink) -> Unit,
    private val onCopyLink: (String) -> Unit,
) : ListAdapter<OpenAppLink, LinksAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model, glide, onItemClick, onCopyLink)
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        super.onViewRecycled(holder)
        (holder as? Recyclable)?.onViewRecycled()
    }

    class ItemViewHolder(
        private val binding: ItemLinkCardBinding
    ) : ViewHolder(binding.root), Recyclable {

        fun bind(
            data: OpenAppLink,
            glide: RequestManager,
            onItemClick: (OpenAppLink) -> Unit,
            onCopyLink: (String) -> Unit,
        ) = with(binding) {
            tvTitle.text = data.title
            tvLink.text = data.smartLink
            tvTotalClicks.text = data.totalClicks.toString()

            /*try {
                tvSubtitle.isVisible = true
                if (data.createdAt.isNullOrBlank()) {
                    tvSubtitle.text = data.timesAgo
                } else {
                    val ldt = DateUtil.parseUtcString(data.createdAt).toLocalDateTime()
                    tvSubtitle.text = StringBuilder().apply {
                        append(DateUtil.getSimpleDate(ldt))
                        append(" \u2022 ")
                        append(DateUtil.getDateFormat(ldt, "hh:mm a"))

                        *//*if (taskComment.isEdited) {
                            append(" \u2022 ")
                            append(" " + root.resources.getString(R.string.edited))
                        }*//*
                    }
                }
            } catch (e: Exception) {
                tvSubtitle.text = "Just Now"
            }*/
            tvSubtitle.text = data.timesAgo

            glide.load(data.originalImage)
                .into(ivIcon)
            data.originalImage?.let { ivIcon.loadWithGlide(it) }

            root.setOnSingleClickListener { onItemClick(data) }
            ivCopyLink.setOnClickListener { onCopyLink(data.smartLink) }
        }

        override fun onViewRecycled() = with(binding) {
            ivIcon.disposeGlideLoad()
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLinkCardBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }

    companion object {
        private val DiffCallback = object : ItemCallback<OpenAppLink>() {
            override fun areItemsTheSame(
                oldItem: OpenAppLink,
                newItem: OpenAppLink
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: OpenAppLink, newItem: OpenAppLink): Boolean {
                return oldItem == newItem
            }
        }
    }
}
package com.example.taskdemo.feature.home.presentation.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class DashboardTabsAdapter(
    private val glide: RequestManager,
    private val onItemClick: (OpenAppLink) -> Unit,
    private val onCopyLink: (String) -> Unit,
    private val onSearch: () -> Unit,
    private val onTabSelected: (DashboardTab) -> Unit,
    private val recycledViewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
) : ListAdapter<DashboardUiModel.DashboardTabLayout, DashboardTabsAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent, recycledViewPool)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model, glide, onItemClick, onCopyLink, onSearch, onTabSelected)
    }

    class ItemViewHolder(
        private val binding: ItemDashboardTabLayoutBinding,
        private val recycledViewPool: RecyclerView.RecycledViewPool
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentTabPosition: Int = 0
        private var tabSelectedListener: TabLayout.OnTabSelectedListener? = null

        fun bind(
            data: DashboardUiModel.DashboardTabLayout,
            glide: RequestManager,
            onItemClick: (OpenAppLink) -> Unit,
            onCopyLink: (String) -> Unit,
            onSearch: () -> Unit,
            onTabSelected: (DashboardTab) -> Unit
        ) = with(binding) {

            selectTab(data.selectedTab)

            tabSelectedListener = object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.let { tabPosition ->
                        currentTabPosition = tabPosition
                        val selectedTab = when (tabPosition) {
                            TAB_TYPE_TOP_LINKS -> DashboardTab.TopLinks
                            TAB_TYPE_RECENT_LINKS -> DashboardTab.RecentLinks
                            TAB_TYPE_FAV_LINKS -> DashboardTab.FavoriteLinks
                            else -> DashboardTab.TopLinks
                        }
                        onTabSelected.invoke(selectedTab)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // Noop.
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // Noop.
                }
            }
            tabLayout.addOnTabSelectedListener(tabSelectedListener!!)

            val linksAdapter = LinksAdapter(
                glide = glide,
                onItemClick = onItemClick,
                onCopyLink = onCopyLink
            )
            listView.apply {
                adapter = linksAdapter
                setRecycledViewPool(recycledViewPool)
            }

            linksAdapter.submitList(data.getLinksForActiveTab())

            btnSearch.setOnSingleClickListener { onSearch() }
        }

        private fun ItemDashboardTabLayoutBinding.selectTab(tab: DashboardTab) {
            when (tab) {
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
        }

        fun onViewDetachedFromWindow() {
            tabSelectedListener?.let { binding.tabLayout.removeOnTabSelectedListener(it) }
        }

        companion object {
            const val TAB_TYPE_TOP_LINKS = 0
            private const val TAB_TYPE_RECENT_LINKS = 1
            private const val TAB_TYPE_FAV_LINKS = 2

            fun from(
                parent: ViewGroup,
                recycledViewPool: RecyclerView.RecycledViewPool
            ): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDashboardTabLayoutBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding, recycledViewPool)
            }
        }
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        super.onViewRecycled(holder)
        holder.onViewDetachedFromWindow()
    }

    companion object {
        private val DiffCallback =
            object : DiffUtil.ItemCallback<DashboardUiModel.DashboardTabLayout>() {
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
    ) : RecyclerView.ViewHolder(binding.root), Recyclable {

        fun bind(
            data: OpenAppLink,
            glide: RequestManager,
            onItemClick: (OpenAppLink) -> Unit,
            onCopyLink: (String) -> Unit,
        ) = with(binding) {
            tvTitle.text = data.title
            tvLink.text = data.smartLink
            tvTotalClicks.text = String.format(Locale.getDefault(), "%02d", data.totalClicks)

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
        private val DiffCallback = object : DiffUtil.ItemCallback<OpenAppLink>() {
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
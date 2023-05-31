package com.example.hw4.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hw4.BuildConfig
import com.example.hw4.DTO.Ad
import com.example.hw4.DTO.FeedItem
import com.example.hw4.DTO.Post
import com.example.hw4.R
import com.example.hw4.databinding.CardAdBinding
import com.example.hw4.databinding.CardPostBinding


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onRemove(post: Post) {}
    fun onEdit(post: Post) {}
    fun onplayVideo(post: Post) {}
    fun onPostClick(post: Post) {}
    fun PhotoClick(post: Post) {}
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostItemCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("unknown item type")
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }

            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }

            else -> error("unknown view type: $viewType")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            null -> error("unknown item type")
        }
    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
        Glide.with(binding.image)
            .load("${BuildConfig.BASE_URL}/media/${ad.image}")
            .into(binding.image)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,

    ) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SuspiciousIndentation")
    fun bind(post: Post) {
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likes.isChecked = post.likedByMe
            likes.text = "${post.likes}"
            shares.isChecked = post.shareByMe
            shares.text = post.shareCount.toString()
            views.isChecked = post.viewByMe
            views.text = post.countView.toString()
            videoGroup.isVisible = post.video != null


            val url = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
            val urlAttachment = "http://10.0.2.2:9999/media/${post.attachment?.url}"
            if (post.authorAvatar == "") {
                avatar.setImageResource(R.drawable.ic_baseline_add_a_photo_24)
            } else {
                Glide.with(binding.avatar).load(url)
                    .placeholder(R.drawable.ic_baseline_downloading_24)
                    .error(R.drawable.ic_baseline_error_24).timeout(10_000).circleCrop()
                    .into(binding.avatar)
            }
            if (post.attachment == null) {
                attachment.isVisible = false//nnn
            } else {
                Glide.with(binding.attachment).load(urlAttachment)
                    .placeholder(R.drawable.ic_baseline_downloading_24)
                    .error(R.drawable.ic_baseline_error_24).timeout(10_000).into(binding.attachment)
                attachment.isVisible = true//nnn
            }



            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            likes.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            shares.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            videoPlay.setOnClickListener {
                onInteractionListener.onplayVideo(post)
            }
            binding.root.setOnClickListener {
                onInteractionListener.onPostClick(post)
            }
            binding.attachment.setOnClickListener {
                onInteractionListener.PhotoClick(post)
            }

        }
    }
}

class PostItemCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean =
        oldItem == newItem
}
package com.example.hw4.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hw4.DTO.Post
import com.example.hw4.R
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
) : ListAdapter<Post, PostViewHolder>(PostItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = (CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
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
                Glide.with(binding.avatar)
                    .load(url)
                    .placeholder(R.drawable.ic_baseline_downloading_24)
                    .error(R.drawable.ic_baseline_error_24)
                    .timeout(10_000)
                    .circleCrop()
                    .into(binding.avatar)
            }
            if (post.attachment == null) {
                attachment.isVisible = false//nnn
            } else {
                Glide.with(binding.attachment)
                    .load(urlAttachment)
                    .placeholder(R.drawable.ic_baseline_downloading_24)
                    .error(R.drawable.ic_baseline_error_24)
                    .timeout(10_000)
                    .into(binding.attachment)
                attachment.isVisible = true//nnn
            }



            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
                    menu.setGroupVisible(R.id.owned,post.ownedByMe)
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

class PostItemCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem == newItem
}
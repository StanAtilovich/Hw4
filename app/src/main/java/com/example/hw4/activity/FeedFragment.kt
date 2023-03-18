package com.example.hw4.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.hw4.util.AndroidUtils
import com.example.hw4.DTO.Post
import com.example.hw4.R
import com.example.hw4.activity.NewPostFragment.Companion.textArg
import com.example.hw4.adapter.OnInteractionListener
import com.example.hw4.adapter.PostAdapter
import com.example.hw4.databinding.FragmentFeedBinding
import com.example.hw4.viewModel.PostViewModel


class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container, false
        )
        val viewModel: PostViewModel by viewModels(
            ownerProducer = ::requireParentFragment
        )

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                    viewModel.likeById(post.id, post.likedByMe)
            }


            override fun onPostClick(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_onePostFragment,
                    Bundle().apply {
                        textArg = post.id.toString()
                    })
                viewModel.edit(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    })
                viewModel.edit(post)

            }

            @SuppressLint("QueryPermissionsNeeded")
            override fun onplayVideo(post: Post) {

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                startActivity(intent)
            }

        }

        )
        binding.post.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
            binding.swipeRefreshLayout.isRefreshing = state.refreshing
        }
        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        viewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id == 0L) {
                return@observe
            }
            with(binding.content) {
                requestFocus()
                setText(post.content)
            }
        }


        binding.save.setOnClickListener {
            viewModel.changeContent(binding.content.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
            binding.editGroup.visibility = View.INVISIBLE
        }


        binding.deleted.setOnClickListener {
            with(binding.content) {
                viewModel.clear()
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.editGroup.visibility = View.INVISIBLE
            }
            return@setOnClickListener
        }


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        with(binding.swipeRefreshLayout) {
            setOnRefreshListener {
                viewModel.refreshPosts()
            }

            setSize(CircularProgressDrawable.LARGE)
        }

        return binding.root

    }
}
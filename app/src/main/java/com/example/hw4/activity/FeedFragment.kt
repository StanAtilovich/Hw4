package com.example.hw4.activity


import PostViewModel
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.hw4.util.AndroidUtils
import com.example.hw4.DTO.Post
import com.example.hw4.R
import com.example.hw4.activity.NewPostFragment.Companion.textArg

import com.example.hw4.adapter.OnInteractionListener
import com.example.hw4.adapter.PostAdapter
import com.example.hw4.databinding.FragmentFeedBinding
import com.example.hw4.viewModel.AuthViewModel

import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext


class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()

    private val viewModelAuth: AuthViewModel by viewModels(ownerProducer = ::requireParentFragment)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container, false
        )


        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

           override fun PhotoClick(post: Post) { //onPostClick
               findNavController().navigate(
                   R.id.action_feedFragment_to_photo,
                   Bundle().apply {
                       textArg = post.attachment?.url
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
                        textArg = post.attachment?.url
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
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefreshLayout.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.errorGroup.isVisible = state.empty
        }


        viewModel.newerCount.observe(viewLifecycleOwner) {
            binding.newerCount.isVisible = false
            CoroutineScope(EmptyCoroutineContext).launch {
                viewModel.newPostView()
                delay(25)
            }
            binding.post.smoothScrollToPosition(0)

        }

        viewModel.newerCount.observe(viewLifecycleOwner) { state ->
            binding.newerCount.isVisible = state > 0
        }

        binding.newerCount.setOnClickListener {
            viewModel.readAll()
            binding.newerCount.isVisible = false
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshPosts()
        }


        binding.retryButton.setOnClickListener {
            viewModel.newPostView()//loadPosts()
            binding.retryButton.isVisible = false
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

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(postitionStart: Int, itemCount: Int) {
                if (postitionStart == 0) {
                    binding.post.smoothScrollToPosition(0)
                }
            }
        })



        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root

    }

    private fun authenticate() = findNavController().navigate(R.id.action_feedFragment_to_singInFragment)
}
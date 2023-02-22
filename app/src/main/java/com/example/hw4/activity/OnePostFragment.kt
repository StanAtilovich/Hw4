package com.example.hw4.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.hw4.DTO.Post
import com.example.hw4.R
import com.example.hw4.activity.NewPostFragment.Companion.textArg
import com.example.hw4.adapter.OnInteractionListener
import com.example.hw4.adapter.PostViewHolder
import com.example.hw4.databinding.FragmentOnePostBinding

import com.example.hw4.viewModel.PostViewModel


class OnePostFragment : Fragment() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOnePostBinding.inflate(
            inflater,
            container, false
        )
               val viewModel: PostViewModel by viewModels(
            ownerProducer = ::requireParentFragment
        )
        val viewHolder = PostViewHolder(binding.onePost, object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)

                viewModel.sharing(post.id)
            }

            override fun onPostClick(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_onePostFragment,
                    Bundle().apply {

                    })
                viewModel.edit(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            @SuppressLint("QueryPermissionsNeeded")
            override fun onplayVideo(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                startActivity(intent)
            }

        })


        //   binding.post.adapter = adapter
        //   viewModel.data.observe(viewLifecycleOwner) { posts ->
        //       adapter.submitList(posts)
        //   }
//
           viewModel.edited.observe(viewLifecycleOwner) { post ->
               if (post.id == 0L) {
                   findNavController().navigate(R.id.action_feedFragment_to_newPostFragment,
                   Bundle().apply
                    { textArg = post.content })
               } else {
                   return@observe
               }
               viewHolder.bind(post)
             // with(binding.content) {
             //     requestFocus()
             //     setText(post.content)
             // }
           }
//
//
        //   binding.save.setOnClickListener {
       //      viewModel.changeContent(binding.content.text.toString())
       //      viewModel.save()
       //      AndroidUtils.hideKeyboard(requireView())
       //      findNavController().navigateUp()
       //      binding.editGroup.visibility = View.INVISIBLE
       //  }
//
//
        //   binding.deleted.setOnClickListener {
        //       with(binding.content) {
        //           viewModel.clear()
        //           setText("")
        //           clearFocus()
        //           AndroidUtils.hideKeyboard(this)
        //           binding.editGroup.visibility = View.INVISIBLE
        //       }
        //       return@setOnClickListener
        //   }
//
//
        //   binding.fab.setOnClickListener {
        //       findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        //   }
        return binding.root
//
    }
}
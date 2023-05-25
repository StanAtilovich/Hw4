package com.example.hw4.activity

import PostViewModel
import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile

import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.hw4.R
import com.example.hw4.databinding.FragmentNewPostBinding
import com.example.hw4.util.AndroidUtils
import com.example.hw4.util.StringArg

import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


class NewPostFragment : Fragment() {
    companion object {
        var Bundle.textArg: String? by StringArg
    }


    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    @OptIn(ExperimentalContracts::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.textArg?.let(binding.edit::setText)

        val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode){
                ImagePicker.RESULT_ERROR -> Snackbar.make(binding.root,
                    ImagePicker.getError(data), Snackbar.LENGTH_SHORT).show()
                Activity.RESULT_OK -> {
                    val fileUri = data?.data

                    viewModel.changePhoto(fileUri, fileUri?.toFile())
                }
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()

        }

        viewModel.photo.observe(viewLifecycleOwner){
            binding.photo.setImageURI(it.uri)
            binding.photoContainer.isVisible = it.uri !=null
        }


        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .galleryOnly()
                .compress(2048)
                .createIntent(startForProfileImageResult::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .cameraOnly()
                .compress(2048)
                .createIntent(startForProfileImageResult::launch)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.deletePhoto()
        }


        binding.edit.requestFocus()




        activity?.addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post,menu)
            }



          override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
              return when(menuItem.itemId){
                  R.id.ok-> {
                      viewModel.changeContent(binding.edit.text.toString())
                      viewModel.save()
                      AndroidUtils.hideKeyboard(requireView())
                      true
                  }
                  else -> false
              }
          }
    }, viewLifecycleOwner)



        return binding.root



    }


}
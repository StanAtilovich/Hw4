package com.example.hw4.activity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.hw4.BuildConfig.BASE_URL
import com.example.hw4.R
import com.example.hw4.databinding.FragmentOnePhotoBinding
import com.example.hw4.util.LongArg


class PhotoView: Fragment() {
    companion object{
    var Bundle.textArg: String? by LongArg
}


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOnePhotoBinding.inflate(
            inflater,
            container,
            false
        )


        val  imageUrl = "${BASE_URL}/media/${arguments?.textArg}"

        Glide.with(binding.imageViewPicture)
            .load(imageUrl)
            .placeholder(R.drawable.ic_baseline_downloading_24)
            .timeout(10000)
            .into(binding.imageViewPicture)
        return binding.root
    }

}
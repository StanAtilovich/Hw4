package com.example.hw4.activity

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import auth.AppAuth
import com.example.hw4.R
import com.example.hw4.databinding.SingInBinding
import com.example.hw4.viewModel.SingInViewModel
import com.google.android.material.snackbar.Snackbar

class SingInFragment: Fragment() {

    private val viewModel: SingInViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = SingInBinding.inflate(inflater, container, false)

        viewModel.data.observe(viewLifecycleOwner) {
            AppAuth.getInstance().setAuth(it.id, it.token)
            findNavController().navigateUp()
        }

        viewModel.state.observe(viewLifecycleOwner){
            state ->
            if (state.loginError){
                binding.password.error = getString(R.string.password_error)
            }
        }
        with(binding){
            login.requestFocus()
            singInB.setOnClickListener {
                println(R.string.pushed_button)
                viewModel.loginAttempt(
                    login.text.toString(),
                    password.text.toString()
                )
            }
        }
        return binding.root
    }



}
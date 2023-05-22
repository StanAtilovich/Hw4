package com.example.hw4.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.hw4.R
import com.example.hw4.databinding.SingInBinding
import com.example.hw4.viewModel.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SingInFragment : Fragment() {

    private val viewModel: SignInViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = SingInBinding.inflate(inflater, container, false)

        viewModel.data.observe(viewLifecycleOwner) {
            viewModel.auth.setAuth(it.id, it.token)
            findNavController().navigateUp()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.loginError) {
                binding.password.error = getString(R.string.password_error)
            }
        }
        with(binding) {
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
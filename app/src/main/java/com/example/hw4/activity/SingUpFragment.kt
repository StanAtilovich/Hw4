package com.example.hw4.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import auth.AppAuth
import com.example.hw4.R
import com.example.hw4.databinding.SignUpBinding
import com.example.hw4.viewModel.SingUpViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar

class SingUpFragment : Fragment() {

    private val viewModel: SingUpViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = SignUpBinding.inflate(inflater, container, false)

        viewModel.data.observe(viewLifecycleOwner) {
                AppAuth.getInstance().setAuth(it.id, it.token)
                findNavController().navigateUp()
            }

            viewModel.state.observe(viewLifecycleOwner) { state ->
                if (state.registrationError)
                    Snackbar.make(binding.root, R.string.error_loading, LENGTH_INDEFINITE)
                        .setAction(R.string.retry_loading) {
                            viewModel.registration(
                                binding.login.text.toString(),
                                binding.password.text.toString(),
                                binding.name.text.toString()
                            )
                        }.show()
            }
            with(binding) {
                signUpButton.setOnClickListener {
                    if (password.text.toString() == repeatPassword.text.toString()) {
                        viewModel.registration(
                            login.text.toString(),
                            password.text.toString(),
                            name.text.toString()
                        )
                    } else {
                        repeatPassword.error = getString(R.string.password_error)
                    }
                }
            }

        return binding.root
    }

}
package com.example.hw4.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.example.hw4.R
import com.example.hw4.activity.NewPostFragment.Companion.textArg
import com.example.hw4.auth.AppAuth
import com.example.hw4.viewModel.AuthViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject




@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {
    @Inject
    lateinit var appAuth: AppAuth
    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging


    private val authViewModel:AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }


            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    textArg = text
                }
            )
        }


        var previousMenuProvider: MenuProvider? = null
        authViewModel.data.observe(this) {
            previousMenuProvider?.let(::removeMenuProvider)
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)

                    menu.setGroupVisible(R.id.unauthorized, !authViewModel.authorized)
                    menu.setGroupVisible(R.id.authorized, authViewModel.authorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {


                        R.id.singIn -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_feedFragment_to_singInFragment)
                            true
                        }

                        R.id.SingUp -> {

                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_feedFragment_to_singUpFragment)
                            true
                        }

                        R.id.singOut -> {
                            appAuth.removeAuth()
                            true
                        }

                        else -> false
                    }


            }.also {
                previousMenuProvider = it
            })

        }



        checkGoogleApiAvailability()
    }



    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }
        firebaseMessaging.token.addOnSuccessListener {
            println(it)
        }
    }
}
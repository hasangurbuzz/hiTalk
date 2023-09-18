package dev.hasangurbuz.hitalk.ui.auth.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.databinding.FragmentLoginBinding
import dev.hasangurbuz.hitalk.ui.BindingFragment
import dev.hasangurbuz.hitalk.ui.auth.AuthViewModel
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.countryCode.registerCarrierNumberEditText(binding.inputNumber)

        subscribeToEvents()
        setListeners()
    }


    private fun setListeners() {
        binding.btnLogin.setOnClickListener {
            if (binding.countryCode.isValidFullNumber) {
                authViewModel.startAuthentication(binding.countryCode.fullNumberWithPlus, requireActivity())
            }
        }


        binding.inputNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!binding.countryCode.isValidFullNumber) {
                    binding.containerInputNumber.apply {
                        this.isErrorEnabled = true
                        this.error = "Phone number is not valid"
                    }
                    return
                }
                binding.containerInputNumber.isErrorEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


    }

    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.authState.collect { authEvent ->
                        Log.e("AUTH", "event triggered")
                        when (authEvent) {
                            is AuthViewModel.AuthEvent.Next -> {
                                Log.e("AUTH", "pass to next fragment")
                                val action =
                                    LoginFragmentDirections.actionLoginFragmentToSmsFragment()
                                findNavController().navigate(action)
                                return@collect
                            }

                            is AuthViewModel.AuthEvent.Success -> {
                                val action =
                                    LoginFragmentDirections.actionLoginFragmentToAppActivity(authViewModel.currentUser.value!!)
                                findNavController().navigate(action)
                                requireActivity().finish()
                                return@collect
                            }

                            is AuthViewModel.AuthEvent.Failed -> {
                                snackBar(authEvent.message).show()
                            }
                        }

                    }
                }
                launch {
                    authViewModel.authCredential.collect { credential ->
                        credential?.let {
                            authViewModel.signin(credential)
                        }
                    }
                }
            }

        }
    }

    private fun snackBar(text: String): Snackbar {
        return Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_SHORT
        ).setAction("Ok") {

        }
    }


}


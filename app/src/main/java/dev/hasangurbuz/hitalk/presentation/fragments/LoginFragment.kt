package dev.hasangurbuz.hitalk.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.databinding.FragmentLoginBinding
import dev.hasangurbuz.hitalk.presentation.activities.AuthActivity
import dev.hasangurbuz.hitalk.presentation.base.fragments.BindingFragment
import dev.hasangurbuz.hitalk.presentation.events.LoginEvent
import dev.hasangurbuz.hitalk.presentation.util.UIUtils
import dev.hasangurbuz.hitalk.presentation.viewmodels.AuthViewModel
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
                authViewModel.onLoginEvent(LoginEvent.LoginClick)
            }
        }

        binding.inputNumber.addTextChangedListener(
            UIUtils.inputListener {
                if (!binding.countryCode.isValidFullNumber) {
                    binding.containerInputNumber.apply {
                        this.isErrorEnabled = true
                        this.error = "Phone number is not valid"
                    }
                    return@inputListener
                }
                binding.containerInputNumber.isErrorEnabled = false
                authViewModel.onLoginEvent(LoginEvent.PhoneNumberChanged(binding.countryCode.fullNumberWithPlus))
            }
        )
    }

    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.eventFlow.collect { event ->
                        when (event) {
                            is AuthViewModel.UIEvent.Failed -> {
                                setLoading(false)
                                snackBar(event.message).show()
                            }

                            is AuthViewModel.UIEvent.NavigateSMS -> {
                                setLoading(false)
                                val action =
                                    LoginFragmentDirections.actionLoginFragmentToSmsFragment()
                                findNavController().navigate(action)
                            }

                            is AuthViewModel.UIEvent.SnackBar -> {
                                snackBar(event.message).show()
                                setLoading(false)
                            }

                            is AuthViewModel.UIEvent.Success -> {}
                            is AuthViewModel.UIEvent.Loading -> {
                                setLoading(true)
                            }
                        }
                    }
                }
            }

        }
    }

    private fun setLoading(isLoading: Boolean) {
        (requireActivity() as AuthActivity).setLoading(isLoading)
    }


}


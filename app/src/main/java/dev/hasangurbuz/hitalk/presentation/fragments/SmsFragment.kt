package dev.hasangurbuz.hitalk.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.databinding.FragmentSmsBinding
import dev.hasangurbuz.hitalk.presentation.activities.AuthActivity
import dev.hasangurbuz.hitalk.presentation.base.fragments.BindingFragment
import dev.hasangurbuz.hitalk.presentation.viewmodels.AuthViewModel
import dev.hasangurbuz.hitalk.presentation.viewmodels.SmsViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SmsFragment : BindingFragment<FragmentSmsBinding>() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private val smsViewModel: SmsViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentSmsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToEvents()
        setListeners()

    }

    private fun setListeners() {
        binding.btnContinue.setOnClickListener {
            val code = binding.inputSmsCode.text.trim()
            if (code.length != 6) {
                return@setOnClickListener
            }
            if (code.isBlank()) {
                return@setOnClickListener
            }
            authViewModel.verifySmsCode(code.toString())
        }
    }

    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                launch {
//                    authViewModel.authState.collect { authState ->
//                        when (authState) {
//                            is AuthViewModel.AuthEvent.Success -> {
//                                // success
//                            }
//
//                            is AuthViewModel.AuthEvent.Failed -> {
//                                findNavController().popBackStack()
//                            }
//
//                            is AuthViewModel.AuthEvent.Next -> {
//                                val action = SmsFragmentDirections.ac()
//                                findNavController().navigate(action)
//                            }
//                        }
//                    }
//                }
                launch {
                    smsViewModel.countdownState.collect { seconds ->
                        binding.textSeconds.text = seconds.toString()
                    }
                }
                launch {
                    authViewModel.eventFlow.collect { event ->
                        when (event) {
                            is AuthViewModel.UIEvent.Failed -> {
                                setLoading(false)
                                findNavController().popBackStack()
                            }

                            is AuthViewModel.UIEvent.NavigateSMS -> {}
                            is AuthViewModel.UIEvent.SnackBar -> {
                                setLoading(false)
                                snackBar(event.message)
                            }

                            is AuthViewModel.UIEvent.Success -> {
                                val action = SmsFragmentDirections.actionSmsFragmentToAppActivity()
                                findNavController().navigate(action)
                                setLoading(false)
                            }

                            is AuthViewModel.UIEvent.Loading -> setLoading(true)

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
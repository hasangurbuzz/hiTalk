package dev.hasangurbuz.hitalk.presentation.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.databinding.FragmentRegisterBinding
import dev.hasangurbuz.hitalk.presentation.activities.AuthActivity
import dev.hasangurbuz.hitalk.presentation.base.fragments.BindingFragment
import dev.hasangurbuz.hitalk.presentation.events.RegisterEvent
import dev.hasangurbuz.hitalk.presentation.util.UIUtils
import dev.hasangurbuz.hitalk.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BindingFragment<FragmentRegisterBinding>() {
    private val IMAGE_PICK_REQUEST = 0
    private val authViewModel: AuthViewModel by activityViewModels()
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentRegisterBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = authViewModel
        binding.lifecycleOwner = this
        super.onViewCreated(view, savedInstanceState)
        binding.countryCode.registerCarrierNumberEditText(binding.inputNumber)
        subscribeToEvents()
        setListeners()
    }


    private fun setListeners() {
        binding.imageProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
        }

        binding.inputNumber.addTextChangedListener(UIUtils.inputListener {
            authViewModel.onRegisterEvent(RegisterEvent.PhoneNumberChanged(binding.countryCode.fullNumberWithPlus))
            if (!binding.countryCode.isValidFullNumber) {
                binding.containerInputNumber.apply {
                    this.isErrorEnabled = true
                    this.error = "Phone number is not valid"
                }
                return@inputListener
            }
            binding.containerInputNumber.isErrorEnabled = false
        })
        binding.inputName.addTextChangedListener(UIUtils.inputListener { text ->
            if (text.isBlank()) {
                binding.containerInputName.apply {
                    this.isErrorEnabled = true
                    this.error = "Phone number is not valid"
                }
            }
            if (text.isNotBlank()) {
                binding.containerInputName.isErrorEnabled = false
            }
            if (text.contains("  ")) {
                val cleanedText = text.replace("  ", " ")
                binding.inputName.setText(cleanedText)
                binding.inputName.setSelection(cleanedText.length)
            }
            authViewModel.onRegisterEvent(RegisterEvent.UsernameChanged(text))
        }
        )

        binding.btnRegister.setOnClickListener {
            if (!binding.countryCode.isValidFullNumber) {
                return@setOnClickListener
            }

            if (binding.containerInputName.isErrorEnabled) {
                return@setOnClickListener
            }

//            if (viewModel.imageUri.value == null) {
//                snackBar("Profile image required")
//                return@setOnClickListener
//            }


            authViewModel.onRegisterEvent(RegisterEvent.RegisterClick)
        }


    }

    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.registerInputState.collect { state ->
                        if (state.imageUri != Uri.EMPTY) {
                            Glide.with(this@RegisterFragment)
                                .load(state.imageUri)
                                .into(binding.imageProfile)
                            binding.imageProfilePlaceholder.visibility = View.GONE
                        } else {
                            binding.imageProfilePlaceholder.visibility = View.VISIBLE
                        }
                    }
                }
                launch {
                    authViewModel.eventFlow.collect { event ->
                        when (event) {
                            is AuthViewModel.UIEvent.Failed -> {
                                setLoading(false)
                                snackBar(event.message)
                            }

                            is AuthViewModel.UIEvent.NavigateSMS -> {
                                setLoading(false)
                                val action =
                                    RegisterFragmentDirections.actionRegisterFragmentToSmsFragment()
                                findNavController().navigate(action)
                            }

                            is AuthViewModel.UIEvent.SnackBar -> {
                                snackBar(event.message).show()
                                setLoading(false)
                            }

                            is AuthViewModel.UIEvent.Success -> TODO()
                            is AuthViewModel.UIEvent.Loading -> {
                                setLoading(true)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                snackBar("Image could not be loaded")
                return
            }
            val imageUri = data.data!!
            authViewModel.onRegisterEvent(RegisterEvent.ImageChanged(imageUri))
        }
    }

    private fun setLoading(isLoading: Boolean) {
        (requireActivity() as AuthActivity).setLoading(isLoading)
    }

}

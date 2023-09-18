package dev.hasangurbuz.hitalk.ui.auth.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.databinding.FragmentRegisterBinding
import dev.hasangurbuz.hitalk.model.User
import dev.hasangurbuz.hitalk.ui.BindingFragment
import dev.hasangurbuz.hitalk.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BindingFragment<FragmentRegisterBinding>() {
    private val IMAGE_PICK_REQUEST = 0
    private val viewModel: RegisterViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentRegisterBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

        binding.inputNumber.addTextChangedListener(inputNumberListener)
        binding.inputName.addTextChangedListener(inputNameListener)

        binding.btnRegister.setOnClickListener {
            if (!binding.countryCode.isValidFullNumber) {
                return@setOnClickListener
            }

            if (binding.containerInputName.isErrorEnabled) {
                return@setOnClickListener
            }

            if (viewModel.imageUri.value == null) {
                snackBar("Profile image required")
                return@setOnClickListener
            }

            val user = User()
            user.name = binding.inputName.text.toString().trim()
            user.phoneNumber = binding.countryCode.fullNumberWithPlus
            user.imageUri = viewModel.imageUri.value
            authViewModel.register(user, requireActivity())
        }


    }


    private val inputNumberListener = object : TextWatcher {
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
    }

    private val inputNameListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = s.toString()
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
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.imageUri.collect { uri ->
                        if (uri == null) {
                            binding.imageProfilePlaceholder.visibility = View.VISIBLE
                            return@collect
                        }
                        Glide.with(this@RegisterFragment)
                            .load(uri)
                            .into(binding.imageProfile)
                        binding.imageProfilePlaceholder.visibility = View.GONE
                    }
                }
                launch {
                    authViewModel.authState.collect { authEvent ->
                        if (authEvent is AuthViewModel.AuthEvent.Next) {
                            val action =
                                RegisterFragmentDirections.actionRegisterFragmentToSmsFragment()
                            findNavController().navigate(action)
                        } else if (authEvent is AuthViewModel.AuthEvent.Failed) {
                            snackBar(authEvent.message).show()
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
            viewModel.setImageUri(data.data.toString())

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

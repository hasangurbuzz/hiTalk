package dev.hasangurbuz.hitalk.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.databinding.FragmentEntryBinding
import dev.hasangurbuz.hitalk.presentation.base.fragments.BindingFragment
import dev.hasangurbuz.hitalk.presentation.viewmodels.AuthViewModel

@AndroidEntryPoint
class EntryFragment : BindingFragment<FragmentEntryBinding>() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentEntryBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        subscribeEvents()
        setListeners()

    }

    private fun setListeners() {
        binding.btnLogin.setOnClickListener {
            val direction = EntryFragmentDirections.actionEntryFragmentToLoginFragment()
            findNavController().navigate(direction)
        }

        binding.btnSignup.setOnClickListener {
            val direction = EntryFragmentDirections.actionEntryFragmentToRegisterFragment()
            findNavController().navigate(direction)
        }
    }


}
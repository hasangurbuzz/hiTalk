package dev.hasangurbuz.hitalk.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import dev.hasangurbuz.hitalk.databinding.FragmentSplashBinding
import dev.hasangurbuz.hitalk.ui.BindingFragment
import dev.hasangurbuz.hitalk.ui.auth.AuthViewModel
import kotlinx.coroutines.launch


class SplashFragment : BindingFragment<FragmentSplashBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentSplashBinding::inflate

    private val authViewModel: AuthViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                val user = authViewModel.getLoggedUser()
                if (user == null) {
                    val action = SplashFragmentDirections.actionSplashFragmentToEntryFragment()
                    findNavController().navigate(action)
                } else {
                    val action = SplashFragmentDirections.actionSplashFragmentToAppActivity(user)
                    findNavController().navigate(action)
                    requireActivity().finish()
                }

            }
        }
    }
}
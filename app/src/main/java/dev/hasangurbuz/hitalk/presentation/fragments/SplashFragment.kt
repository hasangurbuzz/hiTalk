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
import dev.hasangurbuz.hitalk.core.AuthContext
import dev.hasangurbuz.hitalk.databinding.FragmentSplashBinding
import dev.hasangurbuz.hitalk.presentation.base.fragments.BindingFragment
import dev.hasangurbuz.hitalk.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BindingFragment<FragmentSplashBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentSplashBinding::inflate

    @Inject
    lateinit var authContext: AuthContext
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
                    authContext.currentUser = user

                    val action = SplashFragmentDirections.actionSplashFragmentToAppActivity()
                    findNavController().navigate(action)
                    requireActivity().finish()
                }

            }
        }
    }
}
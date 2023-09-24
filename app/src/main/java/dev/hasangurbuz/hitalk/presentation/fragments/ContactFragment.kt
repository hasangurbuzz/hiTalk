package dev.hasangurbuz.hitalk.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.data.repository.ContactRepositoryImpl
import dev.hasangurbuz.hitalk.databinding.FragmentContactBinding
import dev.hasangurbuz.hitalk.domain.model.User
import dev.hasangurbuz.hitalk.domain.repository.ContactRepository
import dev.hasangurbuz.hitalk.presentation.adapter.ContactAdapter
import dev.hasangurbuz.hitalk.presentation.adapter.ItemClickListener
import dev.hasangurbuz.hitalk.presentation.base.fragments.BindingFragment
import dev.hasangurbuz.hitalk.presentation.events.ContactEvent
import dev.hasangurbuz.hitalk.presentation.viewmodels.ContactViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ContactFragment : BindingFragment<FragmentContactBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentContactBinding::inflate

    private val contactViewModel: ContactViewModel by viewModels()
    private lateinit var adapter: ContactAdapter

    @Inject
    lateinit var contactRepository: ContactRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ContactAdapter(mutableListOf(), itemClickListener)

        binding.recyclerContacts.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
        }
        subscribeToEvents()
    }

    private val itemClickListener =
        object : ItemClickListener<User> {
            override fun onClick(item: User) {
                contactViewModel.onEvent(ContactEvent.ContactClick(item))
            }
        }

    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    contactViewModel.contacts.collect { users ->
                        adapter.addItems(users)
                    }
                }

                launch {
                    contactViewModel.eventFlow.collect { event ->
                        when (event) {
                            is ContactViewModel.UIEvent.NavigateChat -> {
                                val action =
                                    ContactFragmentDirections.actionContactFragmentToChatFragment(
                                        event.conversation
                                    )
                                findNavController().navigate(action)
                            }

                            is ContactViewModel.UIEvent.ShowSnackBar -> TODO()
                        }
                    }
                }
            }
        }
    }


}
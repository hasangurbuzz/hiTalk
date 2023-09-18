package dev.hasangurbuz.hitalk.ui.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.adapter.ContactAdapter
import dev.hasangurbuz.hitalk.adapter.ItemClickListener
import dev.hasangurbuz.hitalk.databinding.FragmentContactBinding
import dev.hasangurbuz.hitalk.model.Conversation
import dev.hasangurbuz.hitalk.model.User
import dev.hasangurbuz.hitalk.ui.AppViewModel
import dev.hasangurbuz.hitalk.ui.BindingFragment
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactFragment : BindingFragment<FragmentContactBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentContactBinding::inflate

    private val appViewModel: AppViewModel by activityViewModels()
    private val contactViewModel: ContactViewModel by viewModels()
    private lateinit var adapter: ContactAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ContactAdapter(mutableListOf(), itemClickListener)

        binding.recyclerContacts.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
        }
        subscribeToEvents()
        contactViewModel.loadUsers()
    }

    private val itemClickListener = object : ItemClickListener<User> {
        override fun onClick(item: User) {
//            val participants = mutableListOf(
//                item, appViewModel.appUser.value!!
//            )
//
//            val conversation = findConversation(item.id!!)
//
//            if (conversation != null) {
//                val action =
//                    ContactFragmentDirections.actionContactFragmentToChatFragment(conversation)
//                findNavController().navigate(action)
//            } else {
//                contactViewModel.createConversation(participants)
//            }

        }

    }

    private fun findConversation(userId: String): Conversation? {
        for (entry in appViewModel.mapOfTwo.entries) {
            if (userId == entry.value) {
                return entry.key
            }
        }
        return null
    }

    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    contactViewModel.users.collect { users ->
                        adapter.addItems(users)
                    }
                }
                launch {
                    contactViewModel.conversation.collect { conversation ->
                        if (conversation == null) {
                            return@collect
                        }
//                        val action = ContactFragmentDirections.actionContactFragmentToChatFragment(
//                            conversation
//                        )
//                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

}
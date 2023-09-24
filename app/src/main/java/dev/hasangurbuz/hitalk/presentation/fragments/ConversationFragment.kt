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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.databinding.FragmentConversationBinding
import dev.hasangurbuz.hitalk.domain.model.ConversationItem
import dev.hasangurbuz.hitalk.presentation.activities.AppActivity
import dev.hasangurbuz.hitalk.presentation.adapter.ConversationListAdapter
import dev.hasangurbuz.hitalk.presentation.adapter.ItemClickListener
import dev.hasangurbuz.hitalk.presentation.base.fragments.BindingFragment
import dev.hasangurbuz.hitalk.presentation.events.ConversationEvent
import dev.hasangurbuz.hitalk.presentation.viewmodels.AppViewModel
import dev.hasangurbuz.hitalk.presentation.viewmodels.ConversationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConversationFragment : BindingFragment<FragmentConversationBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentConversationBinding::inflate

    private val appViewModel: AppViewModel by activityViewModels()
    private val viewModel: ConversationViewModel by viewModels()
    private lateinit var newAdapter: ConversationListAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newAdapter = ConversationListAdapter(itemClickListener)

        binding.recyclerConversations.let {
            it.layoutManager = LinearLayoutManager(requireActivity())
            it.adapter = newAdapter
        }

        subscribeToEvents()
        setListeners()


    }

    private val itemClickListener = object : ItemClickListener<ConversationItem> {
        override fun onClick(item: ConversationItem) {
            viewModel.onEvent(ConversationEvent.ConversationClick(item.conversation))
        }
    }

    private fun setListeners() {
        binding.fabAdd.setOnClickListener {
            viewModel.onEvent(ConversationEvent.FabClick)
        }
    }

    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.conversationItems.collectLatest { conversations ->
                        newAdapter.submitList(conversations)
                        setVisibility(binding.textNotFoundConversation, false)

                    }
                }
                launch {
                    viewModel.eventFlow.collect { event ->
                        when (event) {
                            is ConversationViewModel.UIEvent.StartChat -> {
                                val action =
                                    ConversationFragmentDirections.actionHomeFragmentToChatFragment(
                                        event.conversation
                                    )
                                findNavController().navigate(action)
                            }

                            is ConversationViewModel.UIEvent.NavigateContacts -> {
                                val action =
                                    ConversationFragmentDirections.actionHomeFragmentToContactFragment()
                                findNavController().navigate(action)
                            }

                            is ConversationViewModel.UIEvent.NotFoundConversation -> {
                                setVisibility(binding.textNotFoundConversation, true)
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun setVisibility(view: View, isVisible: Boolean) {
        if (isVisible) {
            view.visibility = View.VISIBLE
            return
        }
        view.visibility = View.GONE
    }

}
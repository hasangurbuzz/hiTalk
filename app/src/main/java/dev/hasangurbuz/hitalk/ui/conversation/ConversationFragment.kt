package dev.hasangurbuz.hitalk.ui.conversation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.adapter.ConversationAdapter
import dev.hasangurbuz.hitalk.adapter.ConversationListAdapter
import dev.hasangurbuz.hitalk.adapter.ItemClickListener
import dev.hasangurbuz.hitalk.databinding.FragmentConversationBinding
import dev.hasangurbuz.hitalk.model.ConversationItem
import dev.hasangurbuz.hitalk.ui.AppViewModel
import dev.hasangurbuz.hitalk.ui.BindingFragment
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConversationFragment : BindingFragment<FragmentConversationBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentConversationBinding::inflate

    private val appViewModel: AppViewModel by activityViewModels()
    private val adapter = ConversationAdapter(mutableListOf())
    private lateinit var newAdapter: ConversationListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newAdapter = ConversationListAdapter(itemClickListener)

        binding.recyclerConversations.let {
            it.layoutManager = LinearLayoutManager(requireActivity())
            it.adapter = newAdapter
        }

        subscribeToEvents()
        appViewModel.loadConversations()
        appViewModel.load()

        binding.fabAdd.setOnClickListener {
            val action = ConversationFragmentDirections.actionHomeFragmentToContactFragment()
            findNavController().navigate(action)
        }
    }

    private val itemClickListener = object : ItemClickListener<ConversationItem> {
        override fun onClick(item: ConversationItem) {
            val action =
                ConversationFragmentDirections.actionHomeFragmentToChatFragment(item.conversation!!)
            findNavController().navigate(action)
        }
    }

    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                appViewModel.conversationItems.collect { conversations ->
                    Log.e("SUB", "data size: ${conversations.size}")
                    newAdapter.submitList(conversations)
                }
            }
        }
    }

}
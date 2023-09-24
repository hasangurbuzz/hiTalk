package dev.hasangurbuz.hitalk.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.core.AuthContext
import dev.hasangurbuz.hitalk.databinding.FragmentChatBinding
import dev.hasangurbuz.hitalk.presentation.adapter.ChatListAdapter
import dev.hasangurbuz.hitalk.presentation.base.fragments.BindingFragment
import dev.hasangurbuz.hitalk.presentation.events.ChatEvent
import dev.hasangurbuz.hitalk.presentation.util.UIUtils
import dev.hasangurbuz.hitalk.presentation.viewmodels.AppViewModel
import dev.hasangurbuz.hitalk.presentation.viewmodels.ChatViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BindingFragment<FragmentChatBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChatBinding::inflate

    private val args: ChatFragmentArgs by navArgs()
    private val chatViewModel: ChatViewModel by viewModels()
    private val appViewModel: AppViewModel by activityViewModels()
    private lateinit var newAdapter: ChatListAdapter

    @Inject
    lateinit var authContext: AuthContext

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = chatViewModel

        newAdapter = ChatListAdapter(authContext.currentUser!!.id)

        scrollToEnd()

        binding.recyclerMessages.let {
            it.adapter = newAdapter
            val layoutManager = LinearLayoutManager(requireContext())
            layoutManager.stackFromEnd = true
            it.layoutManager = layoutManager
        }

        subscribeToEvents()
        setListeners()

    }

    private fun setListeners() {
        binding.inputMessage.addTextChangedListener(UIUtils.inputListener {
            chatViewModel.onEvent(ChatEvent.MessageChanged(it))
        })

        binding.btnSend.setOnClickListener {
            chatViewModel.onEvent(ChatEvent.ClickSend)
        }

        binding.recyclerMessages.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                if (bottom < oldBottom) {
                    scrollToEnd()
                }
            }
        })

    }

    private fun scrollToEnd() {
        if (newAdapter.itemCount > 0) {
            binding.recyclerMessages.postDelayed({
                binding.recyclerMessages.smoothScrollToPosition(newAdapter.itemCount - 1)
            }, 10L)
        }
    }

    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    chatViewModel.chatState.collect {
                        newAdapter.submitList(it.messages)
                        scrollToEnd()
                    }
                }
            }
        }
    }
}
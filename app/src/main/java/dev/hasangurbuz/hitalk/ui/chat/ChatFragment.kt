package dev.hasangurbuz.hitalk.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.adapter.ChatListAdapter
import dev.hasangurbuz.hitalk.adapter.DateTimeUtil
import dev.hasangurbuz.hitalk.adapter.MessageAdapter
import dev.hasangurbuz.hitalk.databinding.FragmentChatBinding
import dev.hasangurbuz.hitalk.model.Message
import dev.hasangurbuz.hitalk.ui.AppViewModel
import dev.hasangurbuz.hitalk.ui.BindingFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@AndroidEntryPoint
class ChatFragment : BindingFragment<FragmentChatBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChatBinding::inflate

    private val args: ChatFragmentArgs by navArgs()
    private val chatViewModel: ChatViewModel by viewModels()
    private val appViewModel: AppViewModel by activityViewModels()
    private lateinit var adapter: MessageAdapter
    private lateinit var newAdapter: ChatListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatViewModel.setConversation(args.Conversation)

        adapter = MessageAdapter(mutableListOf(), appViewModel.appUser.value!!.id!!)
        newAdapter = ChatListAdapter(appViewModel.appUser.value!!.id!!)

        scrollToEnd()

        binding.recyclerMessages.let {
            it.adapter = newAdapter
            val layoutManager = LinearLayoutManager(requireContext())
            layoutManager.stackFromEnd = true
            it.layoutManager = layoutManager
        }

        subscribeToEvents()
        chatViewModel.loadMessages(args.Conversation.id!!)
        setListeners()

    }

    private fun setListeners() {
        binding.btnSend.setOnClickListener {
            val message = Message()
            message.content = binding.inputMessage.text.toString()
            message.id = UUID.randomUUID().toString()
            message.senderId = appViewModel.appUser.value!!.id!!
            message.timestamp = DateTimeUtil.toString(LocalDateTime.now())
            message.conversationId = args.Conversation.id

            chatViewModel.sendMessage(message)
        }

        binding.recyclerMessages.addOnLayoutChangeListener(object : View.OnLayoutChangeListener{
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
                if (bottom < oldBottom){
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
                chatViewModel.messages.collect {
                    newAdapter.submitList(it)
                    delay(100L)
                    scrollToEnd()
                }
            }
        }
    }
}
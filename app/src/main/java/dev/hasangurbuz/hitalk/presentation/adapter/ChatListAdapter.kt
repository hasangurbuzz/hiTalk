package dev.hasangurbuz.hitalk.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.hasangurbuz.hitalk.databinding.ItemContainerMessageReceivedBinding
import dev.hasangurbuz.hitalk.databinding.ItemContainerMessageSentBinding
import dev.hasangurbuz.hitalk.domain.model.Message
import dev.hasangurbuz.hitalk.presentation.util.UIUtils

class ChatListAdapter(
    private val userId: String
) :
    ListAdapter<Message, ViewHolder>(DiffCallback()) {

    private val VIEW_TYPE_SENT = 0
    private val VIEW_TYPE_RECEIVED = 1

    inner class SentMessageViewHolder(private val binding: ItemContainerMessageSentBinding) :
        ViewHolder(binding.root) {
        fun setData(message: Message) {
            binding.apply {
                this.message.text = message.content
                val timestamp = UIUtils.dateToHour(message.timestamp)
                this.timestamp.text = timestamp
            }
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemContainerMessageReceivedBinding) :
        ViewHolder(binding.root) {
        fun setData(message: Message) {
            binding.apply {
                this.message.text = message.content
                val timestamp = UIUtils.dateToHour(message.timestamp)
                this.timestamp.text = timestamp
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == VIEW_TYPE_SENT) {
            return SentMessageViewHolder(
                ItemContainerMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return ReceivedMessageViewHolder(
            ItemContainerMessageReceivedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemViewType(position: Int): Int {
        val isSent = isMessageSent(getItem(position))
        if (isSent) {
            return VIEW_TYPE_SENT
        }
        return VIEW_TYPE_RECEIVED
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = getItem(position)

        val viewType = getItemViewType(position)

        val viewHolder: ViewHolder

        if (viewType == VIEW_TYPE_SENT) {
            viewHolder = (holder as SentMessageViewHolder)
            viewHolder.setData(message)
            return
        }

        viewHolder = (holder as ReceivedMessageViewHolder)
        viewHolder.setData(message)
    }

    class DiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem == newItem
        }
    }

    private fun isMessageSent(message: Message): Boolean {
        return message.senderId == userId
    }
}
package dev.hasangurbuz.hitalk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.hasangurbuz.hitalk.databinding.ItemContainerMessageReceivedBinding
import dev.hasangurbuz.hitalk.databinding.ItemContainerMessageSentBinding
import dev.hasangurbuz.hitalk.model.Message

class MessageAdapter(private val messages: MutableList<Message>, private val userId: String) :
    RecyclerView.Adapter<ViewHolder>() {

    private val VIEW_TYPE_SENT = 0
    private val VIEW_TYPE_RECEIVED = 1

    fun addItems(items: List<Message>) {
        val lastSize = itemCount
        messages.addAll(items)
        notifyItemRangeInserted(lastSize, messages.size)
    }


    inner class SentMessageViewHolder(private val binding: ItemContainerMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(message: Message) {
            binding.apply {
                this.message.text = message.content
                this.timestamp.text = DateTimeUtil.dateToHour(message.timestamp!!)
            }
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemContainerMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(message: Message) {
            binding.apply {
                this.message.text = message.content
                this.timestamp.text = DateTimeUtil.dateToHour(message.timestamp!!)
            }
        }

    }

    private fun isMessageSent(message: Message): Boolean {
        return message.senderId.equals(userId)
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
        val isSent = isMessageSent(messages[position])
        if (isSent) {
            return VIEW_TYPE_SENT
        }
        return VIEW_TYPE_RECEIVED
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]

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
}
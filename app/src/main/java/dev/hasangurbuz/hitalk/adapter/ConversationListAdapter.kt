package dev.hasangurbuz.hitalk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.hasangurbuz.hitalk.databinding.ItemContainerConversationBinding
import dev.hasangurbuz.hitalk.model.ConversationItem
import dev.hasangurbuz.hitalk.model.Message

class ConversationListAdapter(private val itemClickListener: ItemClickListener<ConversationItem>) :
    ListAdapter<ConversationItem, ConversationListAdapter.ConversationViewHolder>(DiffCallback()) {

    inner class ConversationViewHolder(private val binding: ItemContainerConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(conversationItem: ConversationItem) {
            binding.apply {
                this.textConversationName.text = conversationItem.title
                this.textLastMessage.text = conversationItem.lastMessage
                val dateTime = conversationItem.timestamp
                val time = DateTimeUtil.dateToHour(dateTime!!)

                this.textLastMessageTime.text = time

                Glide.with(this.root)
                    .load(conversationItem.imageUri)
                    .into(this.imageProfile)
            }

            binding.root.setOnClickListener {
                itemClickListener.onClick(conversationItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        return ConversationViewHolder(
            ItemContainerConversationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = getItem(position)
        holder.setData(conversation)

    }

    class DiffCallback : DiffUtil.ItemCallback<ConversationItem>() {
        override fun areItemsTheSame(
            oldItem: ConversationItem,
            newItem: ConversationItem
        ): Boolean {
            return oldItem.conversation!!.id == newItem.conversation!!.id
        }

        override fun areContentsTheSame(
            oldItem: ConversationItem,
            newItem: ConversationItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
package dev.hasangurbuz.hitalk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.hasangurbuz.hitalk.databinding.ItemContainerConversationBinding
import dev.hasangurbuz.hitalk.model.ConversationItem
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ConversationAdapter(
    var conversationItems: MutableList<ConversationItem>
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    fun addItems(items: List<ConversationItem>) {
        val lastSize = itemCount
        conversationItems.addAll(items)
        notifyItemRangeInserted(lastSize, conversationItems.size)
    }


    inner class ConversationViewHolder(private val binding: ItemContainerConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(conversationItem: ConversationItem) {
            binding.apply {
                this.textConversationName.text = conversationItem.title
                this.textLastMessage.text = conversationItem.lastMessage
                val dateTime = conversationItem.timestamp
                val time =  DateTimeUtil.dateToHour(dateTime!!)

                this.textLastMessageTime.text = time

                Glide.with(this.root)
                    .load(conversationItem.imageUri)
                    .into(this.imageProfile)

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
        val conversation = conversationItems[position]
        holder.setData(conversation)
    }

    override fun getItemCount(): Int {
        return conversationItems.size
    }
}
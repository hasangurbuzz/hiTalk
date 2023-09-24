package dev.hasangurbuz.hitalk.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.hasangurbuz.hitalk.databinding.ItemContainerUserBinding
import dev.hasangurbuz.hitalk.domain.model.User

class ContactAdapter(
    private var users: MutableList<User>,
    private val itemClickListener: ItemClickListener<User>
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var _binding: ItemContainerUserBinding? = null

    fun addItems(userList: List<User>) {
        val lastSize = itemCount
        users.addAll(userList)
        notifyItemRangeInserted(lastSize, userList.size)
    }


    inner class ContactViewHolder(private val binding: ItemContainerUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            _binding = binding
        }

        fun setData(user: User) {
            binding.apply {
                this.textUsername.text = user.name

                Glide.with(binding.root)
                    .load(user.imageUri)
                    .into(this.imageProfile)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val user = users[position]

        _binding?.let {
            it.root.setOnClickListener {
                itemClickListener.onClick(user)
            }
        }

        holder.setData(user)
    }
}
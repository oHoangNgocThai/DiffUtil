package com.example.ngocthai.diffutilexample.ui

import android.databinding.DataBindingUtil
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ngocthai.diffutilexample.R
import com.example.ngocthai.diffutilexample.databinding.ItemContactBinding
import com.example.ngocthai.diffutilexample.entity.Contact

class ContactListAdapter : ListAdapter<Contact, ContactListAdapter.ViewHolder>(ContactDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.item_contact, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    class ViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Contact) {
            binding.contact = item
            binding.executePendingBindings()
        }
    }

    class ContactDiffCallBack : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldContactList: Contact, newContactList: Contact): Boolean {
            return oldContactList.id == newContactList.id
        }

        override fun areContentsTheSame(oldContactList: Contact, newContactList: Contact): Boolean {
            return oldContactList == newContactList
        }
    }
}

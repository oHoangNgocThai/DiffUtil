package com.example.ngocthai.diffutilexample.ui

import android.databinding.DataBindingUtil
import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ngocthai.diffutilexample.R
import com.example.ngocthai.diffutilexample.databinding.ItemContactBinding
import com.example.ngocthai.diffutilexample.entity.Contact

class ContactAsyncDifferAdapter : RecyclerView.Adapter<ContactAsyncDifferAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.item_contact, parent, false))
    }

    override fun getItemCount() = mDiffer.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binData(mDiffer.currentList[position])
    }

    class ViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binData(item: Contact) {
            binding.contact = item
            binding.executePendingBindings()
        }
    }

    fun submitList(list: List<Contact>) {
        mDiffer.submitList(list)
    }

    private val mDiffer = AsyncListDiffer<Contact>(this, object : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldContact: Contact, newContact: Contact): Boolean {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldContact.id == newContact.id
        }

        override fun areContentsTheSame(oldContact: Contact, newContact: Contact): Boolean {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldContact.equals(newContact)
        }

    })
}
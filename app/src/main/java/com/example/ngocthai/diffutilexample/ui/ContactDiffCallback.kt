package com.example.ngocthai.diffutilexample.ui

import android.support.annotation.Nullable
import android.support.v7.util.DiffUtil
import com.example.ngocthai.diffutilexample.entity.Contact

class ContactDiffCallback(
        private val mOldContactList: List<Contact>,
        private val mNewContactList: List<Contact>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return mOldContactList[oldPosition].id == mNewContactList[newPosition].id
    }

    override fun getOldListSize() = mOldContactList.size

    override fun getNewListSize() = mNewContactList.size

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        val oldContact = mOldContactList[oldPosition]
        val newContact = mNewContactList[newPosition]
        return (oldContact.name == newContact.name && oldContact.age == newContact.age)
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}

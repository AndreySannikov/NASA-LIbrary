package ru.degus.nasalibrary.util

import androidx.recyclerview.widget.DiffUtil
import ru.degus.nasalibrary.models.Item

class DiffCallback(private val oldList: List<Item>, private val newList: List<Item>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].data == newList[newItemPosition].data
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
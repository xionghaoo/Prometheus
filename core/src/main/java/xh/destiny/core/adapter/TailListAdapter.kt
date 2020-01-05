package com.ks.common.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import xh.destiny.core.R

abstract class TailListAdapter<T>(private var items: ArrayList<T>,
                                  @LayoutRes private val tailLayout: Int = R.layout.common_list_item_tail)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v)

    class TailViewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_CONTENT -> ItemViewHolder(inflater.inflate(itemLayoutId(), parent, false))
            ITEM_TYPE_TAIL -> TailViewHolder(inflater.inflate(tailLayout, parent, false))
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_CONTENT -> {
                val item = items[position]
                val v = holder.itemView
                bindView(v, item, position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            ITEM_TYPE_TAIL
        } else {
            ITEM_TYPE_CONTENT
        }
    }

    fun updateData(_items: ArrayList<T>?) {
        if (_items == null) return
        items = _items
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    abstract fun itemLayoutId() : Int

    abstract fun bindView(v: View, item: T, position: Int)

    companion object {
        const val ITEM_TYPE_CONTENT = 0
        const val ITEM_TYPE_TAIL = 1
    }
}
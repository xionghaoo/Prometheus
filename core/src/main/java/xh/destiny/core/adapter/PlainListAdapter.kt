package com.ks.common.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class PlainListAdapter<T>(private var items: ArrayList<T>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(itemLayoutId(), parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val v = holder.itemView
        bindView(v, item, position)
    }

    fun updateData(_items: ArrayList<T>) {
        items = _items
        notifyDataSetChanged()
    }

    abstract fun itemLayoutId() : Int

    abstract fun bindView(v: View, item: T, position: Int)
}
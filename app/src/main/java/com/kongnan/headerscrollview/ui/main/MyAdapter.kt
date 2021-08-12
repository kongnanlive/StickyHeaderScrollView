package com.kongnan.headerscrollview.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kongnan.headerscrollview.R

class MyAdapter(private val data: MutableList<String>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView = view.findViewById(R.id.textView) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(scanResult: MutableList<String>) {
        data.clear()
        data.addAll(scanResult)
        notifyDataSetChanged()
    }

    companion object {

        fun increase(title: String, size: Int = 100): MutableList<String> {
            val data = mutableListOf<String>()
            (1..size).forEach {
                data.add("$title + $it")
            }
            return data
        }
    }
}
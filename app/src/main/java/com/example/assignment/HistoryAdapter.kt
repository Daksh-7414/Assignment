package com.example.assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(private var historyList: List<HistoryEntity>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val urlText: TextView = itemView.findViewById(R.id.urlLink)
        val timeText: TextView = itemView.findViewById(R.id.urlDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.urlText.text = item.url
        holder.timeText.text = formatTimestamp(item.timestamp)
    }

    override fun getItemCount(): Int = historyList.size

    fun formatTimestamp(time: Long): String {
        val dateFormate = SimpleDateFormat("hh:mm a, dd MMM yy", Locale.ENGLISH)
        return dateFormate.format(Date(time)).lowercase()
    }

    fun updateList(newList: List<HistoryEntity>) {
        historyList = newList
        notifyDataSetChanged()
    }
}

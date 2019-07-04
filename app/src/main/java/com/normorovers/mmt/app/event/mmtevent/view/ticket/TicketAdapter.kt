package com.normorovers.mmt.app.event.mmtevent.view.ticket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.db.Ticket

class TicketAdapter(private val context: Context) : ListAdapter<Ticket, TicketAdapter.TicketHolder>(TicketDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.ticket_item, parent, false)
        return TicketHolder(itemView)
    }

    override fun onBindViewHolder(holder: TicketHolder, position: Int) {
        val currentTicket = getItem(position)
        holder.name?.text = currentTicket.user.name
        holder.mobile?.text = currentTicket.user.mobile
        holder.crew?.text = currentTicket.user.crew
    }

    class TicketHolder(itemView: View) : ViewHolder(itemView) {
        val root: View = itemView
        val name: TextView? = itemView.findViewById(R.id.ticket_name)
        val mobile: TextView? = itemView.findViewById(R.id.ticket_mobile)
        val crew: TextView? = itemView.findViewById(R.id.ticket_crew)
    }
}

private class TicketDiffCallback : DiffUtil.ItemCallback<Ticket>() {
    override fun areItemsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
        return oldItem.equals(newItem)
    }
}
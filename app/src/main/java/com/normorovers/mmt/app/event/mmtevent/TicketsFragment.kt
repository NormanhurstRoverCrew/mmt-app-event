package com.normorovers.mmt.app.event.mmtevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.normorovers.mmt.app.event.mmtevent.db.Ticket
import kotlinx.android.synthetic.main.fragment_teams.*

class TicketsFragment(val teamId: Long?) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tickets, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val rv: RecyclerView = recycler_view

        rv.layoutManager = LinearLayoutManager(this.context)
        rv.hasFixedSize()

        val adapter = TicketAdapter(this.context!!)
        rv.adapter = adapter

        val ticketsViewModel: TicketsViewModel = ViewModelProviders.of(this).get(TicketsViewModel::class.java)

        if (teamId != null ) {
            ticketsViewModel.getFromTeam(teamId)
        } else {
            swipe_container.isRefreshing = false
            ticketsViewModel.getAll()
        }.observe(this, Observer { tickets: List<Ticket> ->
            adapter.submitList(tickets)
        })

        swipe_container.setOnRefreshListener {
            ticketsViewModel.refreshData()
        }
    }
}
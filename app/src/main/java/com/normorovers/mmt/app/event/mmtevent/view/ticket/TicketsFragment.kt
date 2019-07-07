package com.normorovers.mmt.app.event.mmtevent.view.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.db.Ticket
import com.normorovers.mmt.app.event.mmtevent.view.team.TeamViewModel
import kotlinx.android.synthetic.main.fragment_teams.*

//class TicketsFragment(val teamId: Long?) : Fragment() {
class TicketsFragment(val teamId: Long?) : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_tickets, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		val rv: RecyclerView = recycler_view

		rv.layoutManager = LinearLayoutManager(this.context)
		rv.hasFixedSize()

		val ticketsViewModel: TicketsViewModel = ViewModelProviders.of(this).get(TicketsViewModel::class.java)

		if (activity is TicketSelectorActivity) {
			// Activity for selecting a ticket and returning it
			val adapter = TicketSelectAdapter(this.context!!)
			rv.adapter = adapter

			swipe_container.isRefreshing = false
			ticketsViewModel.getAll().observe(this, Observer { tickets: List<Ticket> ->
				adapter.submitList(tickets)
				swipe_container.isRefreshing = false
			})
		} else {
			// Normal ticket fragment
			val adapter = TicketAdapter(this.context!!)
			rv.adapter = adapter


			// if we don't have a valid Id show all the tickets
			if (teamId != null && teamId >= 0) {
				// get a factory with the team id
				val factory: TeamViewModel.Factory = TeamViewModel.Factory(activity!!.application, teamId)

				// get a model from the factory that includes the id
				val teamViewModel: TeamViewModel = ViewModelProviders.of(this, factory).get(TeamViewModel::class.java)

				teamViewModel.getTickets()
			} else {
				swipe_container.isRefreshing = false
				ticketsViewModel.getAll()
			}.observe(this, Observer { tickets: List<Ticket> ->
				adapter.submitList(tickets)
				swipe_container.isRefreshing = false
			})
		}

		swipe_container.setOnRefreshListener {
			ticketsViewModel.refreshData()
		}
	}
}
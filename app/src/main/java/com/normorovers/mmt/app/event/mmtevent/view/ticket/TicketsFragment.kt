package com.normorovers.mmt.app.event.mmtevent.view.ticket

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.db.Ticket
import com.normorovers.mmt.app.event.mmtevent.view.team.TeamViewModel
import kotlinx.android.synthetic.main.fragment_teams.*
import org.jetbrains.anko.doAsync
import java.util.concurrent.Future

//class TicketsFragment(val teamId: Long?) : Fragment() {
class TicketsFragment(val teamId: Long?) : Fragment() {

	private lateinit var fullListTickets: List<Ticket>
	private lateinit var adapter: ListAdapter<Ticket, out RecyclerView.ViewHolder>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

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
			adapter = TicketSelectAdapter(this.context!!)
			rv.adapter = adapter

			swipe_container.isRefreshing = false
			ticketsViewModel.getAll().observe(this, Observer { tickets: List<Ticket> ->
				adapter.submitList(tickets)
				swipe_container.isRefreshing = false
			})
		} else {
			// Normal ticket fragment
			adapter = TicketAdapter(this.context!!)
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
				fullListTickets = tickets
				adapter.submitList(tickets)
				swipe_container.isRefreshing = false
			})
		}

		swipe_container.setOnRefreshListener {
			ticketsViewModel.refreshData()
		}
	}

	private fun search(query: String?): Future<Unit> = doAsync {
		val wanted = if (!query.isNullOrEmpty()) {
			val rQuery = query.toLowerCase()
			fullListTickets.filter {
				it.user.name.toLowerCase().contains(rQuery) or
						(!it.user.crew.isNullOrBlank() && it.user.crew.toLowerCase().contains(rQuery)) or
						(!it.user.mobile.isNullOrBlank() && it.user.mobile.replace("\\s".toRegex(), "").contains(rQuery))
			}.toList()
		} else {
			fullListTickets
		}
		adapter.submitList(wanted)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.search_menu, menu)

		val searchItem: MenuItem = menu.findItem(R.id.action_search)
		val searchView: SearchView = searchItem.actionView as SearchView

		searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(query: String?): Boolean {
				search(query)
				return false
			}

			override fun onQueryTextChange(query: String?): Boolean {
				search(query)
				return false
			}
		})

		super.onCreateOptionsMenu(menu, inflater)
	}
}
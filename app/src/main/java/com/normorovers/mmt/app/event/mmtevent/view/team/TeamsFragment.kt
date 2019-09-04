package com.normorovers.mmt.app.event.mmtevent.view.team

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
import com.normorovers.mmt.app.event.mmtevent.db.Team
import kotlinx.android.synthetic.main.fragment_teams.*
import org.jetbrains.anko.doAsync
import java.util.concurrent.Future

private const val ARG_TYPE = "type"

class TeamsFragment : Fragment() {
    private var type: Type? = null

	private lateinit var fullListTeams: List<Team>
	private lateinit var adapter: ListAdapter<Team, out RecyclerView.ViewHolder>

    enum class Type(val value: Int) {
        TeamEdit(1),
        TeamActivityLog(2);

        companion object {
			private val map = values().associateBy(Type::value)
            fun fromInt(type: Int) = map[type]
        }
    }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            type = Type.fromInt(it.getInt(ARG_TYPE))
        }

        val rv: RecyclerView = recycler_view

        rv.layoutManager = LinearLayoutManager(this.context)
        rv.hasFixedSize()

		adapter = when (type!!) {
            Type.TeamEdit -> TeamsListEditAdapter(this.context!!)
            Type.TeamActivityLog -> TeamsListActivityLogAdapter(this.context!!)
        }

        rv.adapter = adapter

        val teamsViewModel: TeamsViewModel = ViewModelProviders.of(this).get(TeamsViewModel::class.java)
        teamsViewModel.getAll().observe(this, Observer { teams: List<Team> ->
			fullListTeams = teams
			adapter.submitList(teams)
			swipe_container.isRefreshing = false
        })

        swipe_container.setOnRefreshListener {
            teamsViewModel.refreshData()
        }
    }

	private fun search(query: String?): Future<Unit> = doAsync {
		val wanted = if (!query.isNullOrEmpty()) {
			val validQuery = query.toLowerCase()
			fullListTeams.filter {
				it.name.toLowerCase().contains(validQuery) or
						it.registration.toLowerCase().contains(validQuery) or
						it.id.toString().contains(validQuery)
			}.toList()
		} else {
			fullListTeams
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param type The type of list this is. Edit or Base Log
         * @return A new instance of fragment TeamFragment.
         */
        @JvmStatic
        fun newInstance(type: Type) =
                TeamsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_TYPE, type.value)
                    }
                }
    }
}
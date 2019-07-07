package com.normorovers.mmt.app.event.mmtevent.view.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.db.Team
import kotlinx.android.synthetic.main.fragment_teams.*

private const val ARG_TYPE = "type"

class TeamsFragment : Fragment() {

    private var type: Type? = null

    enum class Type(val value: Int) {
        TeamEdit(1),
        TeamActivityLog(2);

        companion object {
            private val map = Type.values().associateBy(Type::value)
            fun fromInt(type: Int) = map[type]
        }
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

        val adapter: ListAdapter<Team, out RecyclerView.ViewHolder> = when (type!!) {
            Type.TeamEdit -> TeamsListEditAdapter(this.context!!)
            Type.TeamActivityLog -> TeamsListActivityLogAdapter(this.context!!)
        }

        rv.adapter = adapter

        val teamsViewModel: TeamsViewModel = ViewModelProviders.of(this).get(TeamsViewModel::class.java)
        teamsViewModel.getAll().observe(this, Observer { teams: List<Team> ->
            adapter.submitList(teams)
            swipe_container.isRefreshing = false
        })

        swipe_container.setOnRefreshListener {
            teamsViewModel.refreshData()
        }
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
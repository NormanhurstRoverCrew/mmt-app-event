package com.normorovers.mmt.app.event.mmtevent

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.normorovers.mmt.app.event.mmtevent.db.Team
import kotlinx.android.synthetic.main.fragment_teams.*

class TeamsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teams, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipe_container.isRefreshing = true

        val rv: RecyclerView = recycler_view

        rv.layoutManager = LinearLayoutManager(this.context)
        rv.hasFixedSize()

        val adapter = TeamAdapter(this.context!!)
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
}
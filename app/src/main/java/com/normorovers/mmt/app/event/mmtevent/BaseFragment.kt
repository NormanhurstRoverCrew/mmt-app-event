package com.normorovers.mmt.app.event.mmtevent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.normorovers.mmt.app.event.mmtevent.view.team.EditTeamActivity
import com.normorovers.mmt.app.event.mmtevent.view.team.TeamsFragment
import com.normorovers.mmt.app.event.mmtevent.view.ticket.TicketsFragment
import kotlinx.android.synthetic.main.fragment_check_in.*

class BaseFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_base, container, false)

	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		setButtonActions()
	}

	fun setButtonActions() {
		// Check in teams button opens fragment for checking in teams.
		(list_tickets as Button).setOnClickListener {
			activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container,
					TicketsFragment(null))?.addToBackStack(null)?.commit()
		}

		// List tickets button opens fragment for showing the list of teams.
		(list_teams as Button).setOnClickListener {
			activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container,
					TeamsFragment())?.addToBackStack(null)?.commit()
		}

		// List tickets button opens fragment for showing the list of teams.
		(scan_team as Button).setOnClickListener {
			activity?.startActivity(Intent(context, EditTeamActivity::class.java))
		}
	}
}
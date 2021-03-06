package com.normorovers.mmt.app.event.mmtevent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.normorovers.mmt.app.event.mmtevent.view.base.ActivityLogActivity
import com.normorovers.mmt.app.event.mmtevent.view.base.BaseTeamLogActivity
import com.normorovers.mmt.app.event.mmtevent.view.team.TeamsFragment
import kotlinx.android.synthetic.main.fragment_base.*
import kotlinx.android.synthetic.main.fragment_check_in.list_teams
import kotlinx.android.synthetic.main.fragment_check_in.scan_team

class BaseFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_base, container, false)

	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		setButtonActions()
	}

	fun setButtonActions() {
		// List tickets button opens fragment for showing the list of teams.
		(list_teams as Button).setOnClickListener {
			activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container,
					TeamsFragment.newInstance(TeamsFragment.Type.TeamActivityLog))?.addToBackStack(null)?.commit()
		}

		// List tickets button opens fragment for showing the list of teams.
		(scan_team as Button).setOnClickListener {
			activity?.startActivity(Intent(context, BaseTeamLogActivity::class.java))
		}

		// List tickets button opens fragment for showing the list of teams.
		(logs as Button).setOnClickListener {
			val i = Intent(activity, ActivityLogActivity::class.java)
			i.putExtra("base", 1)
			startActivity(i)
		}
	}
}
package com.normorovers.mmt.app.event.mmtevent.view.team

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.db.Team
import com.normorovers.mmt.app.event.mmtevent.view.ticket.TicketsFragment
import kotlinx.android.synthetic.main.activity_edit_team.*
import kotlinx.android.synthetic.main.content_edit_team.*

class EditTeamActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_edit_team)
		setSupportActionBar(toolbar)

		val teamId: Long? = intent.getLongExtra("id", -1)

		//TODO show error message here if teamUid == -1
		val factory: TeamViewModel.Factory = TeamViewModel.Factory(application, teamId!!)

		val teamViewModel: TeamViewModel = ViewModelProviders.of(this, factory).get(TeamViewModel::class.java)
		teamViewModel.get()
				.observe(this, Observer { team: Team ->
					(team_name as TextView).text = team.name
					(team_registration as TextView).text = team.registration
				})



		Log.d("EditTeam", intent.getLongExtra("id", -1).toString())

		supportFragmentManager.beginTransaction().replace(
				R.id.fragment,
				TicketsFragment(intent.getLongExtra("id", -1))
		).commit()
	}

}

package com.normorovers.mmt.app.event.mmtevent.view.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.view.team.TeamFragment
import kotlinx.android.synthetic.main.activity_base_team_log.*

private const val ARG_NAME = "name"
private const val ARG_REGO = "rego"
private const val ARG_UID = "uid"

class BaseTeamLogActivity : AppCompatActivity() {
	private var name: String? = null
	private var rego: String? = null
	private var uid: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_base_team_log)
		setSupportActionBar(toolbar)

		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		intent?.let {
			name = it.getStringExtra(ARG_NAME)
			rego = it.getStringExtra(ARG_REGO)
			uid = it.getStringExtra(ARG_UID)
		}

		supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_team,
				TeamFragment.newInstance(uid!!, name!!, rego!!))?.commit()
	}

}

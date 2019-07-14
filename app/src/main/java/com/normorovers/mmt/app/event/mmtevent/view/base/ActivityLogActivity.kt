package com.normorovers.mmt.app.event.mmtevent.view.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.view.base.ui.activitylog.ActivityLogFragment

class ActivityLogActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_log_activity)

		val teamId: Long = intent.getLongExtra("team", -1L)
		val baseId: Int = intent.getIntExtra("base", -1)

		if (savedInstanceState == null) {
			if (teamId >= 0L && baseId >= 0) {
				supportFragmentManager.beginTransaction()
						.replace(R.id.container, ActivityLogFragment.newInstance(teamId, baseId))
						.commitNow()
			} else if (baseId >= 0) {
				supportFragmentManager.beginTransaction()
						.replace(R.id.container, ActivityLogFragment.newInstance(baseId))
						.commitNow()
			}
		}
	}

}

package com.normorovers.mmt.app.event.mmtevent.view.team

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.normorovers.mmt.app.event.mmtevent.R
import kotlinx.android.synthetic.main.activity_team.*

class TeamActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_team)
		setSupportActionBar(toolbar)

		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

}

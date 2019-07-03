package com.normorovers.mmt.app.event.mmtevent

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_edit_team.*
import kotlinx.android.synthetic.main.content_edit_team.*

class EditTeamActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_team)
        setSupportActionBar(toolbar)

        (team_name as TextView).text = intent.getStringExtra("name")
        (team_registration as TextView).text = intent.getStringExtra("registration")

        Log.d("EditTeam", intent.getLongExtra("id", -1).toString())

        supportFragmentManager.beginTransaction().replace(
                R.id.fragment,
                TicketsFragment(intent.getLongExtra("id", -1))
        ).commit()
    }

}

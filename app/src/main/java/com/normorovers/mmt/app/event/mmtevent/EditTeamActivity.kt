package com.normorovers.mmt.app.event.mmtevent

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
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
    }

}

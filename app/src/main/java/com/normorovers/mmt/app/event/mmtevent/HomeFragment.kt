package com.normorovers.mmt.app.event.mmtevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.actionBar?.title = "All Teams"
        setButtonActions()
    }

    fun setButtonActions() {
        // Check in teams button opens fragment for checking in teams.
        (checkin_teams as Button).setOnClickListener {
        }

        // List tickets button opens fragment for showing the list of teams.
        (list_teams as Button).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container,
                    TeamsFragment())?.addToBackStack(null)?.commit()
        }
    }
}
package com.normorovers.mmt.app.event.mmtevent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setButtonActions(view, activity)


    }

    fun setButtonActions(view: View?, activity: FragmentActivity?) {
        // Check in teams button opens fragment for checking in teams.
        (view?.findViewById(R.id.checkin_teams) as Button).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container,
                    CheckinFragment())?.addToBackStack(null)?.commit()
        }

        // List tickets button opens fragment for showing the list of teams.
        (view.findViewById(R.id.list_tickets) as Button).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container,
                    TicketsFragment())?.addToBackStack(null)?.commit()
        }
    }
}
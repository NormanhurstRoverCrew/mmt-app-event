package com.normorovers.mmt.app.event.mmtevent.view.base.ui.activitylog

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.db.ActivityLog
import kotlinx.android.synthetic.main.fragment_teams.*

class ActivityLogFragment(val teamId: Long?, val baseId: Int) : Fragment() {

	companion object {
		fun newInstance(teamId: Long, baseId: Int) = ActivityLogFragment(teamId, baseId)
		fun newInstance(baseId: Int) = ActivityLogFragment(baseId = baseId, teamId = null)
	}

	private lateinit var viewModel: ActivityLogViewModel

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View {
		return inflater.inflate(R.layout.activity_log_fragment, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		viewModel = ActivityLogViewModel(context!!.applicationContext as Application)

		val logs = if (teamId != null) {
			viewModel.getLogs(teamId, baseId)
		} else {
			viewModel.getLogs(baseId)
		}

		val rv: RecyclerView = recycler_view

		rv.layoutManager = LinearLayoutManager(this.context)
		rv.hasFixedSize()

		val adapter: ListAdapter<ActivityLog, out RecyclerView.ViewHolder> = ActivityLogAdapter(context!!)

		rv.adapter = adapter

		adapter.submitList(logs.get())
	}

}

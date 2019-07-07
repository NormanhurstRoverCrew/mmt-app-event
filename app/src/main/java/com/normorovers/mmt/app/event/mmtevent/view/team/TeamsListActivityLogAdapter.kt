package com.normorovers.mmt.app.event.mmtevent.view.team

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.db.Team
import com.normorovers.mmt.app.event.mmtevent.view.base.BaseTeamLogActivity

class TeamsListActivityLogAdapter(private val context: Context) : ListAdapter<Team, TeamsListActivityLogAdapter.TeamHolder>(TeamDiffCallback()) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamHolder {
		val itemView: View = LayoutInflater.from(parent.context)
				.inflate(R.layout.team_item, parent, false)
		return TeamHolder(itemView)
	}

	override fun onBindViewHolder(holder: TeamHolder, position: Int) {
		val currentTeam = getItem(position)
		holder.textViewTitle?.text = if (currentTeam.name.length > 0) {
			currentTeam.name
		} else {
			"[Name: EMPTY]"
		}

		holder.textViewRego?.text = if (currentTeam.registration.length > 0) {
			currentTeam.registration
		} else {
			"[Rego: EMPTY]"
		}

		holder.textViewDescription?.text = "Put Checkin status for base"

		holder.root.setOnClickListener {
			val i = Intent(context, BaseTeamLogActivity::class.java)
			i.putExtra("uid", currentTeam.uid)
			i.putExtra("name", currentTeam.name)
			i.putExtra("rego", currentTeam.registration)
			context.startActivity(i)
		}
	}

	class TeamHolder(itemView: View) : ViewHolder(itemView) {
		val root: View = itemView
		val textViewTitle: TextView? = itemView.findViewById(R.id.title)
		val textViewRego: TextView? = itemView.findViewById(R.id.registration)
		val textViewDescription: TextView? = itemView.findViewById(R.id.description)
	}

	private class TeamDiffCallback : DiffUtil.ItemCallback<Team>() {
		override fun areItemsTheSame(oldItem: Team, newItem: Team): Boolean {
			return oldItem.uid == newItem.uid
		}

		override fun areContentsTheSame(oldItem: Team, newItem: Team): Boolean {
			return oldItem.equals(newItem)
		}
	}
}
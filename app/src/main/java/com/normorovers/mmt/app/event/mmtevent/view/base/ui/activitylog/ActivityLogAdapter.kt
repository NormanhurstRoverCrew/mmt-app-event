package com.normorovers.mmt.app.event.mmtevent.view.base.ui.activitylog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.normorovers.mmt.app.event.mmtevent.R
import com.normorovers.mmt.app.event.mmtevent.db.ActivityLog
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

class ActivityLogAdapter(private val context: Context) : ListAdapter<ActivityLog, ActivityLogAdapter.ActivityLogHolder>(TeamDiffCallback()) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityLogHolder {
		val itemView: View = LayoutInflater.from(parent.context)
				.inflate(R.layout.activity_log_item, parent, false)
		return ActivityLogHolder(itemView)
	}

	override fun onBindViewHolder(holder: ActivityLogHolder, position: Int) {
		val log = getItem(position)

		holder.message.text = message(log) ?: "UNKNOWN"
		holder.time.text = log.loggedAt.toLocalTime().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_TIME)
		holder.id.text = log.team.toString()
	}

	private fun message(log: ActivityLog): String? {
		log.arrived?.let { return "Arrived" }
		log.departed?.let { return "Departed" }
		log.comment?.let { return "Comment: $it" }
		log.points?.let { return "Points: $it" }
		log.trivia?.let { return "Trivia: $it" }
		log.clues?.let { return if (it) "Clues: Set to Opened" else "Clues: Set to UNOpened" }

		return null
	}

	class ActivityLogHolder(itemView: View) : ViewHolder(itemView) {
		val root: View = itemView
		val message: TextView = itemView.findViewById(R.id.main_message)
		val time: TextView = itemView.findViewById(R.id.time)
		val id: TextView = itemView.findViewById(R.id.team_id)
	}

	private class TeamDiffCallback : DiffUtil.ItemCallback<ActivityLog>() {
		override fun areItemsTheSame(oldItem: ActivityLog, newItem: ActivityLog): Boolean {
			return oldItem.loggedAt == newItem.loggedAt
		}

		override fun areContentsTheSame(oldItem: ActivityLog, newItem: ActivityLog): Boolean {
			return oldItem.equals(newItem)
		}
	}
}


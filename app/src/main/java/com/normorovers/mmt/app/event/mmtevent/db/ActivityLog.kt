package com.normorovers.mmt.app.event.mmtevent.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "activity_logs")
class ActivityLog(
		@PrimaryKey val id: Long?,
		@ColumnInfo(name = "logged_at") @SerializedName("logged_at") val loggedAt: OffsetDateTime = OffsetDateTime.now(),
		@SerializedName("team_id") val team: Long,
		val base: Int,
		val admin: String,
		var arrived: Boolean?,
		var departed: Boolean?,
		var points: Float?,
		var trivia: Float?,
		var clues: Boolean?,
		var comment: String?
) {
	companion object {
		fun new(base: Int, team: Long): ActivityLog {
			return ActivityLog(
					base = base,
					team = team,
					admin = "auth0|TODO",
					id = null,
					arrived = null,
					departed = null,
					points = null,
					trivia = null,
					clues = null,
					comment = null
			)
		}
	}
}
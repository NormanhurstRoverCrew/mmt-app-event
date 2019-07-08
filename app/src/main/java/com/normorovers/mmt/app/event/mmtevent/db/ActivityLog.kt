package com.normorovers.mmt.app.event.mmtevent.db

import android.app.Application
import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.normorovers.mmt.app.event.mmtevent.R
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
		var comment: String?,

		var synced: Boolean = false
) {
	companion object {
		fun new(base: Int, team: Long, admin: String): ActivityLog {
			return ActivityLog(
					base = base,
					team = team,
					admin = admin,
					id = null,
					arrived = null,
					departed = null,
					points = null,
					trivia = null,
					clues = null,
					comment = null
			)
		}

		fun new(application: Application, team: Long): ActivityLog {
			val preferences = application.getSharedPreferences(application.getString(R.string.shared_preferences), Context.MODE_PRIVATE)
			val base = preferences.getInt("base_id", -1)
			val admin = preferences.getString("auth_id", null)

			if (base == -1) throw Error("You must set your base ID before continuing.")
			if (admin.isNullOrEmpty()) throw Error("Not Logged in?")

			return ActivityLog(
					base = base,
					team = team,
					admin = admin,
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
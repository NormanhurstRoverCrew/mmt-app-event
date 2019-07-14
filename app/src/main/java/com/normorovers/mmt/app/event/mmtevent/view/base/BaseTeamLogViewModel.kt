package com.normorovers.mmt.app.event.mmtevent.view.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.normorovers.mmt.app.event.mmtevent.db.ActivityLog
import com.normorovers.mmt.app.event.mmtevent.db.ActivityLogRepository
import com.normorovers.mmt.app.event.mmtevent.db.TeamRepository

class BaseTeamLogViewModel(private val app: Application, private val teamId: Long, private val baseId: Int) : AndroidViewModel(app) {
	private val activityR = ActivityLogRepository(app)
	private val teamR = TeamRepository(app)

	private fun newLog(): ActivityLog {
		return ActivityLog.new(app, teamId)
	}

	fun insert(activityLog: ActivityLog) {
		activityR.insert(activityLog)
	}

	fun insertArrived() {
		val log = newLog()
		log.arrived = true
		insert(log)
	}

	fun insertDepart() {
		val log = newLog()
		log.departed = true
		insert(log)
	}

	fun insertPoints(points: Float) {
		val log = newLog()
		log.points = points
		insert(log)
	}

	fun insertTrivia(trivia: Float) {
		val log = newLog()
		log.trivia = trivia
		insert(log)
	}

	fun insertClues(opened: Boolean?) {
		val log = newLog()
		log.clues = opened ?: true
		insert(log)
	}

	fun insertComment(comment: String) {
		val log = newLog()
		log.comment = comment
		insert(log)
	}

	fun getAll(): LiveData<List<ActivityLog>> {
		return activityR.getAll()
	}

	fun totalPoints(): Float {
		return activityR.totalPoints(teamId, baseId)
	}

	fun comments(): List<String> {
		return activityR.comments(teamId, baseId)
	}

	fun isAtBase(): Boolean {
		return activityR.isAtBase(teamId, baseId)
	}
}
package com.normorovers.mmt.app.event.mmtevent.view.base.ui.activitylog

import android.app.Application
import androidx.lifecycle.ViewModel
import com.normorovers.mmt.app.event.mmtevent.db.ActivityLog
import com.normorovers.mmt.app.event.mmtevent.db.ActivityLogRepository
import java.util.concurrent.Future

class ActivityLogViewModel(application: Application) : ViewModel() {
	private val repository = ActivityLogRepository(application)

	fun getLogs(teamId: Long, baseId: Int): Future<List<ActivityLog>> {
		return repository.getTeam(teamId, baseId)
	}

	fun getLogs(baseId: Int): Future<List<ActivityLog>> {
		return repository.get(baseId)
	}
}

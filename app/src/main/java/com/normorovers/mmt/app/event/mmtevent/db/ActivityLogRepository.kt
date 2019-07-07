package com.normorovers.mmt.app.event.mmtevent.db

import android.app.Application
import androidx.lifecycle.LiveData
import org.jetbrains.anko.doAsync

class ActivityLogRepository(private val application: Application) {
	val database = AppDatabase.getInstance(application)
	val activityLogDao = database.activityLogDao()
	val allActivityLogs = activityLogDao.getAll()

	fun insert(activityLog: ActivityLog) {
		doAsync {
			activityLogDao.insert(activityLog)
		}
	}

	fun update(activityLog: ActivityLog) {
		doAsync {
			activityLogDao.update(activityLog)
		}
	}

	fun delete(activityLog: ActivityLog) {
		doAsync {
			activityLogDao.delete(activityLog)
		}
	}

	fun getAll(): LiveData<List<ActivityLog>> {
		return allActivityLogs
	}
}
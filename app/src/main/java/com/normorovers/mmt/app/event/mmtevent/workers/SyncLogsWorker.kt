package com.normorovers.mmt.app.event.mmtevent.workers

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.normorovers.mmt.app.event.mmtevent.api.ApiUnauthorized
import com.normorovers.mmt.app.event.mmtevent.db.ActivityLogRepository

class SyncLogsWorker(private val appContext: Context, workerParams: WorkerParameters)
	: Worker(appContext, workerParams) {
	override fun doWork(): Result {
		try {
			val activityLogRepo = ActivityLogRepository(appContext as Application)
			if (!activityLogRepo.sync().get()) return Result.retry()
		} catch (e: ApiUnauthorized) {
			Log.d("ApiUnauthorized", "Caught")
			return Result.retry()
		}
		return Result.success()
	}
}
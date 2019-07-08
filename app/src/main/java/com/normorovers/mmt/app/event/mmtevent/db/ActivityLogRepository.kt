package com.normorovers.mmt.app.event.mmtevent.db

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.*
import com.normorovers.mmt.app.event.mmtevent.api.ActivityLogs
import com.normorovers.mmt.app.event.mmtevent.api.Api
import com.normorovers.mmt.app.event.mmtevent.api.ApiUnauthorized
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import retrofit2.Call
import java.util.concurrent.Future

class ActivityLogRepository(private val application: Application) {
	val database = AppDatabase.getInstance(application)
	val activityLogDao = database.activityLogDao()
	val allActivityLogs = activityLogDao.getAll()

	fun insert(activityLog: ActivityLog) {
		doAsync {
			activityLogDao.insert(activityLog)
			startSync()
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

	fun startSync() {
		val syncWorker = OneTimeWorkRequestBuilder<ActivityLogRepository.SyncLogsWorker>()
				.setConstraints(
						Constraints.Builder()
								.setRequiredNetworkType(NetworkType.CONNECTED)
								.build())
				.build()

		WorkManager.getInstance().enqueueUniqueWork("com.normorovers.mmt.app.event.log_syncing", ExistingWorkPolicy.REPLACE, syncWorker)
	}


	private fun sync(): Future<Boolean> {
		val retrofit = Api(application).retrofit()
		val activityLogApi = retrofit.create(ActivityLogs::class.java)

		return doAsyncResult {
			val unsyncedLogs = activityLogDao.getUnsnced()
			val call: Call<ResponseBody> = activityLogApi.sendLogs(unsyncedLogs)

			val response = call.execute()

			if (!response.isSuccessful) {
				return@doAsyncResult false
			}


			// mark all the logs just sent to the server as "sync'd"
			for (log in unsyncedLogs) {
				log.synced = true
				activityLogDao.insert(log)
			}

			return@doAsyncResult true
		}
	}

	private class SyncLogsWorker(private val appContext: Context, workerParams: WorkerParameters)
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
}
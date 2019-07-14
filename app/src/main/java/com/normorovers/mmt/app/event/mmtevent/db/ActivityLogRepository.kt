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

	fun get(baseId: Int): Future<List<ActivityLog>> {
		return doAsyncResult {
			return@doAsyncResult activityLogDao.get(baseId)
		}
	}

	fun getTeam(teamId: Long, baseId: Int): Future<List<ActivityLog>> {
		return doAsyncResult {
			return@doAsyncResult activityLogDao.getTeam(teamId, baseId)
		}
	}

	fun startSync() {
		val syncWorker = OneTimeWorkRequestBuilder<SyncLogsWorker>()
				.setConstraints(
						Constraints.Builder()
								.setRequiredNetworkType(NetworkType.CONNECTED)
								.build())
				.build()

		WorkManager.getInstance(application).enqueueUniqueWork("com.normorovers.mmt.app.event.log_syncing", ExistingWorkPolicy.REPLACE, syncWorker)
	}

	fun totalPoints(teamId: Long, baseId: Int): Float {
		val points = doAsyncResult {
			var a = 0F
			for (log in activityLogDao.getTeamAllPoints(teamId, baseId)) {
				log.points?.let { a += it }
				log.trivia?.let { a += it }
			}
			return@doAsyncResult a
		}

		val clues = doAsyncResult {
			var b = 0F
			for (log in activityLogDao.getTeamClues(teamId, baseId)) {
				log.clues?.let { b += if (it) (-10F) else (10F) }
			}
			return@doAsyncResult b
		}


		return points.get() + clues.get()
	}

	fun comments(teamId: Long, baseId: Int): List<String> {
		val res = doAsyncResult {
			val comments: ArrayList<String> = ArrayList()
			for (log in activityLogDao.getTeamComments(teamId, baseId)) {
				log.comment?.let { comments.add(it) }
			}
			return@doAsyncResult comments
		}

		return res.get()
	}

	fun isAtBase(teamId: Long, baseId: Int): Boolean {
		return doAsyncResult {
			return@doAsyncResult isAtBase(activityLogDao.getTeamArrivedOrDeparted(teamId, baseId))
		}.get()
	}

	private fun isAtBase(logs: List<ActivityLog>): Boolean {
		var atBase = false
		for (log in logs) {
			log.arrived?.let { if (it) atBase = true }
			log.departed?.let { if (it) atBase = false }
		}
		return atBase
	}

	private fun hasVisitedBase(logs: List<ActivityLog>): Boolean {
		var visited = false
		for (log in logs) {
			log.arrived?.let { if (it) visited = true }
		}
		return visited
	}


	// returns the observer so the context can destroy the observer when its finished
	fun isAtBaseLive(teamId: Long, baseId: Int, update: (atBase: Boolean) -> Unit): (logs: List<ActivityLog>) -> Unit {
		val observer: (logs: List<ActivityLog>) -> Unit = {
			if (hasVisitedBase(it)) update(isAtBase(it))
		}
		activityLogDao.getTeamArrivedOrDepartedLive(teamId, baseId).observeForever(observer)
		return observer
	}

	private fun sync(): Future<Boolean> {
		val retrofit = Api(application).retrofit()
		val activityLogApi = retrofit.create(ActivityLogs::class.java)

		return doAsyncResult {
			val unsyncedLogs = activityLogDao.getUnsynced()
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
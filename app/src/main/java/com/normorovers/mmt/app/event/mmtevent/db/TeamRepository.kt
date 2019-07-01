package com.normorovers.mmt.app.event.mmtevent.db

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.*
import com.normorovers.mmt.app.event.mmtevent.api.Api
import com.normorovers.mmt.app.event.mmtevent.api.ApiUnauthorized
import com.normorovers.mmt.app.event.mmtevent.api.Teams
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Response

class TeamRepository(val application: Application) {
	val database = AppDatabase.getInstance(application)
	val teamDao = database.teamDao()
	val allTeams = teamDao.getAll()

	fun insert(team: Team) {
		doAsync {
			teamDao.insert(team)
		}
	}

	fun update(team: Team) {
		doAsync {
			teamDao.update(team)
		}
	}

	fun delete(team: Team) {
		doAsync {
			teamDao.delete(team)
		}
	}

	fun getAll(): LiveData<List<Team>> {
		return allTeams
	}

	fun deleteAll() {
		doAsync {
			teamDao.deleteAll()
		}
	}

	fun refreshData() {
		val pullWorker = OneTimeWorkRequestBuilder<PullWorker>()
				.setConstraints(
						Constraints.Builder()
								.setRequiredNetworkType(NetworkType.CONNECTED)
								.build())
				.addTag("test")
				.build()

		WorkManager.getInstance().enqueue(pullWorker)
	}

	private fun apiPull() {
		apiPull({})
	}

	private fun apiPull(unauthorized: () -> Unit) {
		Api(application).retrofit {
			val teamsD: Teams = it.create(Teams::class.java)
			val call: Call<List<Team>> = teamsD.getTeams()
			doAsync {
				val response: Response<List<Team>> = call.execute()
				if (!response.isSuccessful) {
					when (response.code()) {
						401 -> {
							unauthorized()
						}
					}
				}

				val teams: List<Team> = response.body()!!

				val original = teamDao.getOnlyAll()

				val remove: List<Team>? = original.filter { old ->
					val overlap: Team? = teams.find { new ->
						new.uid == old.uid
					}
					overlap == null
				}

				if (remove != null) {
					for (team in remove.iterator()) {
						delete(team)
					}
				}

				for (team in teams) {
					insert(team)
				}
			}

		}
	}

	class PullWorker(appContext: Context, workerParams: WorkerParameters)
		: Worker(appContext, workerParams) {
		override fun doWork(): Result {
			try {
				TeamRepository(applicationContext as Application).apiPull()
			} catch (e: ApiUnauthorized) {
				Log.d("ApiUnauthorized", "Caught")
			}
			return Result.success()
		}

	}

}
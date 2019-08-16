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

			val retrofit = Api(application).retrofit()


			val teamsD: Teams = retrofit.create(Teams::class.java)
			val call: Call<Team> = teamsD.update(team.id, team)
			call.execute()
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

	fun get(id: Long): LiveData<Team> {
		return teamDao.get(id)
//		val teams: List<Team>? = allTeams.value
//		val team: List<Team> = teams!!.filter { it.uid == uid }
//		if (team.any()) {
//			return team.get(0)
//		} else {
//			throw NoTeamException
//		}
	}

	fun getOnly(id: Long): Team {
		return teamDao.getOnly(id)
//		val teams: List<Team>? = allTeams.value
//		val team: List<Team> = teams!!.filter { it.uid == uid }
//		if (team.any()) {
//			return team.get(0)
//		} else {
//			throw NoTeamException
//		}
	}

	fun deleteAll() {
		doAsync {
			teamDao.deleteAll()
		}
	}

	fun getByUid(team: String): Team {
		return teamDao.getById(team)
	}

	fun refreshData() {
		val pullWorker = OneTimeWorkRequestBuilder<PullWorker>()
				.setConstraints(
						Constraints.Builder()
								.setRequiredNetworkType(NetworkType.CONNECTED)
								.build())
				.addTag("test")
				.build()

		WorkManager.getInstance(application).enqueue(pullWorker)
	}

	fun refreshTeamData(teamId: Long) {
		val pullWorker = OneTimeWorkRequestBuilder<PullTeamWorker>()
				.setInputData(
						Data.Builder()
								.putLong("team_id", teamId)
								.build()
				)
				.setConstraints(
						Constraints.Builder()
								.setRequiredNetworkType(NetworkType.CONNECTED)
								.build())
				.addTag("test")
				.build()

		WorkManager.getInstance(application).enqueue(pullWorker)
	}

	private fun apiPull() {
		apiPull({})
	}

	private fun apiPull(unauthorized: () -> Unit) {
		val retrofit = Api(application).retrofit()

		val teamsD: Teams = retrofit.create(Teams::class.java)
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

				for (ticket in team.tickets!!) {
					TicketRepository(application).insert(ticket)
				}

			}

		}
	}

	private fun apiPullTeam(teamId: Long) {
		apiPullTeam(teamId, {})
	}


	private fun apiPullTeam(teamId: Long, unauthorized: () -> Unit) {
		val retrofit = Api(application).retrofit()
		val teamsD: Teams = retrofit.create(Teams::class.java)
		val call: Call<Team> = teamsD.getTeam(teamId, true)
		doAsync {
			val response: Response<Team> = call.execute()
			if (!response.isSuccessful) {
				when (response.code()) {
					401 -> {
						unauthorized()
					}
				}
			}

			if (response.body() == null) {
				return@doAsync
			}

			val team: Team = response.body()!!

			insert(team)

			val tickets = team.tickets!!

			val original = AppDatabase.getInstance(application).ticketDao().getOnlyFromTeam(teamId)

			val remove: List<Ticket>? = original.filter { old ->
				val overlap: Ticket? = tickets.find { new ->
					new.uid == old.uid
				}
				overlap == null
			}

			val ticketRepo = TicketRepository(application)

			if (remove != null) {
				for (ticket in remove.iterator()) {
					ticket.teamId = -1

					// instead of deleting the ticket, instead make it teamless. Then update its team
					ticket.teamId = -1
					ticketRepo.update(ticket)
					ticketRepo.refreshTicket(ticket.id)
				}
			}

			for (ticket in tickets) {
				ticketRepo.insert(ticket)
			}
		}


	}

	class PullTeamWorker(appContext: Context, workerParams: WorkerParameters)
		: Worker(appContext, workerParams) {
		override fun doWork(): Result {
			try {
				val teamRepo = TeamRepository(applicationContext as Application)
				val teamId: Long? = inputData.getLong("team_id", -1)
				if (teamId != null) {
					teamRepo.apiPullTeam(teamId)
				} else {
					return Result.failure()
				}
			} catch (e: ApiUnauthorized) {
				Log.d("ApiUnauthorized", "Caught")
			} catch (e: NoTeamException) {
				Log.d("NoTeamException", "Caught")
			}
			return Result.success()
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
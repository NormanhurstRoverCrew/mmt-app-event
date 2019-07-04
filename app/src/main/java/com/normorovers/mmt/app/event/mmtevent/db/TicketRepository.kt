package com.normorovers.mmt.app.event.mmtevent.db

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.*
import com.normorovers.mmt.app.event.mmtevent.api.Api
import com.normorovers.mmt.app.event.mmtevent.api.ApiUnauthorized
import com.normorovers.mmt.app.event.mmtevent.api.Tickets
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Response

class TicketRepository(private val application: Application) {
	val database = AppDatabase.getInstance(application)
	val ticketDao = database.ticketDao()
	val allTickets = ticketDao.getAll()

	fun insert(ticket: Ticket) {
		doAsync {
			ticketDao.insert(ticket)
		}
	}

	fun update(ticket: Ticket) {
		doAsync {
			ticketDao.update(ticket)
		}
	}

	fun delete(ticket: Ticket) {
		doAsync {
			ticketDao.delete(ticket)
		}
	}

	fun getAll(): LiveData<List<Ticket>> {
		return allTickets
	}

	fun getFromTeam(teamId: Long): LiveData<List<Ticket>> {
		return ticketDao.getFromTeam(teamId)
	}

	fun deleteAll() {
		doAsync {
			ticketDao.deleteAll()
		}
	}

	fun refreshData() {
		val pullWorker = OneTimeWorkRequestBuilder<PullTicketsWorker>()
				.setConstraints(
						Constraints.Builder()
								.setRequiredNetworkType(NetworkType.CONNECTED)
								.build())
				.addTag("test")
				.build()

		WorkManager.getInstance().enqueue(pullWorker)
	}

	fun refreshTicket(id: Long) {
		val pullWorker = OneTimeWorkRequestBuilder<PullTicketWorker>()
				.setInputData(
						Data.Builder()
								.putLong("ticket_id", id)
								.build()
				)
				.setConstraints(
						Constraints.Builder()
								.setRequiredNetworkType(NetworkType.CONNECTED)
								.build())
				.addTag("test")
				.build()

		WorkManager.getInstance().enqueue(pullWorker)
	}

	private fun apiPullTicket(ticketId: Long) {
		apiPullTicket(ticketId, {})
	}

	private fun apiPullTicket(ticketId: Long, unauthorized: () -> Unit) {
		Api(application).retrofit {
			val ticketD: Tickets = it.create(Tickets::class.java)
			val call: Call<Ticket> = ticketD.getTicket(ticketId)
			doAsync {
				val response: Response<Ticket> = call.execute()
				if (!response.isSuccessful) {
					when (response.code()) {
						401 -> {
							unauthorized()
						}
					}
				}

				val ticket: Ticket = response.body()!!

				insert(ticket)
			}
		}
	}

	private fun apiPull() {
		apiPull({})
	}

	private fun apiPull(unauthorized: () -> Unit) {
		Api(application).retrofit {
			val ticketD: Tickets = it.create(Tickets::class.java)
			val call: Call<List<Ticket>> = ticketD.all()
			doAsync {
				val response: Response<List<Ticket>> = call.execute()
				if (!response.isSuccessful) {
					when (response.code()) {
						401 -> {
							unauthorized()
						}
					}
				}

				val tickets: List<Ticket> = response.body()!!

				val original = ticketDao.getOnlyAll()

				val remove: List<Ticket>? = original.filter { old ->
					val overlap: Ticket? = tickets.find { new ->
						new.uid == old.uid
					}
					overlap == null
				}

				if (remove != null) {
					for (ticket in remove.iterator()) {
						delete(ticket)
					}
				}

				for (ticket: Ticket in tickets) {
					insert(ticket)
				}
			}
		}
	}

	class PullTicketWorker(appContext: Context, workerParams: WorkerParameters)
		: Worker(appContext, workerParams) {
		override fun doWork(): Result {
			try {
				val ticketRepo = TicketRepository(applicationContext as Application)
				val ticketId: Long? = inputData.getLong("ticket_id", -1)
				if (ticketId != null) {
					ticketRepo.apiPullTicket(ticketId)
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

	class PullTicketsWorker(appContext: Context, workerParams: WorkerParameters)
		: Worker(appContext, workerParams) {
		override fun doWork(): Result {
			try {
				TicketRepository(applicationContext as Application).apiPull()
			} catch (e: ApiUnauthorized) {
				Log.d("ApiUnauthorized", "Caught")
			}
			return Result.success()
		}
	}
}
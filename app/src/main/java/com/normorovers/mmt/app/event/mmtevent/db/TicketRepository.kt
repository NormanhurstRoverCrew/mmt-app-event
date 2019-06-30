package com.normorovers.mmt.app.event.mmtevent.db

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import org.jetbrains.anko.doAsync

class TicketRepository(application: Application) {
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

    fun deleteAll() {
        doAsync {
            ticketDao.deleteAll()
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

    }

    class PullWorker(appContext: Context, workerParams: WorkerParameters)
        : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            TicketRepository(applicationContext as Application).apiPull()
            return Result.success()
        }

    }

}
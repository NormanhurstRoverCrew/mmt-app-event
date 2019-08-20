package com.normorovers.mmt.app.event.mmtevent.view.ticket

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.normorovers.mmt.app.event.mmtevent.db.Ticket
import com.normorovers.mmt.app.event.mmtevent.db.TicketRepository

class TicketsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TicketRepository(application)
    val allTickets = repository.getAll()

    fun insert(ticket: Ticket) {
        repository.insert(ticket)
    }

    fun update(ticket: Ticket) {
        repository.update(ticket)
    }

    fun delete(ticket: Ticket) {
        repository.delete(ticket)
    }

    fun deleteAll() {
        repository.deleteAll()
    }

    fun getAll(): LiveData<List<Ticket>> {
//        refreshData()
        return repository.getAll()
    }

    fun getFromTeam(teamId: Long): LiveData<List<Ticket>> {
        return repository.getFromTeam(teamId)
    }

    fun refreshData() {
        repository.refreshData()
    }
}
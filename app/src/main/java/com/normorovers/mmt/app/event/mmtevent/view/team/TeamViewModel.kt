package com.normorovers.mmt.app.event.mmtevent.view.team

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.normorovers.mmt.app.event.mmtevent.db.Team
import com.normorovers.mmt.app.event.mmtevent.db.TeamRepository
import com.normorovers.mmt.app.event.mmtevent.db.Ticket
import com.normorovers.mmt.app.event.mmtevent.db.TicketRepository
import org.jetbrains.anko.doAsync


class TeamViewModel(application: Application, val teamId: Long) : AndroidViewModel(application) {
	private val repository = TeamRepository(application)
	private var ticketRepository = TicketRepository(application)
	private var tickets = ticketRepository.getFromTeam(teamId)

	fun update(team: Team) {
		repository.update(team)
	}

	fun get(): LiveData<Team> {
		refreshTeamData(teamId)
		return repository.get(teamId)
	}

	fun getTickets(): LiveData<List<Ticket>> {
		return tickets
	}

	fun refreshTeamData(id: Long) {
		repository.refreshTeamData(id)
	}

	fun addTicketbyUid(ticket: String) {
		doAsync {
			val t = ticketRepository.getByUid(ticket)
			ticketRepository.updateTeamId(t, teamId)
		}
	}

	/**
	 * A creator is used to inject the product ID into the ViewModel
	 *
	 *
	 * This creator is to showcase how to inject dependencies into ViewModels. It's not
	 * actually necessary in this case, as the product ID can be passed in a public method.
	 */
	@Suppress("UNCHECKED_CAST")
	class Factory(private val application: Application, private val teamId: Long) : ViewModelProvider.NewInstanceFactory() {
		override fun <T : ViewModel> create(modelClass: Class<T>): T {
			val t = TeamViewModel(application, teamId)
			return t as T
		}
	}
}
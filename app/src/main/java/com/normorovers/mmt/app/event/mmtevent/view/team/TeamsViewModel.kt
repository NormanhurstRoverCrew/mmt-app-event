package com.normorovers.mmt.app.event.mmtevent.view.team

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.normorovers.mmt.app.event.mmtevent.db.Team
import com.normorovers.mmt.app.event.mmtevent.db.TeamRepository

class TeamsViewModel(application: Application) : AndroidViewModel(application) {
	private val repository = TeamRepository(application)
	val allTeams = repository.getAll()

	fun insert(team: Team) {
		repository.insert(team)
	}

	fun update(team: Team) {
		repository.update(team)
	}

	fun delete(team: Team) {
		repository.delete(team)
	}

	fun deleteAll() {
		repository.deleteAll()
	}

	fun getAll(): LiveData<List<Team>> {
		refreshData()
		return repository.getAll()
	}

	fun refreshData() {
		repository.refreshData()
	}
}
package com.normorovers.mmt.app.event.mmtevent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.normorovers.mmt.app.event.mmtevent.db.Team
import com.normorovers.mmt.app.event.mmtevent.db.TeamRepository

class TeamsViewModel(application: Application) : AndroidViewModel(application) {
    val repository = TeamRepository(application)
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
        return repository.getAll()
    }

    fun refreshData() {
        repository.refreshData()
    }
}
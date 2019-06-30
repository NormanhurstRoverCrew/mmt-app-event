package com.normorovers.mmt.app.event.mmtevent.db

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.normorovers.mmt.app.event.mmtevent.api.Api
import com.normorovers.mmt.app.event.mmtevent.api.Teams
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class TeamRepository(application: Application) {

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
//        doAsync {
        apiPull()
//        }
    }

    private fun apiPull() {
        val retrofit: Retrofit = Api().retrofit()

        val teamsD: Teams = retrofit.create(Teams::class.java)
        val call: Call<List<Team>> = teamsD.getTeams()
        call.enqueue(object : Callback<List<Team>> {
            override fun onResponse(call: Call<List<Team>>, response: Response<List<Team>>) {
                if (!response.isSuccessful) {
                    when (response.code()) {
                        401 -> {
                            TODO("Handle Unauthorized API Request")
                        }
                    }
                    return
                }
                val teams: List<Team> = response.body()!!

                val orriginal = allTeams.value

                val remove: List<Team>? = orriginal?.filter { old ->
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

            override fun onFailure(call: Call<List<Team>>, t: Throwable) {
                Log.e("GetTeams", t.localizedMessage!!)
                TODO("Handle Error API Request")
            }
        })
    }

}
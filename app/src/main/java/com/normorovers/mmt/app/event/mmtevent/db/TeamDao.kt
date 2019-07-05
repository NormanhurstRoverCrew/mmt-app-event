package com.normorovers.mmt.app.event.mmtevent.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TeamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(team: Team)

    @Update
    fun update(team: Team)

    @Delete
    fun delete(team: Team)

    @Query("DELETE FROM teams")
    fun deleteAll()

    @Query("SELECT * FROM teams ORDER BY uid DESC")
    fun getAll(): LiveData<List<Team>>

    @Query("SELECT * FROM teams WHERE teams.uid = :uid")
    fun get(uid: String): LiveData<Team>

    @Query("SELECT * FROM teams WHERE teams.id = :id")
    fun get(id: Long): LiveData<Team>

    @Query("SELECT * FROM teams WHERE teams.id = :id")
    fun getOnly(id: Long): Team

    @Query("SELECT * FROM teams ORDER BY uid DESC")
    fun getOnlyAll(): List<Team>

    @Query("SELECT * FROM teams WHERE uid = :team_uid")
    fun getById(team_uid: String): Team

    @Query("SELECT COUNT(*) FROM tickets WHERE team_id = :team_id")
    fun countTicketsInTeam(team_id: Long): LiveData<Long>
}
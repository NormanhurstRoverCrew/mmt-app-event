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

    @Query("SELECT * FROM teams ORDER BY uid DESC")
    fun getOnlyAll(): List<Team>

    @Query("SELECT * FROM teams ORDER BY uid DESC")
    fun loadTeamsWithTickets(): List<TeamTickets>
}
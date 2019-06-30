package com.normorovers.mmt.app.event.mmtevent.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TicketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ticket: Ticket)

    @Update
    fun update(ticket: Ticket)

    @Delete
    fun delete(ticket: Ticket)

    @Query("DELETE FROM tickets")
    fun deleteAll()

    @Query("SELECT * FROM tickets ORDER BY uid DESC")
    fun getAll(): LiveData<List<Ticket>>

    @Query("SELECT * FROM tickets ORDER BY uid DESC")
    fun getOnlyAll(): List<Ticket>
}
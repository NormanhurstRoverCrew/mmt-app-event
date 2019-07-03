package com.normorovers.mmt.app.event.mmtevent.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Team::class, Ticket::class), exportSchema = false, version = 1)
abstract class AppDatabase : RoomDatabase() {
    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it, AppDatabase::class.java, "mmt19_develpment.db")
                .fallbackToDestructiveMigration()
                .build()
    })

    abstract fun teamDao(): TeamDao
    abstract fun ticketDao(): TicketDao
}
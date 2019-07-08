package com.normorovers.mmt.app.event.mmtevent.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Team::class, Ticket::class, ActivityLog::class], exportSchema = false, version = 5)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it, AppDatabase::class.java, "mmt19_develpment.db")
                .fallbackToDestructiveMigration()
                .build()
    })

    abstract fun teamDao(): TeamDao
    abstract fun ticketDao(): TicketDao
	abstract fun activityLogDao(): ActivityLogDao
}
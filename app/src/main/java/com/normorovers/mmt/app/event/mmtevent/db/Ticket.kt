package com.normorovers.mmt.app.event.mmtevent.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
class Ticket(
        @PrimaryKey val uid: String?,
        @Embedded val user: User) {

    override fun toString(): String {
        return "uid: $uid, user: {$user}"
    }
}
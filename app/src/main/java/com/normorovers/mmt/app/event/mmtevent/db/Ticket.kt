package com.normorovers.mmt.app.event.mmtevent.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tickets")
class Ticket(
        @PrimaryKey(autoGenerate = false) val uid: String,
        val id: Long,
        @ColumnInfo(name = "team_id") @SerializedName("team_id") var teamId: Long,
        @Embedded val user: User
) {

    override fun toString(): String {
        return "uid: $uid, team id: ${teamId}, user: {$user}"
    }

}
package com.normorovers.mmt.app.event.mmtevent.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
class Team(
		@PrimaryKey(autoGenerate = false) @ColumnInfo(name = "uid") var uid: String = "",
		@ColumnInfo(name = "id") var id: Long = -1,
		@ColumnInfo(name = "name") var name: String = "",
		@ColumnInfo(name = "registration") var registration: String = "",
		@Ignore var tickets: List<Ticket>? = null,
		@Ignore var nTickets: Long? = null
) {
    override fun toString(): String {
        return "$uid name: $name, registration: $registration"
    }

    override fun equals(other: Any?): Boolean {
        return (this.name == (other as Team).name) && (this.registration == (other).registration)
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + registration.hashCode()
        return result
    }
}
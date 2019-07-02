package com.normorovers.mmt.app.event.mmtevent.db

import androidx.room.Embedded
import androidx.room.Relation

class TeamTickets(
		@Embedded() val team: Team,

		@Relation(parentColumn = "id", entityColumn = "teamId", entity = Ticket::class)
		val tickets: List<Ticket>
)
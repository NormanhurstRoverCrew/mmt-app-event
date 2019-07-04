package com.normorovers.mmt.app.event.mmtevent.api

import com.normorovers.mmt.app.event.mmtevent.db.Ticket
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Tickets {
	@GET("tickets")
	fun all(): Call<List<Ticket>>

	@GET("tickets/{id}")
	fun getTicket(@Path("id") id: Long): Call<Ticket>
}

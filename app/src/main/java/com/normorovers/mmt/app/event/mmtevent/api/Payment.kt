package com.normorovers.mmt.app.event.mmtevent.api

import com.normorovers.mmt.app.event.mmtevent.view.ticket.PaymentResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Payment {
	@GET("payments/{uid}")
	fun hasTicketPaid(@Path("uid") uid: String): Call<PaymentResult>
}

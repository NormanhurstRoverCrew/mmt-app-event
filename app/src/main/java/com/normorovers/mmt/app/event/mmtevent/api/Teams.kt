package com.normorovers.mmt.app.event.mmtevent.api

import com.normorovers.mmt.app.event.mmtevent.db.Team
import retrofit2.Call
import retrofit2.http.*

interface Teams {
    @GET("teams")
    fun getTeams(): Call<List<Team>>

    @GET("teams/{id}")
    fun getTeam(@Path("id") id: Int): Call<Team>

    @GET("teams/{id}")
    fun getTeam(
            @Path("id") id: Int,
            @Query("load_tickets") loadTickets: Boolean
    ): Call<Team>

    @GET("teams/{id}/point_logs")
    fun getPointLogs(@Path("id") id: Int): Call<Team>

    @POST("teams/{id}/point_logs")
    fun postPointLogs(@Body teams: List<Teams>): Call<List<Team>>
}

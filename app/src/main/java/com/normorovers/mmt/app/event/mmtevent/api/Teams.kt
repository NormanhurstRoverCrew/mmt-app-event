package com.normorovers.mmt.app.event.mmtevent.api

import com.normorovers.mmt.app.event.mmtevent.db.Team
import retrofit2.Call
import retrofit2.http.*

interface Teams {
    @GET("teams")
    fun getTeams(): Call<List<Team>>

    @GET("teams/{id}")
	fun getTeam(@Path("id") id: Long): Call<Team>

    @GET("teams/{id}")
    fun getTeam(
			@Path("id") id: Long,
			@Query("load_tickets") loadTickets: Boolean
    ): Call<Team>

    @GET("teams/{id}/point_logs")
    fun getPointLogs(@Path("id") id: Int): Call<Team>

    @POST("teams/{id}/point_logs")
	fun postPointLogs(@Body teams: List<Team>): Call<List<Team>>

	@PUT("teams/{id}")
	fun update(@Path("id") team_id: Long, @Body team: Team): Call<Team>
}

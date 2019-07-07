package com.normorovers.mmt.app.event.mmtevent.api

import com.normorovers.mmt.app.event.mmtevent.db.ActivityLog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ActivityLogs {
	@POST("base/logs")
	fun sendLogs(@Body logs: List<ActivityLog>): Call<ResponseBody>
}

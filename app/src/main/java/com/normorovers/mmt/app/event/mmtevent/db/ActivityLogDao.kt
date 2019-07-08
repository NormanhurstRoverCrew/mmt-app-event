package com.normorovers.mmt.app.event.mmtevent.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ActivityLogDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(activity_log: ActivityLog)

	@Update
	fun update(activity_log: ActivityLog)

	@Delete
	fun delete(activity_log: ActivityLog)

	@Query("DELETE FROM activity_logs")
	fun deleteAll()

	@Query("SELECT * FROM activity_logs")
	fun getAll(): LiveData<List<ActivityLog>>

	@Query("SELECT * FROM activity_logs")
	fun getOnlyAll(): List<ActivityLog>

	@Query("SELECT * FROM activity_logs WHERE synced = 0")
	fun getUnsnced(): List<ActivityLog>
}
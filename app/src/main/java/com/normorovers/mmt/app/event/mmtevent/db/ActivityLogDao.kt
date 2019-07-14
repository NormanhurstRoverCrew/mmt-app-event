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
	fun getUnsynced(): List<ActivityLog>

	@Query("SELECT * FROM activity_logs WHERE base = :base_id ORDER BY logged_at DESC")
	fun get(base_id: Int): List<ActivityLog>

	@Query("SELECT * FROM activity_logs WHERE team = :team_id ORDER BY logged_at DESC")
	fun getTeam(team_id: Long): List<ActivityLog>

	@Query("SELECT * FROM activity_logs WHERE team = :team_id AND base = :base_id ORDER BY logged_at ASC")
	fun getTeam(team_id: Long, base_id: Int): List<ActivityLog>

	@Query("SELECT * FROM activity_logs WHERE team = :team_id AND base = :base_id AND arrived = 1 ORDER BY logged_at DESC")
	fun getTeamArrived(team_id: Long, base_id: Int): ActivityLog

	@Query("SELECT * FROM activity_logs WHERE team = :team_id AND base = :base_id AND departed = 1 ORDER BY logged_at DESC")
	fun getTeamDeparted(team_id: Long, base_id: Int): ActivityLog

	@Query("SELECT * FROM activity_logs WHERE team = :team_id AND base = :base_id AND (arrived = 1 OR departed = 1) ORDER BY logged_at ASC")
	fun getTeamArrivedOrDeparted(team_id: Long, base_id: Int): List<ActivityLog>

	@Query("SELECT * FROM activity_logs WHERE team = :team_id AND base = :base_id AND (arrived = 1 OR departed = 1) ORDER BY logged_at ASC")
	fun getTeamArrivedOrDepartedLive(team_id: Long, base_id: Int): LiveData<List<ActivityLog>>

	@Query("SELECT * FROM activity_logs WHERE team = :team_id AND base = :base_id AND comment IS NOT NULL ORDER BY logged_at DESC")
	fun getTeamComments(team_id: Long, base_id: Int): List<ActivityLog>

	@Query("SELECT * FROM activity_logs WHERE team = :team_id AND base = :base_id AND points IS NOT NULL ORDER BY logged_at DESC")
	fun getTeamPoints(team_id: Long, base_id: Int): List<ActivityLog>

	@Query("SELECT * FROM activity_logs WHERE team = :team_id AND base = :base_id AND trivia IS NOT NULL ORDER BY logged_at DESC")
	fun getTeamTrivia(team_id: Long, base_id: Int): List<ActivityLog>

	@Query("SELECT * FROM activity_logs WHERE team = :team_id AND base = :base_id AND clues IS NOT NULL ORDER BY logged_at DESC")
	fun getTeamClues(team_id: Long, base_id: Int): List<ActivityLog>

	@Query("SELECT * FROM activity_logs WHERE team = :team_id AND base = :base_id AND (points IS NOT NULL OR trivia IS NOT NULL) ORDER BY logged_at DESC")
	fun getTeamAllPoints(team_id: Long, base_id: Int): List<ActivityLog>
}
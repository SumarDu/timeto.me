package me.timeto.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Query("SELECT MAX(start_time) FROM events")
    fun getLastEventStartTime(): Long?

    @Query("SELECT * FROM events WHERE end_time IS NULL LIMIT 1")
    fun getOngoingEvent(): Event?

    @Query("SELECT * FROM events ORDER BY start_time DESC")
    fun getAllEvents(): Flow<List<Event>>
}

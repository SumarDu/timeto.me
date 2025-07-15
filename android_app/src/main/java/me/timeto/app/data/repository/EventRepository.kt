package me.timeto.app.data.repository

import me.timeto.app.data.database.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.timeto.app.data.database.EventDao
import me.timeto.app.data.supabase.SupabaseClient
import me.timeto.shared.vm.history.HistoryEvent
import me.timeto.shared.vm.history.HistoryRepository
import io.github.jan.supabase.postgrest.from
import android.util.Log

class EventRepository(private val eventDao: EventDao) : HistoryRepository {

    suspend fun syncEvents() {
        Log.d("EventRepository", "Starting Supabase sync...")
        try {
            val lastSyncTime = eventDao.getLastEventStartTime()
            val ongoingEvent = eventDao.getOngoingEvent()

            val query = SupabaseClient.client.from("quest_events").select {
                if (lastSyncTime != null) {
                    filter {
                        or {
                            gt("start_time", lastSyncTime)
                            ongoingEvent?.let { eq("id", it.id) }
                        }
                    }
                }
            }

            val eventsFromSupabase = query.decodeList<Event>()

            if (eventsFromSupabase.isNotEmpty()) {
                eventsFromSupabase.forEach { event ->
                    eventDao.insert(event)
                }
                Log.d("EventRepository", "Sync successful. ${eventsFromSupabase.size} events upserted.")
            } else {
                Log.d("EventRepository", "Sync successful. No new events.")
            }
        } catch (e: Exception) {
            Log.e("EventRepository", "Sync failed", e)
            e.printStackTrace()
        }
    }

    fun getAllEvents() = eventDao.getAllEvents()

    override fun getEvents(): Flow<List<HistoryEvent>> {
        return eventDao.getAllEvents().map {
            it.map { event ->
                HistoryEvent(
                    uuid = event.uuid,
                    id = event.id,
                    eventName = event.eventName,
                                        startTime = event.startTime.toString(),
                                                            endTime = event.endTime?.toString() ?: "",
                    colorRgba = event.colorRgba
                )
            }
        }
    }
}

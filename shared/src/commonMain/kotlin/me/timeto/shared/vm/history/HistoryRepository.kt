package me.timeto.shared.vm.history

import kotlinx.coroutines.flow.Flow

// Common data class for history events
data class HistoryEvent(
    val uuid: String,
    val id: Int,
    val eventName: String,
    val startTime: String, // Assuming ISO format string
    val endTime: String,   // Assuming ISO format string
    val colorRgba: String
)

// Repository interface in the shared module
interface HistoryRepository {
    fun getEvents(): Flow<List<HistoryEvent>>
}

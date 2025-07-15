package me.timeto.app.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import androidx.room.PrimaryKey

import java.util.UUID

@Entity(tableName = "events")
@Serializable
data class Event(
    @ColumnInfo(name = "uuid")
    @SerialName("uuid")
    val uuid: String = java.util.UUID.randomUUID().toString(),

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerialName("id")
    val id: Int,

    @ColumnInfo(name = "event_name")
    @SerialName("event_name")
    val eventName: String,

    @ColumnInfo(name = "start_time")
        @SerialName("start_time")
    val startTime: Long,

    @ColumnInfo(name = "end_time")
        @SerialName("end_time")
    val endTime: Long? = null,

    @ColumnInfo(name = "color_rgba")
        @SerialName("color_rgba")
    val colorRgba: String
)

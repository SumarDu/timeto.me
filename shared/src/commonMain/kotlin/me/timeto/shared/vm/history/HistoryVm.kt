package me.timeto.shared.vm.history

import kotlinx.coroutines.flow.*
import me.timeto.shared.*

import me.timeto.shared.limitMax
import me.timeto.shared.limitMin
import me.timeto.shared.time
import me.timeto.shared.DialogsManager
import me.timeto.shared.vm.history.form.HistoryFormUtils
import me.timeto.shared.vm.Vm
import me.timeto.shared.vm.history.colorFromRgbaString

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class HistoryVm(private val historyRepository: HistoryRepository) : Vm<HistoryVm.State>() {

    data class State(
        val daysUi: List<DayUi>,
    )

    override val state = MutableStateFlow(
        State(
            daysUi = emptyList(),
        )
    )

    init {
        val scopeVm = scopeVm()
        historyRepository.getEvents().onEachExIn(scopeVm) { events ->
            state.update {
                it.copy(
                    daysUi = makeDaysUi(events = events)
                )
            }
        }
    }

    // TODO: This functionality needs to be re-evaluated as it depended on the old data model.
    /*
    fun moveIntervalToTasks(
        intervalDb: HistoryEvent,
        dialogsManager: DialogsManager,
    ) {
        HistoryFormUtils.moveToTasksUi(
            intervalDb = intervalDb,
            dialogsManager = dialogsManager,
            onSuccess = {},
        )
    }

    fun deleteInterval(
        intervalDb: HistoryEvent,
        dialogsManager: DialogsManager,
    ) {
        HistoryFormUtils.deleteIntervalUi(
            intervalDb = intervalDb,
            dialogsManager = dialogsManager,
            onSuccess = {},
        )
    }
    */
}

///

class DayUi(
        val unixDay: Int,
        val events: List<HistoryEvent>,
        val nextEventStartTime: Int,
    ) {

        private val dayUnixTime: UnixTime = UnixTime.byLocalDay(unixDay)
        private val dayTimeStart: Int = dayUnixTime.time
        private val dayTimeFinish: Int = dayTimeStart + 86400 - 1

        val intervalsUi: List<IntervalUi> = events.map { event ->
            val startTime = Instant.fromEpochMilliseconds(event.startTime.toLong()).epochSeconds.toInt()
            val endTime = if (event.endTime.isEmpty()) {
                Clock.System.now().epochSeconds.toInt()
            } else {
                Instant.fromEpochMilliseconds(event.endTime.toLong()).epochSeconds.toInt()
            }
            val unixTime = UnixTime(startTime)

            val finishTime = events.getNextOrNull(event)?.let { Instant.fromEpochMilliseconds(it.startTime.toLong()).epochSeconds.toInt() } ?: nextEventStartTime
            val seconds = (endTime - startTime).limitMin(0)
            val barTimeFinish = dayTimeFinish.limitMax(endTime)

            IntervalUi(
                event = event,
                isStartsPrevDay = unixTime.localDay < unixDay,
                text = event.eventName.textFeatures().textUi(
                    withActivityEmoji = false,
                    withTimer = false,
                ),
                secondsForBar = (barTimeFinish - dayTimeStart.limitMin(startTime)),
                barTimeFinish = barTimeFinish,
                timeString = unixTime.getStringByComponents(UnixTime.StringComponent.hhmm24),
                periodString = makePeriodString(seconds),
                color = colorFromRgbaString(event.colorRgba),
            )
        }

        val dayText: String = UnixTime.byLocalDay(unixDay).getStringByComponents(
            UnixTime.StringComponent.dayOfMonth,
            UnixTime.StringComponent.space,
            UnixTime.StringComponent.month,
            UnixTime.StringComponent.comma,
            UnixTime.StringComponent.space,
            UnixTime.StringComponent.dayOfWeek3,
        )
    }

        data class IntervalUi(
        val event: HistoryEvent,
        val isStartsPrevDay: Boolean,
        val text: String,
        val secondsForBar: Int,
        val barTimeFinish: Int,
        val timeString: String,
        val periodString: String,
        val color: ColorRgba,
    )

///

///

private fun makePeriodString(
    seconds: Int,
): String {
    if (seconds < 60)
        return "$seconds sec"
    if (seconds < 3_600)
        return "${seconds / 60} min"
    val (h, m, _) = seconds.toHms()
    if (m == 0)
        return "${h}h"
    return "${h}h ${m.toString().padStart(2, '0')}m"
}

private fun makeDaysUi(events: List<HistoryEvent>): List<DayUi> {
    if (events.isEmpty()) return emptyList()

    val eventsWithTime = events.map { event ->
                val startTime = Instant.fromEpochMilliseconds(event.startTime.toLong()).epochSeconds.toInt()
        Pair(event, UnixTime(startTime))
    }.sortedBy { it.second.time }

    val groupedByDay = eventsWithTime.groupBy { it.second.localDay }

    val daysUi = mutableListOf<DayUi>()
    val sortedDays = groupedByDay.keys.sorted()

    sortedDays.forEachIndexed { index, day ->
        val dayEvents = groupedByDay[day]!!.map { it.first }
        val nextDayKey = if (index + 1 < sortedDays.size) sortedDays[index + 1] else null
        val nextEventStartTime = nextDayKey?.let { key -> groupedByDay[key]?.firstOrNull()?.second?.time } ?: time()

        daysUi.add(
            DayUi(
                unixDay = day,
                events = dayEvents,
                nextEventStartTime = nextEventStartTime
            )
        )
    }

    return daysUi.reversed()
}


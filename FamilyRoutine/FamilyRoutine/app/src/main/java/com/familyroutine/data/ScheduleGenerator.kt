package com.familyroutine.data

// Автогенерация распорядка дня по возрасту и настройкам сна
object ScheduleGenerator {

    fun generateDay(age: Int, wakeMinute: Int, bedMinute: Int, hasSchool: Boolean): List<DayEvent> {
        val events = mutableListOf<DayEvent>()
        events += DayEvent("Подъём", "🌅", wakeMinute, wakeMinute + 30)
        events += DayEvent("Зарядка", "🤸", wakeMinute + 30, wakeMinute + 45)
        events += DayEvent("Завтрак", "🥞", wakeMinute + 45, wakeMinute + 75)

        if (hasSchool) {
            events += DayEvent("Школа", "🏫", 8 * 60, 13 * 60 + 30)
            events += DayEvent("Обед", "🍝", 13 * 60 + 30, 14 * 60 + 15)
            events += DayEvent("Домашние задания", "📚", 14 * 60 + 15, 15 * 60 + 15)
        } else {
            events += DayEvent("Прогулка", "🌳", 10 * 60, 12 * 60)
            events += DayEvent("Обед", "🍝", 12 * 60 + 30, 13 * 60)
        }

        events += DayEvent("Свободное время", "🎮", 15 * 60 + 15, 18 * 60 + 30)
        events += DayEvent("Ужин", "🍽️", 18 * 60 + 30, 19 * 60 + 15)
        if (age >= 6) events += DayEvent("Чтение", "📖", 19 * 60 + 15, 19 * 60 + 45)
        events += DayEvent("Подготовка ко сну", "🛁", bedMinute - 45, bedMinute - 15)
        events += DayEvent("Сон", "😴", bedMinute - 15, bedMinute)

        return events.sortedBy { it.startMinute }
    }

    fun format(minute: Int): String = "%02d:%02d".format(minute / 60, minute % 60)
}

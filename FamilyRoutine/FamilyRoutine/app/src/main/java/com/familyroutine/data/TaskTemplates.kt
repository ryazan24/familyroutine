package com.familyroutine.data

// Библиотека шаблонов заданий по возрастным группам: 4–6, 7–9, 10–16
object TaskTemplates {

    fun forAge(age: Int): List<KidTask> = when {
        age <= 6 -> listOf(
            KidTask(title = "Почистить зубы", emoji = "🪥", category = TaskCategory.HYGIENE, points = 1),
            KidTask(title = "Собрать игрушки", emoji = "🧸", category = TaskCategory.CLEANING, points = 2),
            KidTask(title = "Сам одеться", emoji = "🧦", category = TaskCategory.HYGIENE, points = 2),
            KidTask(title = "Вымыть руки перед едой", emoji = "🧼", category = TaskCategory.HYGIENE, points = 1)
        )
        age <= 9 -> listOf(
            KidTask(title = "Почистить зубы", emoji = "🪥", category = TaskCategory.HYGIENE, points = 1),
            KidTask(title = "Сделать зарядку", emoji = "🤸", category = TaskCategory.SPORT, points = 2),
            KidTask(title = "Убрать игрушки", emoji = "🧸", category = TaskCategory.CLEANING, points = 2),
            KidTask(title = "Прочитать 10 минут", emoji = "📖", category = TaskCategory.READING, points = 3),
            KidTask(title = "Помочь накрыть на стол", emoji = "🤝", category = TaskCategory.HELP, points = 2)
        )
        else -> listOf(
            KidTask(title = "Сделать уроки", emoji = "📚", category = TaskCategory.STUDY, points = 5),
            KidTask(title = "Убрать комнату", emoji = "🧹", category = TaskCategory.CLEANING, points = 3),
            KidTask(title = "Спорт 20 минут", emoji = "⚽", category = TaskCategory.SPORT, points = 3),
            KidTask(title = "Прочитать 20 минут", emoji = "📖", category = TaskCategory.READING, points = 3),
            KidTask(title = "Помыть посуду", emoji = "🍽️", category = TaskCategory.HELP, points = 3)
        )
    }
}

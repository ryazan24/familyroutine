package com.familyroutine.data

import java.util.UUID

// Типы аккаунтов: PARENT — родитель, CHILD — 4–9 лет, TEEN — 10–16 лет, AI — сообщения помощника
enum class AccountType { PARENT, CHILD, TEEN, AI }

enum class TaskCategory(val emoji: String, val titleRu: String) {
    HYGIENE("🪥", "Гигиена"),
    CLEANING("🧹", "Уборка"),
    STUDY("📚", "Учёба"),
    SPORT("⚽", "Спорт"),
    READING("📖", "Чтение"),
    HELP("🤝", "Помощь по дому")
}

enum class TaskStatus { PENDING, DONE, MISSED }

data class FamilyUser(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val accountType: AccountType,
    val age: Int,
    val stars: Int = 0
)

data class KidTask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val emoji: String,
    val category: TaskCategory,
    val scheduledMinuteOfDay: Int? = null, // null = "в течение дня"
    val points: Int,
    val repeatDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val status: TaskStatus = TaskStatus.PENDING,
    val createdByParent: Boolean = true
)

data class DayEvent(
    val title: String,
    val emoji: String,
    val startMinute: Int,
    val endMinute: Int,
    val isFixed: Boolean = true // школа/сон нельзя двигать
)

data class Reward(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val costStars: Int
)

data class ChatMessage(
    val author: AccountType,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

package com.familyroutine

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.familyroutine.ai.AiRole
import com.familyroutine.ai.LocalStubBackend
import com.familyroutine.ai.SafeAiAssistant
import com.familyroutine.data.AccountType
import com.familyroutine.data.ChatMessage
import com.familyroutine.data.FamilyUser
import com.familyroutine.data.FirebaseSyncRepository
import com.familyroutine.data.KidTask
import com.familyroutine.data.NoopSyncRepository
import com.familyroutine.data.ScheduleGenerator
import com.familyroutine.data.SyncRepository
import com.familyroutine.data.TaskCategory
import com.familyroutine.data.TaskStatus
import com.familyroutine.data.TaskTemplates
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class FamilyViewModel(app: Application) : AndroidViewModel(app) {

    var child by mutableStateOf(
        FamilyUser(name = "Миша", accountType = AccountType.CHILD, age = 7)
    )
        private set

    var tasks by mutableStateOf(TaskTemplates.forAge(child.age))
        private set

    var wakeMinute by mutableStateOf(7 * 60 + 30) // 07:30
    var bedMinute by mutableStateOf(21 * 60)      // 21:00

    var events by mutableStateOf(
        ScheduleGenerator.generateDay(child.age, wakeMinute, bedMinute, hasSchool = true)
    )
        private set

    var aiMessages by mutableStateOf(
        listOf(
            ChatMessage(
                AccountType.AI,
                "Привет! Я твой ИИ-помощник 🦉 Помогу с домашкой или просто поболтаю!"
            )
        )
    )
        private set

    // ---- Синхронизация ----
    var familyCode by mutableStateOf("")
        private set

    var syncStatus by mutableStateOf("Синхронизация не подключена")
        private set

    // Если google-services.json добавлен — Firebase инициализирован, берём его.
    // Иначе молча работаем офлайн (NoopSyncRepository).
    private val sync: SyncRepository = try {
        if (FirebaseApp.getApps(app).isNotEmpty()) {
            FirebaseSyncRepository()
        } else {
            NoopSyncRepository()
        }
    } catch (e: Exception) {
        NoopSyncRepository()
    }

    // Защита от эха: пока применяем данные с сервера — не пушим обратно
    private var applyingRemote = false

    init {
        if (!sync.available) {
            syncStatus = "Добавьте google-services.json, чтобы включить синхронизацию (см. README)"
        }
    }

    private val ai = SafeAiAssistant(LocalStubBackend())

    // ---- Задания и звёздочки ----

    // Ребёнок отметил задание выполненным → +звёздочки → push на сервер
    fun completeTask(id: String) {
        val done = tasks.firstOrNull { it.id == id && it.status != TaskStatus.DONE } ?: return
        tasks = tasks.map { if (it.id == id) it.copy(status = TaskStatus.DONE) else it }
        child = child.copy(stars = child.stars + done.points)
        pushAll()
    }

    // Родитель добавил задание вручную
    fun addTask(title: String, points: Int) {
        tasks = tasks + KidTask(
            title = title,
            emoji = "⭐",
            category = TaskCategory.HELP,
            points = points
        )
        pushAll()
    }

    // ---- Распорядок ----

    fun regenerateSchedule() {
        events = ScheduleGenerator.generateDay(
            age = child.age,
            wakeMinute = wakeMinute,
            bedMinute = bedMinute,
            hasSchool = child.age in 7..16
        )
    }

    // ---- Семейный код ----

    // Родитель создаёт код; называет его ребёнку, тот вводит на своём устройстве
    fun createFamilyCode() {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // без похожих символов
        familyCode = (1..6).map { alphabet.random() }.joinToString("")
        attachListeners()
        pushAll()
        syncStatus = "Код создан! Назовите его ребёнку: $familyCode"
    }

    // Ребёнок (или второй родитель) подключается по коду
    fun joinFamily(code: String) {
        val normalized = code.uppercase().trim()
        if (normalized.length < 4) {
            syncStatus = "Код слишком короткий"
            return
        }
        familyCode = normalized
        attachListeners()
        syncStatus = if (sync.available) {
            "Подключено к семье $familyCode ✅"
        } else {
            "Подключено локально (синхронизация активируется после настройки Firebase)"
        }
    }

    private fun attachListeners() {
        if (familyCode.isBlank() || !sync.available) return
        sync.joinFamily(
            code = familyCode,
            onTasks = { remote ->
                applyingRemote = true
                tasks = remote
                applyingRemote = false
            },
            onStars = { remote ->
                applyingRemote = true
                child = child.copy(stars = remote)
                applyingRemote = false
            }
        )
    }

    private fun pushAll() {
        if (familyCode.isBlank() || !sync.available || applyingRemote) return
        sync.pushTasks(familyCode, tasks)
        sync.pushStars(familyCode, child.stars)
    }

    // ---- ИИ-помощник ----

    fun askAi(text: String) {
        if (text.isBlank()) return
        aiMessages = aiMessages + ChatMessage(AccountType.CHILD, text)

        val lowered = text.lowercase()
        val role = when {
            listOf("математ", "урок", "домашк", "задач", "пример", "проверь")
                .any { lowered.contains(it) } -> AiRole.HOMEWORK_HELPER
            listOf("распорядок", "задание", "что мне делать")
                .any { lowered.contains(it) } -> AiRole.ROUTINE_COACH
            else -> AiRole.FRIEND
        }

        viewModelScope.launch {
            val reply = ai.ask(role, text)
            aiMessages = aiMessages + ChatMessage(AccountType.AI, reply.text)
        }
    }
}

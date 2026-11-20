package com.familyroutine.ai

import kotlinx.coroutines.delay

// Роли ИИ-помощника
enum class AiRole { HOMEWORK_HELPER, FRIEND, ROUTINE_COACH }

data class AiReply(val text: String, val safe: Boolean = true)

interface AiAssistant {
    suspend fun ask(role: AiRole, message: String): AiReply
}

interface AiBackend {
    suspend fun complete(systemPrompt: String, userMessage: String): AiReply
}

// Безопасная обёртка: контент-фильтр + системные промпты по ролям.
// Все ответы ИИ для детей проходят через неё.
class SafeAiAssistant(private val backend: AiBackend) : AiAssistant {

    private val blockedKeywords = listOf(
        "суицид", "убить себя", "порно", "наркотик", "затравл", "обижают"
    )

    override suspend fun ask(role: AiRole, message: String): AiReply {
        // 1) Мягкий контент-фильтр: опасные темы → перенаправляем к родителю
        if (blockedKeywords.any { message.lowercase().contains(it) }) {
            return AiReply(
                "Мне кажется, об этом важно поговорить с мамой или папой — давай я позову их. " +
                "А ещё я могу помочь тебе с домашкой 😊"
            )
        }

        // 2) Системный промпт по роли
        val system = when (role) {
            AiRole.HOMEWORK_HELPER ->
                "Ты дружелюбный помощник по учёбе для ребёнка 4-16 лет. Объясняй простым языком, " +
                "не давай готовый ответ сразу — сначала подскажи ход рассуждений и похвали за попытку."
            AiRole.FRIEND ->
                "Ты добрый собеседник ребёнка. Говори коротко, тепло и безопасно: никаких опасных, " +
                "страшных или взрослых тем. Проявляй интерес к его дню и хобби."
            AiRole.ROUTINE_COACH ->
                "Ты координатор семейного распорядка. Напоминай о заданиях добро и игриво, " +
                "превращай рутину в маленькое приключение."
        }

        return backend.complete(system, message)
    }
}

// Локальный демо-бэкенд: приложение работает без интернета и API-ключа.
class LocalStubBackend : AiBackend {
    override suspend fun complete(systemPrompt: String, userMessage: String): AiReply {
        delay(600) // имитация сетевой задержки
        val text = userMessage.lowercase()
        return AiReply(
            when {
                listOf("математ", "пример", "задач", "проверь").any { text.contains(it) } ->
                    "Конечно! Скинь мне пример, и давай разберём его по шагам — " +
                    "сначала попробуй сам, а если застрянешь, я подскажу 🧮"
                listOf("скучно", "болт", "как дела").any { text.contains(it) } ->
                    "Привет-привет! Расскажи, как прошёл твой день? " +
                    "А я знаю крутые факты про космос и динозавров 🦖"
                listOf("распорядок", "задание", "что мне делать").any { text.contains(it) } ->
                    "Сегодня у тебя на день задания — открывай «Мой день» и зарабатывай звёздочки! ⭐"
                else ->
                    "Понял тебя! Могу помочь с домашкой 📚, поболтать 🦖 " +
                    "или напомнить, что сегодня нужно сделать ✅"
            }
        )
    }
}

// Заготовка реального бэкенда (Gemini). ВАЖНО: ключ API нельзя хранить в приложении —
// делайте серверный прокси (см. README).
class GeminiBackend(private val apiKey: String) : AiBackend {
    override suspend fun complete(systemPrompt: String, userMessage: String): AiReply {
        // Пример запроса:
        // POST https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey
        // { "system_instruction": { "parts": [{ "text": systemPrompt }] },
        //   "contents": [{ "parts": [{ "text": userMessage }] }] }
        return AiReply("Подключите серверный прокси для настоящего ИИ (см. README).")
    }
}

package com.familyroutine.data

// Слой синхронизации между устройствами.
// По умолчанию — NoopSyncRepository (приложение полностью работает офлайн).
// Когда добавлен google-services.json, FamilyViewModel сам переключается на FirebaseSyncRepository.
interface SyncRepository {
    val available: Boolean
    fun joinFamily(code: String, onTasks: (List<KidTask>) -> Unit, onStars: (Int) -> Unit)
    fun pushTasks(code: String, tasks: List<KidTask>)
    fun pushStars(code: String, stars: Int)
}

class NoopSyncRepository : SyncRepository {
    override val available = false
    override fun joinFamily(code: String, onTasks: (List<KidTask>) -> Unit, onStars: (Int) -> Unit) {}
    override fun pushTasks(code: String, tasks: List<KidTask>) {}
    override fun pushStars(code: String, stars: Int) {}
}

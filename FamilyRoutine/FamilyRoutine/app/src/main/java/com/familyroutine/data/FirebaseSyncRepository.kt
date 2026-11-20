package com.familyroutine.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Синхронизация через Firebase Realtime Database.
// Структура данных:
//   families/{СЕМЕЙНЫЙ_КОД}/tasks/{taskId} -> поля задания
//   families/{СЕМЕЙНЫЙ_КОД}/stars        -> общий счётчик звёздочек
class FirebaseSyncRepository : SyncRepository {

    override val available = true
    private val db = FirebaseDatabase.getInstance().reference
    private val listeners = mutableListOf<ValueEventListener>()

    override fun joinFamily(
        code: String,
        onTasks: (List<KidTask>) -> Unit,
        onStars: (Int) -> Unit
    ) {
        // Отписываемся от предыдущей семьи
        listeners.forEach { db.removeEventListener(it) }
        listeners.clear()

        val ref = db.child("families").child(code)

        val tasksListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Пустой список не применяем: не затираем локальные данные при первом подключении
                val remote = snapshot.children.mapNotNull { taskFrom(it) }
                if (remote.isNotEmpty()) onTasks(remote)
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        val starsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Int::class.java)?.let(onStars)
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        ref.child("tasks").addValueEventListener(tasksListener)
        ref.child("stars").addValueEventListener(starsListener)
        listeners += tasksListener
        listeners += starsListener
    }

    override fun pushTasks(code: String, tasks: List<KidTask>) {
        db.child("families").child(code).child("tasks")
            .setValue(tasks.associate { it.id to taskToMap(it) })
    }

    override fun pushStars(code: String, stars: Int) {
        db.child("families").child(code).child("stars").setValue(stars)
    }

    private fun taskToMap(t: KidTask): Map<String, Any> = mapOf(
        "id" to t.id,
        "title" to t.title,
        "emoji" to t.emoji,
        "category" to t.category.name,
        "points" to t.points,
        "status" to t.status.name
    )

    private fun taskFrom(s: DataSnapshot): KidTask? {
        val title = s.child("title").getValue(String::class.java) ?: return null
        val emoji = s.child("emoji").getValue(String::class.java) ?: "⭐"
        val categoryName = s.child("category").getValue(String::class.java)
        val statusName = s.child("status").getValue(String::class.java)
        val points = s.child("points").getValue(Int::class.java) ?: 1

        val category = runCatching { TaskCategory.valueOf(categoryName ?: "") }
            .getOrDefault(TaskCategory.HELP)
        val status = runCatching { TaskStatus.valueOf(statusName ?: "") }
            .getOrDefault(TaskStatus.PENDING)

        return KidTask(
            id = s.child("id").getValue(String::class.java) ?: s.key ?: return null,
            title = title,
            emoji = emoji,
            category = category,
            points = points,
            status = status
        )
    }
}

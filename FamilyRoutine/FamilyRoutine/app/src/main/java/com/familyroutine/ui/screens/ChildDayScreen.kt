package com.familyroutine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.familyroutine.FamilyViewModel
import com.familyroutine.data.DayEvent
import com.familyroutine.data.KidTask
import com.familyroutine.data.ScheduleGenerator
import com.familyroutine.data.TaskStatus

// Экран ребёнка: таймлайн дня + крупные карточки заданий + подключение к семье по коду
@Composable
fun ChildDayScreen(vm: FamilyViewModel, onOpenChat: () -> Unit) {
    var showJoin by remember { mutableStateOf(false) }
    var joinCode by remember { mutableStateOf("") }

    // Первый запуск: предлагаем подключиться к родителю по семейному коду
    if (showJoin) {
        AlertDialog(
            onDismissRequest = { showJoin = false },
            title = { Text("Подключиться к семье 🔗") },
            text = {
                Column {
                    Text("Попроси у родителей семейный код и введи его сюда:")
                    OutlinedTextField(
                        value = joinCode,
                        onValueChange = { joinCode = it },
                        label = { Text("Семейный код") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        vm.syncStatus,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.joinFamily(joinCode)
                    showJoin = false
                }) { Text("Подключиться") }
            },
            dismissButton = {
                TextButton(onClick = { showJoin = false }) { Text("Позже") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Привет, ${vm.child.name}! 🌞", style = MaterialTheme.typography.headlineMedium)
            Text(
                "⭐ Звёздочек: ${vm.child.stars}",
                style = MaterialTheme.typography.titleMedium
            )
            if (vm.familyCode.isEmpty()) {
                TextButton(onClick = { showJoin = true }) {
                    Text("🔗 Подключиться к семье")
                }
            }
            Text(
                "Твой день",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        items(vm.events, key = { "${it.title}${it.startMinute}" }) { event ->
            EventCard(event)
        }
        item {
            Text(
                "Задания на сегодня",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        items(vm.tasks, key = { it.id }) { task ->
            TaskCard(task, onDone = { vm.completeTask(task.id) })
        }
        item {
            Button(onClick = onOpenChat, modifier = Modifier.fillMaxWidth()) {
                Text("Спросить ИИ-помощника 🦉")
            }
        }
    }
}

@Composable
private fun EventCard(event: DayEvent) {
    Card {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(event.emoji, fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(event.title, fontWeight = FontWeight.Medium)
                Text(
                    "${ScheduleGenerator.format(event.startMinute)} – " +
                    ScheduleGenerator.format(event.endMinute),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TaskCard(task: KidTask, onDone: () -> Unit) {
    val isDone = task.status == TaskStatus.DONE
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(task.emoji, fontSize = 36.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                Text("+${task.points} ⭐", style = MaterialTheme.typography.bodySmall)
            }
            if (isDone) {
                Text("✅", fontSize = 28.sp)
            } else {
                Button(onClick = onDone) { Text("Готово!") }
            }
        }
    }
}

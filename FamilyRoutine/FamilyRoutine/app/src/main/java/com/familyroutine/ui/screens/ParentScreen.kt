package com.familyroutine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.familyroutine.FamilyViewModel
import com.familyroutine.data.ScheduleGenerator
import com.familyroutine.data.TaskStatus

// Экран родителя: задания, настройки распорядка, синхронизация, дайджест дня
@Composable
fun ParentScreen(vm: FamilyViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Родительский режим 👨‍👩‍👧",
                style = MaterialTheme.typography.headlineSmall
            )
            Text("${vm.child.name}, ${vm.child.age} лет")
        }

        item {
            Text("Добавить задание", style = MaterialTheme.typography.titleLarge)
        }
        item {
            var title by remember { mutableStateOf("") }
            var points by remember { mutableStateOf("2") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название задания") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = points,
                    onValueChange = { points = it },
                    label = { Text("Баллы за выполнение") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    if (title.isNotBlank()) {
                        vm.addTask(title, points.toIntOrNull() ?: 2)
                        title = ""
                    }
                }) { Text("Добавить задание") }
            }
        }

        item {
            Text("Синхронизация 🔗", style = MaterialTheme.typography.titleLarge)
            Text(vm.syncStatus, style = MaterialTheme.typography.bodyMedium)
        }
        item {
            var joinCode by remember { mutableStateOf("") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.createFamilyCode() }) {
                    Text("Создать семейный код")
                }
                if (vm.familyCode.isNotEmpty()) {
                    Text(
                        "Код семьи: ${vm.familyCode}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                OutlinedTextField(
                    value = joinCode,
                    onValueChange = { joinCode = it },
                    label = { Text("Или введите существующий код") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { vm.joinFamily(joinCode) }) {
                    Text("Подключиться по коду")
                }
            }
        }

        item {
            Text("Распорядок", style = MaterialTheme.typography.titleLarge)
            Text("Подъём: ${ScheduleGenerator.format(vm.wakeMinute)}")
            Slider(
                value = vm.wakeMinute.toFloat(),
                onValueChange = { vm.wakeMinute = it.toInt() },
                valueRange = 300f..600f // 05:00–10:00
            )
            Text("Отбой: ${ScheduleGenerator.format(vm.bedMinute)}")
            Slider(
                value = vm.bedMinute.toFloat(),
                onValueChange = { vm.bedMinute = it.toInt() },
                valueRange = 1110f..1380f // 18:30–23:00
            )
            Button(onClick = { vm.regenerateSchedule() }) {
                Text("Сгенерировать распорядок заново")
            }
        }

        item {
            Text("Дайджест за сегодня", style = MaterialTheme.typography.titleLarge)
            val done = vm.tasks.count { it.status == TaskStatus.DONE }
            Text("Выполнено заданий: $done из ${vm.tasks.size}")
            Text("Звёздочек у ребёнка: ${vm.child.stars} ⭐")
        }
    }
}

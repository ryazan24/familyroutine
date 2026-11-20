package com.familyroutine.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.familyroutine.FamilyViewModel
import com.familyroutine.data.AccountType
import com.familyroutine.data.ChatMessage

// Экран чата с ИИ-помощником: домашка, болтовня, напоминания
@Composable
fun AiChatScreen(vm: FamilyViewModel) {
    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(vm.aiMessages, key = { it.timestamp }) { message ->
                ChatBubble(message)
            }
        }

        // Для малышей (до 10 лет) — готовые фразы-кнопки вместо набора текста
        if (vm.child.age <= 9) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Помоги с математикой 🧮", "Мне скучно 😴", "Что мне сегодня делать?")
                    .forEach { phrase ->
                        SuggestionChip(onClick = { vm.askAi(phrase) }) { Text(phrase) }
                    }
            }
        }

        var input by remember { mutableStateOf("") }
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Напиши помощнику...") }
            )
            IconButton(onClick = {
                vm.askAi(input)
                input = ""
            }) { Text("➤", style = MaterialTheme.typography.titleLarge) }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isAi = message.author == AccountType.AI
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isAi) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth(if (isAi) 0.85f else 0.75f)
        ) {
            Text(
                message.text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

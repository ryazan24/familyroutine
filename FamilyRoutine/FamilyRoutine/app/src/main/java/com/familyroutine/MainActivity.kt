package com.familyroutine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.familyroutine.ui.screens.AiChatScreen
import com.familyroutine.ui.screens.ChildDayScreen
import com.familyroutine.ui.screens.ParentScreen
import com.familyroutine.ui.theme.FamilyRoutineTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyRoutineTheme {
                val vm: FamilyViewModel = viewModel()
                var screen by remember { mutableStateOf("day") }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = screen == "day",
                                onClick = { screen = "day" },
                                icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                                label = { Text("Мой день") }
                            )
                            NavigationBarItem(
                                selected = screen == "chat",
                                onClick = { screen = "chat" },
                                icon = { Icon(Icons.Default.Face, contentDescription = null) },
                                label = { Text("ИИ-помощник") }
                            )
                            NavigationBarItem(
                                selected = screen == "parent",
                                onClick = { screen = "parent" },
                                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                                label = { Text("Родитель") }
                            )
                        }
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        when (screen) {
                            "day" -> ChildDayScreen(vm, onOpenChat = { screen = "chat" })
                            "chat" -> AiChatScreen(vm)
                            "parent" -> ParentScreen(vm)
                        }
                    }
                }
            }
        }
    }
}

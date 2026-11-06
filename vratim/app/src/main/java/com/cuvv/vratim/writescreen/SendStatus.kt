package com.cuvv.vratim.writescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cuvv.vratim.Config

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendStatus(
    config: Config,
    initialEntityId: Int,
    langMap: Map<String, String>,
    onBackClick: () -> Unit,
    onSendClick: (entityId: Int, status: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val textPrimary = Color(0xFFE2E8F0)
    val textSecondary = Color(0xFF94A3B8)
    val cardBackground = Color(0xFF161A1D)

    var selectedEntityId by remember(initialEntityId) { mutableStateOf(initialEntityId) }

    val currentEntity = remember(selectedEntityId, config.entities) {
        config.entities.firstOrNull { it.getActiveId(config.entities.indexOf(it)) == selectedEntityId }
    }

    var isEntityDropdownExpanded by remember { mutableStateOf(false) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

    // Локальное состояние выбранного числового u8 статуса
    var selectedStatusValue by remember(selectedEntityId) {
        mutableStateOf(currentEntity?.texts?.firstOrNull()?.status ?: 0)
    }

    // Находим строковое представление для текущего выбранного статуса
    val currentStatusText = remember(selectedStatusValue, currentEntity) {
        currentEntity?.texts?.firstOrNull { it.status == selectedStatusValue }?.text ?: "$selectedStatusValue"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackground, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = langMap["menu_send_status"] ?: "Отправить статус",
                style = MaterialTheme.typography.titleMedium,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (initialEntityId == -1) {
                ExposedDropdownMenuBox(
                    expanded = isEntityDropdownExpanded,
                    onExpandedChange = { isEntityDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = currentEntity?.name ?: "Выберите сущность",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Сущность") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isEntityDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textPrimary, unfocusedTextColor = textPrimary)
                    )
                    ExposedDropdownMenu(
                        expanded = isEntityDropdownExpanded,
                        onDismissRequest = { isEntityDropdownExpanded = false }
                    ) {
                        val entities = config.entities
                        for (i in entities.indices) {
                            val item = entities[i]
                            val itemId = item.getActiveId(i)
                            DropdownMenuItem(
                                text = { Text(item.name) },
                                onClick = {
                                    selectedEntityId = itemId
                                    isEntityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Text(
                    text = "${langMap["entity_label"] ?: "Цель"}: ${currentEntity?.name ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            ExposedDropdownMenuBox(
                expanded = isStatusDropdownExpanded,
                onExpandedChange = { isStatusDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = currentStatusText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Статус (u8)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textPrimary, unfocusedTextColor = textPrimary)
                )
                ExposedDropdownMenu(
                    expanded = isStatusDropdownExpanded,
                    onDismissRequest = { isStatusDropdownExpanded = false }
                ) {
                    val mappings = currentEntity?.texts ?: emptyList()
                    if (mappings.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Значение: 0") },
                            onClick = {
                                selectedStatusValue = 0
                                isStatusDropdownExpanded = false
                            }
                        )
                    } else {
                        for (i in mappings.indices) {
                            val mapping = mappings[i]
                            DropdownMenuItem(
                                text = { Text(mapping.text) },
                                onClick = {
                                    selectedStatusValue = mapping.status
                                    isStatusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (selectedEntityId != -1) {
                        onSendClick(selectedEntityId, selectedStatusValue)
                    }
                },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2225))
            ) {
                Text(text = langMap["btn_send"] ?: "Send", color = textPrimary)
            }
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2225))
        ) {
            Text(
                text = langMap["btn_back"] ?: "Back",
                color = textPrimary,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

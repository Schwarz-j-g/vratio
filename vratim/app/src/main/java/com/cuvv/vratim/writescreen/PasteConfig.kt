package com.cuvv.vratim.writescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun PasteConfig(
    langMap: Map<String, String>,
    actionLog: String,
    onBackClick: () -> Unit,
    onSaveClick: (rawJson: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var jsonInputText by remember { mutableStateOf("") }

    val textPrimary = Color(0xFFE2E8F0)
    val textSecondary = Color(0xFF94A3B8)
    val cardBackground = Color(0xFF161A1D)
    val borderColor = Color(0xFF334155)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardBackground, RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = langMap["import_title"] ?: "Import configuration",
                    style = MaterialTheme.typography.titleSmall,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    // Если actionLog пуст, выводим дефолтный статус ожидания
                    text = actionLog.ifEmpty { langMap["status_idle"] ?: "Waiting for input..." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (actionLog.contains("Error")) Color(0xFFEF4444) else textSecondary
                )
            }

            OutlinedTextField(
                value = jsonInputText,
                onValueChange = { jsonInputText = it },
                placeholder = {
                    Text(
                        text = langMap["import_placeholder"] ?: "Paste JSON text here...",
                        color = Color(0xFF475569)
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    focusedContainerColor = cardBackground,
                    unfocusedContainerColor = cardBackground,
                    focusedBorderColor = textSecondary,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2225))
            ) {
                Text(text = langMap["btn_back"] ?: "Back", color = textPrimary)
            }

            Button(
                onClick = {
                    if (jsonInputText.isNotBlank()) {
                        onSaveClick(jsonInputText)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2225))
            ) {
                Text(text = langMap["btn_save"] ?: "Save", color = textPrimary)
            }
        }
    }
}
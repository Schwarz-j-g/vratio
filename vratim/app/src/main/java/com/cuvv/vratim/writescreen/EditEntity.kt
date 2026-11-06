package com.cuvv.vratim.writescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cuvv.vratim.Config
import com.cuvv.vratim.Text

@Composable
fun EditEntity(
    config: Config,
    editingEntityId: Int,
    langMap: Map<String, String>,
    onBackClick: () -> Unit,
    onSaveClick: (entityId: Int, updatedMappings: List<Text>) -> Unit,
    modifier: Modifier = Modifier
) {
    val textPrimary = Color(0xFFE2E8F0)
    val cardBackground = Color(0xFF161A1D)

    val entity = remember(editingEntityId, config.entities) {
        config.entities.firstOrNull { it.getActiveId(config.entities.indexOf(it)) == editingEntityId }
    }

    if (entity == null) {
        onBackClick()
        return
    }

    val mappingsList = remember(entity) { entity.texts.toMutableStateList() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(cardBackground, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = entity.name,
                style = MaterialTheme.typography.titleMedium,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (index in mappingsList.indices) {
                            MappingInputRow(
                                mapping = mappingsList[index],
                                onMappingChanged = { updatedMapping ->
                                    mappingsList[index] = updatedMapping
                                }
                            )
                        }
                    }
                }
            }
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
                    onSaveClick(editingEntityId, mappingsList.toList())
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

@Composable
private fun MappingInputRow(
    mapping: Text,
    onMappingChanged: (Text) -> Unit
) {
    val textPrimary = Color(0xFFE2E8F0)
    val borderColor = Color(0xFF334155)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = mapping.status.toString(),
            onValueChange = { newValue ->
                val intVal = newValue.toIntOrNull() ?: 0
                if (intVal in 0..255) {
                    onMappingChanged(mapping.copy(status = intVal))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .weight(0.25f)
                .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        OutlinedTextField(
            value = mapping.text,
            onValueChange = { newText ->
                onMappingChanged(mapping.copy(text = newText))
            },
            singleLine = true,
            modifier = Modifier
                .weight(0.75f)
                .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}

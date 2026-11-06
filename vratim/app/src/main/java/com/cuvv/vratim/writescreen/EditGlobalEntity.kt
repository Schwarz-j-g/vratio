package com.cuvv.vratim.writescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun EditGlobalEntity(
    config: Config,
    langMap: Map<String, String>,
    onBackClick: () -> Unit,
    onSaveClick: (url: String, get: String, post: String, lang: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val textPrimary = Color(0xFFE2E8F0)
    val cardBackground = Color(0xFF161A1D)

    var urlState by remember { mutableStateOf(config.url) }
    var getState by remember { mutableStateOf(config.getPrefix) }
    var postState by remember { mutableStateOf(config.postPrefix) }
    var langState by remember { mutableStateOf(config.currentLang) }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = langMap["menu_global"] ?: "Global Settings",
                style = MaterialTheme.typography.titleMedium,
                color = textPrimary
            )

            GlobalInputRow(label = "URL", value = urlState, keyboardType = KeyboardType.Uri) { urlState = it }
            GlobalInputRow(label = "GET", value = getState, keyboardType = KeyboardType.Text) { getState = it }
            GlobalInputRow(label = "POST", value = postState, keyboardType = KeyboardType.Text) { postState = it }
            GlobalInputRow(label = "LANG", value = langState, keyboardType = KeyboardType.Text) { langState = it }
        }

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
                    onSaveClick(urlState, getState, postState, langState)
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
private fun GlobalInputRow(
    label: String,
    value: String,
    keyboardType: KeyboardType,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.2f),
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            modifier = Modifier.weight(0.8f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFFE2E8F0),
                unfocusedTextColor = Color(0xFFE2E8F0),
                focusedBorderColor = Color(0xFF94A3B8),
                unfocusedBorderColor = Color(0xFF334155),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}

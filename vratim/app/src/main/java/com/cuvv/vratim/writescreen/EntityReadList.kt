package com.cuvv.vratim.writescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cuvv.vratim.Config

@Composable
fun EntityReadList(
    config: Config,
    serverStatuses: ByteArray,
    onEntityClick: (entityId: Int) -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entities = config.entities

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                count = entities.size,
                key = { index -> index }
            ) { index ->
                val entity = entities[index]
                val activeId = entity.getActiveId(index)
                val rawStatus = serverStatuses[activeId].toInt() and 0xFF

                var currentStatusText = ""
                val mappings = entity.texts
                for (i in mappings.indices) {
                    if (mappings[i].status == rawStatus) {
                        currentStatusText = mappings[i].text
                        break
                    }
                }

                if (currentStatusText.isEmpty()) {
                    currentStatusText = "$rawStatus"
                }

                ReadEntityCard(
                    name = entity.name,
                    statusText = currentStatusText,
                    onClick = { onEntityClick(activeId) }
                )
            }
        }

        IconButton(
            onClick = onRefreshClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(56.dp)
                .background(Color(0xFF1E2225), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color(0xFFE2E8F0),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ReadEntityCard(
    name: String,
    statusText: String,
    onClick: () -> Unit
) {
    val cardBackground = Color(0xFF161A1D)
    val textPrimary = Color(0xFFE2E8F0)
    val textSecondary = Color(0xFF94A3B8)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, style = MaterialTheme.typography.titleMedium, color = textPrimary)
            Text(text = statusText, style = MaterialTheme.typography.bodyMedium, color = textSecondary)
        }
    }
}

package com.rusdili.pro.feature.dictionary

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rusdili.pro.core.common.SearchHistoryEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun RecentSearchChipsRow(
    history: List<SearchHistoryEntry>,
    onSelectEntry: (SearchHistoryEntry) -> Unit,
    onRemoveEntry: (String) -> Unit,
    onClearAll: () -> Unit
) {
    if (history.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Son baxılanlar",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(
                onClick = onClearAll,
                modifier = Modifier.testTag("dict_recent_clear_button")
            ) {
                Text("Təmizlə")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            history.take(10).forEach { entry ->
                InputChip(
                    selected = false,
                    onClick = { onSelectEntry(entry) },
                    label = {
                        Text(
                            text = entry.matchedRussian ?: entry.query,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { onRemoveEntry(entry.queryKey) },
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("dict_chip_remove_${entry.queryKey}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tarixçədən sil: ${entry.query}",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    },
                    modifier = Modifier.testTag("dict_recent_chip_${entry.queryKey}")
                )
            }
        }
    }
}

@Composable
internal fun SearchHistorySection(
    history: List<SearchHistoryEntry>,
    onSelectEntry: (SearchHistoryEntry) -> Unit,
    onSpeak: (String) -> Unit,
    onRemoveEntry: (String) -> Unit,
    onClearAll: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (history.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dict_history_empty_state"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Axtarış tarixçəsi boşdur",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Lüğətdə axtardığın və ya baxdığın sözlər burada avtomatik yadda saxlanacaq.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Son axtarılan sözlər (${history.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedButton(
                    onClick = onClearAll,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("history_clear_all_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Hamısını sil")
                }
            }

            history.forEach { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_item_${entry.queryKey}"),
                    onClick = { onSelectEntry(entry) },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (entry.matchedRussian != null) Icons.Default.History else Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.matchedRussian ?: entry.query,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (!entry.matchedTranslation.isNullOrBlank()) {
                                Text(
                                    text = entry.matchedTranslation!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (entry.searchedAt > 0L) {
                                Text(
                                    text = formatter.format(Date(entry.searchedAt)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        entry.matchedRussian?.let { russian ->
                            IconButton(
                                onClick = { onSpeak(russian) },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("history_speak_${entry.queryKey}")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Sözü səsləndir: $russian"
                                )
                            }
                        }
                        IconButton(
                            onClick = { onRemoveEntry(entry.queryKey) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("history_delete_${entry.queryKey}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tarixçədən sil: ${entry.query}"
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.rusdili.pro.feature.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

private val REMINDER_PRESET_HOURS = listOf(9, 13, 19, 21)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DailyReminderCard(
    enabled: Boolean,
    hour: Int,
    minute: Int,
    onToggleReminder: (Boolean, Int, Int) -> Unit,
    onSelectTime: (Int, Int) -> Unit,
    onTestNotification: () -> Unit
) {
    val context = LocalContext.current
    var permissionDeniedNote by remember { mutableStateOf(false) }
    var testSentMessage by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            permissionDeniedNote = false
            onToggleReminder(true, hour, minute)
        } else {
            permissionDeniedNote = true
            onToggleReminder(false, hour, minute)
        }
    }

    val nextDayLabel = remember(hour, minute, enabled) {
        if (!enabled) "" else computeNextDayLabel(hour, minute)
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("home_reminder_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (enabled) {
                            Icons.Default.NotificationsActive
                        } else {
                            Icons.Default.NotificationsNone
                        },
                        contentDescription = "Gündəlik təkrar xatırlatması",
                        tint = if (enabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Gündəlik təkrar xatırlatması",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (enabled) {
                                "Hər gün saat %02d:%02d-da ($nextDayLabel)".format(hour, minute)
                            } else {
                                "Təkrar sessiyalarını unutmamaq üçün vaxt təyin et"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = enabled,
                    onCheckedChange = { shouldEnable ->
                        testSentMessage = false
                        if (!shouldEnable) {
                            permissionDeniedNote = false
                            onToggleReminder(false, hour, minute)
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            permissionDeniedNote = !NotificationManagerCompat.from(context).areNotificationsEnabled()
                            onToggleReminder(true, hour, minute)
                        }
                    },
                    modifier = Modifier.testTag("reminder_toggle_switch")
                )
            }

            if (permissionDeniedNote) {
                Text(
                    text = "Bildiriş icazəsi aktiv deyil. Xatırlatmalar üçün sistem ayarlarından bildirişlərə icazə verin.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("reminder_permission_warning")
                )
            }

            AnimatedVisibility(visible = enabled) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Xatırlatma saatı",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        AssistChip(
                            onClick = { showTimePickerDialog = true },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Xüsusi vaxt seç",
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text("%02d:%02d".format(hour, minute)) },
                            modifier = Modifier.testTag("reminder_custom_time_button")
                        )
                        REMINDER_PRESET_HOURS.forEach { presetHour ->
                            FilterChip(
                                selected = hour == presetHour && minute == 0,
                                onClick = {
                                    testSentMessage = false
                                    onSelectTime(presetHour, 0)
                                },
                                label = { Text("%02d:00".format(presetHour)) },
                                modifier = Modifier.testTag("reminder_hour_$presetHour")
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                val runtimeGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                val systemEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
                                if (!runtimeGranted) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else if (!systemEnabled) {
                                    permissionDeniedNote = true
                                } else {
                                    permissionDeniedNote = false
                                    onTestNotification()
                                    testSentMessage = true
                                }
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("test_reminder_button")
                        ) {
                            Text("Test bildirişi göndər")
                        }

                        if (testSentMessage) {
                            Text(
                                text = "Bildiriş göndərildi",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = hour.coerceIn(0, 23),
            initialMinute = minute.coerceIn(0, 59),
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            title = {
                Text(
                    text = "Xatırlatma vaxtını seç",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.testTag("reminder_time_picker")
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        testSentMessage = false
                        onSelectTime(timePickerState.hour, timePickerState.minute)
                        showTimePickerDialog = false
                    },
                    modifier = Modifier.testTag("reminder_time_confirm_button")
                ) {
                    Text("Təsdiq et")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePickerDialog = false },
                    modifier = Modifier.testTag("reminder_time_cancel_button")
                ) {
                    Text("Ləğv et")
                }
            }
        )
    }
}

private fun computeNextDayLabel(hour: Int, minute: Int): String {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
        set(Calendar.MINUTE, minute.coerceIn(0, 59))
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return if (target.timeInMillis > now.timeInMillis) "növbəti: bu gün" else "növbəti: sabah"
}

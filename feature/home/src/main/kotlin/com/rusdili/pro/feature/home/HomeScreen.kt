package com.rusdili.pro.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rusdili.pro.core.common.AppConstants
import com.rusdili.pro.core.common.DayActivity
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.ui.AppButton
import com.rusdili.pro.core.ui.NewsCard
import com.rusdili.pro.core.ui.ProgressBar

@Composable
fun HomeScreen(
    onBook: () -> Unit,
    onPractice: () -> Unit,
    onNews: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.refreshStreak()
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val current = state) {
        UiState.Loading -> CircularProgressIndicator(Modifier.padding(32.dp))
        is UiState.Error -> Text(current.message, Modifier.padding(24.dp))
        is UiState.Success -> HomeContent(
            data = current.data,
            onCheckIn = viewModel::recordDailyActivity,
            onToggleReminder = viewModel::setDailyReminder,
            onSelectReminderTime = viewModel::updateReminderTime,
            onTestReminder = viewModel::sendTestReminderNotification,
            onBook = onBook,
            onPractice = onPractice,
            onNews = onNews
        )
    }
}

@Composable
private fun HomeContent(
    data: HomeData,
    onCheckIn: () -> Unit,
    onToggleReminder: (Boolean, Int, Int) -> Unit,
    onSelectReminderTime: (Int, Int) -> Unit,
    onTestReminder: () -> Unit,
    onBook: () -> Unit,
    onPractice: () -> Unit,
    onNews: () -> Unit
) {
    var showAchievementsSheet by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Gündəlik icmal",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Bugünkü rus dili hədəfin və nailiyyətlərin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = if (data.isTodayActive) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = Modifier.testTag("home_streak_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Ardıcıl günlər",
                        tint = if (data.isTodayActive) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${data.streak}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (data.isTodayActive) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }

        DailyStreakCard(
            streak = data.streak,
            bestStreak = data.bestStreak,
            totalActiveDays = data.totalActiveDays,
            isTodayActive = data.isTodayActive,
            weeklyActivity = data.weeklyActivity,
            onCheckIn = onCheckIn
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.testTag("home_goal_card")
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Bugünkü hədəf", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                ProgressBar(data.xpToday / AppConstants.DAILY_GOAL_XP.toFloat())
                Text("Bu gün ${data.xpToday} / ${AppConstants.DAILY_GOAL_XP} XP", Modifier.padding(top = 8.dp))
            }
        }

        if (data.weeklyActivity.isNotEmpty()) {
            WeeklyActivityChartCard(
                weeklyActivity = data.weeklyActivity,
                weeklyTotalXp = data.weeklyTotalXp
            )
        }

        if (data.achievements.isNotEmpty()) {
            AchievementsShowcaseCard(
                achievements = data.achievements,
                onOpenAll = { showAchievementsSheet = true }
            )
        }

        DailyReminderCard(
            enabled = data.reminderEnabled,
            hour = data.reminderHour,
            minute = data.reminderMinute,
            onToggleReminder = onToggleReminder,
            onSelectTime = onSelectReminderTime,
            onTestNotification = onTestReminder
        )
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Davam et", style = MaterialTheme.typography.titleMedium)
                Text(data.nextLesson)
                AppButton("Davam et", onBook, Modifier.testTag("continue_lesson_button"))
            }
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Günün sözü", style = MaterialTheme.typography.titleMedium)
                Text(data.wordRussian, style = MaterialTheme.typography.headlineSmall)
                Text(data.wordTranslation)
                Text(data.example, style = MaterialTheme.typography.bodyLarge)
            }
        }
        Text("Son xəbər", style = MaterialTheme.typography.titleLarge)
        if (data.latestNews != null) NewsCard(data.latestNews)
        else Text("Hələlik yeni xəbər yoxdur")
        AppButton("Hamısını gör", onNews, Modifier.testTag("all_news_button"))
        AppButton(
            "Gündəlik məşqə başla",
            onPractice,
            Modifier.padding(bottom = 12.dp).testTag("start_practice_button")
        )
    }

    if (showAchievementsSheet) {
        AchievementsBottomSheet(
            achievements = data.achievements,
            totalQuestionsCompleted = data.totalQuestionsCompleted,
            totalLessonsCompleted = data.totalLessonsCompleted,
            totalXpEarned = data.totalXpEarned,
            onDismiss = { showAchievementsSheet = false }
        )
    }
}

@Composable
private fun DailyStreakCard(
    streak: Int,
    bestStreak: Int,
    totalActiveDays: Int,
    isTodayActive: Boolean,
    weeklyActivity: List<DayActivity>,
    onCheckIn: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("home_streak_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isTodayActive) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Gündəlik seriya",
                            tint = if (isTodayActive) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (streak > 0) "$streak günlük seriya" else "Seriyaya başla",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isTodayActive) {
                                "Bugünkü fəaliyyət qeydə alınıb"
                            } else {
                                "Seriyanı qorumaq üçün bu gün aktiv ol"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isTodayActive) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isTodayActive) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(
                            text = if (isTodayActive) "Aktiv" else "Gözləyir",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isTodayActive) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }

            if (weeklyActivity.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weeklyActivity.forEach { day ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = day.dayShortLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (day.isToday) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            val circleModifier = when {
                                day.isCompleted -> Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (day.isToday) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.primaryContainer
                                        }
                                    )
                                day.isToday -> Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                                    .background(MaterialTheme.colorScheme.surface)
                                else -> Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            }
                            Box(
                                modifier = circleModifier,
                                contentAlignment = Alignment.Center
                            ) {
                                if (day.isCompleted) {
                                    Icon(
                                        imageVector = if (day.isToday) {
                                            Icons.Default.LocalFireDepartment
                                        } else {
                                            Icons.Default.Check
                                        },
                                        contentDescription = "${day.dayShortLabel} aktiv",
                                        tint = if (day.isToday) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Ən yaxşı seriya",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Ən yaxşı: $bestStreak gün",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Ümumi aktiv günlər",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Ümumi: $totalActiveDays gün",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            AnimatedVisibility(visible = !isTodayActive) {
                Button(
                    onClick = onCheckIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("daily_checkin_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Bu günü qeyd et")
                }
            }
        }
    }
}

@Composable
private fun WeeklyActivityChartCard(
    weeklyActivity: List<DayActivity>,
    weeklyTotalXp: Int
) {
    val todayIndex = remember(weeklyActivity) {
        weeklyActivity.indexOfFirst { it.isToday }.takeIf { it >= 0 } ?: 0
    }
    var selectedIndex by remember(weeklyActivity.size) { mutableIntStateOf(todayIndex) }
    val safeIndex = selectedIndex.coerceIn(0, (weeklyActivity.size - 1).coerceAtLeast(0))
    val selectedDay = weeklyActivity.getOrNull(safeIndex)
    val activeDaysCount = weeklyActivity.count { it.isCompleted }
    val maxChartXp = maxOf(
        AppConstants.DAILY_GOAL_XP,
        weeklyActivity.maxOfOrNull { it.xpEarned } ?: 0
    ).coerceAtLeast(AppConstants.DAILY_GOAL_XP)

    val primaryColor = MaterialTheme.colorScheme.primary
    val primarySoftColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val inactiveColor = MaterialTheme.colorScheme.surfaceVariant
    val goalLineColor = MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = Modifier.fillMaxWidth().testTag("home_weekly_chart_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Həftəlik statistika qrafiki",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Həftəlik fəaliyyət",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "$weeklyTotalXp XP · $activeDaysCount/7 gün",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            selectedDay?.let { day ->
                val dayStatus = when {
                    day.xpEarned >= AppConstants.DAILY_GOAL_XP -> "Günlük hədəf tamamlandı"
                    day.xpEarned > 0 -> "Aktiv öyrənmə"
                    day.isCompleted -> "Günlük qeydiyyat aktiv"
                    else -> "Fəaliyyət yoxdur"
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (day.isToday) "Bu gün (${day.dayShortLabel})" else day.dayShortLabel,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${day.xpEarned} XP · $dayStatus",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            val animatedFractions = weeklyActivity.map { day ->
                val target = when {
                    day.xpEarned > 0 -> (day.xpEarned / maxChartXp.toFloat()).coerceIn(0.08f, 1f)
                    day.isCompleted -> 0.14f
                    else -> 0.04f
                }
                animateFloatAsState(
                    targetValue = target,
                    animationSpec = tween(durationMillis = 350),
                    label = "weekly_bar_${day.date}"
                ).value
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(top = 8.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("weekly_activity_canvas")
                ) {
                    val count = weeklyActivity.size.coerceAtLeast(1)
                    val slotWidth = size.width / count
                    val barWidth = (slotWidth * 0.44f).coerceAtMost(32.dp.toPx())
                    val bottomPadding = 2.dp.toPx()
                    val chartHeight = (size.height - bottomPadding).coerceAtLeast(1f)

                    val goalFraction = (AppConstants.DAILY_GOAL_XP / maxChartXp.toFloat()).coerceIn(0f, 1f)
                    val goalY = chartHeight * (1f - goalFraction)

                    drawLine(
                        color = goalLineColor,
                        start = Offset(0f, goalY),
                        end = Offset(size.width, goalY),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )

                    drawLine(
                        color = goalLineColor,
                        start = Offset(0f, chartHeight),
                        end = Offset(size.width, chartHeight),
                        strokeWidth = 1.dp.toPx()
                    )

                    weeklyActivity.forEachIndexed { index, day ->
                        val fraction = animatedFractions.getOrElse(index) { 0.04f }
                        val barHeight = (chartHeight * fraction).coerceAtLeast(4.dp.toPx())
                        val left = slotWidth * index + (slotWidth - barWidth) / 2f
                        val top = chartHeight - barHeight
                        val isSelected = index == safeIndex

                        val barColor = when {
                            day.xpEarned >= AppConstants.DAILY_GOAL_XP -> primaryColor
                            day.xpEarned > 0 || day.isCompleted -> if (isSelected) tertiaryColor else primarySoftColor
                            else -> inactiveColor
                        }

                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weeklyActivity.forEachIndexed { index, day ->
                    val isSelected = index == safeIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { selectedIndex = index }
                            .testTag("chart_day_${index}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.dayShortLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected || day.isToday) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }

            Text(
                text = "Qırıq xətt: günlük ${AppConstants.DAILY_GOAL_XP} XP hədəfi",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

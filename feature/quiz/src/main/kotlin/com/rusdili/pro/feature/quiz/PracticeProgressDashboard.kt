package com.rusdili.pro.feature.quiz

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rusdili.pro.core.common.ReviewSession
import com.rusdili.pro.core.common.ReviewSessionType
import com.rusdili.pro.core.ui.ProgressBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private data class DailyProgressPoint(
    val dateKey: String,
    val label: String,
    val isToday: Boolean,
    val successfulItems: Int,
    val totalItems: Int,
    val successfulSessions: Int,
    val xpEarned: Int
) {
    val accuracyPercent: Int
        get() = if (totalItems > 0) ((successfulItems * 100f) / totalItems).toInt() else 0
}

@Composable
internal fun PracticeProgressDashboard(
    data: PracticeData,
    onSelectRange: (ProgressRange) -> Unit,
    onSelectMetric: (ProgressMetric) -> Unit,
    onStartDaily: () -> Unit,
    onStartFlashcards: () -> Unit
) {
    val sessions = data.reviewSessions
    val totalSuccessfulItems = sessions.sumOf { it.successfulItems }
    val totalAttemptedItems = sessions.sumOf { it.totalItems }
    val overallAccuracy = if (totalAttemptedItems > 0) {
        ((totalSuccessfulItems * 100f) / totalAttemptedItems).toInt()
    } else {
        0
    }
    val successfulSessionsCount = sessions.count { it.isSuccessful }
    val totalSessionXp = sessions.sumOf { it.xpEarned }

    val timelinePoints = remember(sessions, data.progressRange) {
        buildTimelinePoints(sessions, data.progressRange)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_progress_dashboard"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DashboardKpiCard(
                title = "Uğurlu təkrar",
                value = "$totalSuccessfulItems",
                subtitle = "$overallAccuracy% dəqiqlik",
                icon = Icons.Default.CheckCircle,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_successful_reviews")
            )
            DashboardKpiCard(
                title = "Sessiyalar",
                value = "$successfulSessionsCount / ${sessions.size}",
                subtitle = "$totalSessionXp XP qazanıldı",
                icon = Icons.Default.EmojiEvents,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_sessions")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DashboardKpiCard(
                title = "Öyrənilən söz",
                value = "${data.reviewedWordsCount} / ${data.allWordsCount}",
                subtitle = "${data.completedChapterIds.size} fəsil keçilib",
                icon = Icons.Default.Style,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_words_mastery")
            )
            DashboardKpiCard(
                title = "Aktiv seriya",
                value = "${data.currentStreak} gün",
                subtitle = "Davamlı məşq",
                icon = Icons.Default.School,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_streak")
            )
        }

        ProgressTimelineChartCard(
            points = timelinePoints,
            hasSessions = sessions.isNotEmpty(),
            range = data.progressRange,
            metric = data.progressMetric,
            onSelectRange = onSelectRange,
            onSelectMetric = onSelectMetric,
            onStartDaily = onStartDaily,
            onStartFlashcards = onStartFlashcards
        )

        if (sessions.isNotEmpty()) {
            SessionTypeBreakdownCard(sessions = sessions)
            RecentSessionsCard(sessions = sessions.take(10))
        }
    }
}

@Composable
private fun DashboardKpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProgressTimelineChartCard(
    points: List<DailyProgressPoint>,
    hasSessions: Boolean,
    range: ProgressRange,
    metric: ProgressMetric,
    onSelectRange: (ProgressRange) -> Unit,
    onSelectMetric: (ProgressMetric) -> Unit,
    onStartDaily: () -> Unit,
    onStartFlashcards: () -> Unit
) {
    val defaultSelection = remember(points) {
        val lastActive = points.indexOfLast { it.totalItems > 0 }
        if (lastActive >= 0) lastActive else (points.lastIndex).coerceAtLeast(0)
    }
    var selectedIndex by remember(points.size, range) { mutableIntStateOf(defaultSelection) }
    val safeIndex = selectedIndex.coerceIn(0, (points.size - 1).coerceAtLeast(0))
    val selectedPoint = points.getOrNull(safeIndex)

    val lineColor = if (metric == ProgressMetric.SUCCESSFUL_REVIEWS) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val pointCenterColor = MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("progress_chart_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = "Zamanla öyrənmə qrafiki",
                        tint = lineColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Zamanla öyrənmə dinamikası",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProgressRange.entries.forEach { entry ->
                    FilterChip(
                        selected = range == entry,
                        onClick = { onSelectRange(entry) },
                        label = { Text(entry.label) },
                        modifier = Modifier.testTag("progress_range_${entry.name.lowercase()}")
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProgressMetric.entries.forEach { entry ->
                    FilterChip(
                        selected = metric == entry,
                        onClick = { onSelectMetric(entry) },
                        label = { Text(entry.label) },
                        modifier = Modifier.testTag("progress_metric_${entry.name.lowercase()}")
                    )
                }
            }

            if (!hasSessions) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("progress_chart_empty_state")
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Hələ təkrar sessiyası qeydə alınmayıb",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Gündəlik məşq suallarını cavabladıqda və ya Kartlar bölməsində sözləri təkrar etdikdə zamanla inkişaf qrafikin burada əks olunacaq.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onStartDaily,
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Text("Məşqə başla")
                            }
                            OutlinedButton(
                                onClick = onStartFlashcards,
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Text("Kartlar")
                            }
                        }
                    }
                }
            } else {
                selectedPoint?.let { point ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("progress_selected_day_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (point.isToday) "Bu gün (${point.label})" else point.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${point.successfulSessions} uğurlu sessiya · +${point.xpEarned} XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${point.successfulItems}/${point.totalItems} (${point.accuracyPercent}%)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = lineColor
                            )
                        }
                    }
                }

                val values = points.map { point ->
                    when (metric) {
                        ProgressMetric.SUCCESSFUL_REVIEWS -> point.successfulItems.toFloat()
                        ProgressMetric.ACCURACY -> point.accuracyPercent.toFloat()
                    }
                }
                val maxY = when (metric) {
                    ProgressMetric.SUCCESSFUL_REVIEWS -> (values.maxOrNull() ?: 5f).coerceAtLeast(5f)
                    ProgressMetric.ACCURACY -> 100f
                }

                val animatedRatios = values.mapIndexed { index, value ->
                    animateFloatAsState(
                        targetValue = (value / maxY).coerceIn(0f, 1f),
                        animationSpec = tween(durationMillis = 350),
                        label = "chart_point_$index"
                    ).value
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .padding(top = 6.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(points.size) {
                                detectTapGestures { offset ->
                                    if (points.isNotEmpty() && size.width > 0) {
                                        val step = size.width / points.size.toFloat()
                                        val tapped = (offset.x / step).toInt()
                                            .coerceIn(0, points.lastIndex)
                                        selectedIndex = tapped
                                    }
                                }
                            }
                            .testTag("progress_timeline_canvas")
                    ) {
                        val horizontalPadding = 12.dp.toPx()
                        val topPadding = 12.dp.toPx()
                        val bottomPadding = 12.dp.toPx()
                        val usableWidth = (size.width - horizontalPadding * 2).coerceAtLeast(1f)
                        val usableHeight = (size.height - topPadding - bottomPadding).coerceAtLeast(1f)

                        // Horizontal dashed reference lines (0%, 50%, 100%)
                        listOf(0f, 0.5f, 1f).forEach { fraction ->
                            val y = topPadding + usableHeight * (1f - fraction)
                            drawLine(
                                color = gridColor,
                                start = Offset(horizontalPadding, y),
                                end = Offset(size.width - horizontalPadding, y),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                            )
                        }

                        if (animatedRatios.isNotEmpty()) {
                            val coords = animatedRatios.mapIndexed { idx, ratio ->
                                val x = if (animatedRatios.size == 1) {
                                    horizontalPadding + usableWidth / 2f
                                } else {
                                    horizontalPadding + usableWidth * (idx / (animatedRatios.size - 1).toFloat())
                                }
                                val y = topPadding + usableHeight * (1f - ratio)
                                Offset(x, y)
                            }

                            val linePath = Path().apply {
                                moveTo(coords.first().x, coords.first().y)
                                for (i in 1 until coords.size) {
                                    val prev = coords[i - 1]
                                    val curr = coords[i]
                                    val midX = (prev.x + curr.x) / 2f
                                    cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                                }
                            }

                            val areaPath = Path().apply {
                                addPath(linePath)
                                lineTo(coords.last().x, topPadding + usableHeight)
                                lineTo(coords.first().x, topPadding + usableHeight)
                                close()
                            }

                            drawPath(
                                path = areaPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        lineColor.copy(alpha = 0.30f),
                                        lineColor.copy(alpha = 0.02f)
                                    ),
                                    startY = topPadding,
                                    endY = topPadding + usableHeight
                                )
                            )

                            drawPath(
                                path = linePath,
                                color = lineColor,
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )

                            // Vertical guide for selected point
                            val selectedCoord = coords[safeIndex]
                            drawLine(
                                color = lineColor.copy(alpha = 0.35f),
                                start = Offset(selectedCoord.x, topPadding),
                                end = Offset(selectedCoord.x, topPadding + usableHeight),
                                strokeWidth = 1.5.dp.toPx()
                            )

                            coords.forEachIndexed { idx, pointOffset ->
                                val isSelected = idx == safeIndex
                                val outerRadius = if (isSelected) 7.dp.toPx() else 4.5.dp.toPx()
                                drawCircle(
                                    color = lineColor,
                                    radius = outerRadius,
                                    center = pointOffset
                                )
                                drawCircle(
                                    color = pointCenterColor,
                                    radius = if (isSelected) 3.5.dp.toPx() else 2.dp.toPx(),
                                    center = pointOffset
                                )
                            }
                        }
                    }
                }

                // X-axis date labels (spaced evenly; thinned if > 8 points so labels never collide)
                val labelStep = when {
                    points.size <= 7 -> 1
                    points.size <= 14 -> 2
                    else -> (points.size / 6).coerceAtLeast(2)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    points.forEachIndexed { idx, point ->
                        val showLabel = idx == 0 || idx == points.lastIndex || idx % labelStep == 0
                        Text(
                            text = if (showLabel) point.label else "",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (idx == safeIndex) FontWeight.Bold else FontWeight.Normal,
                            color = if (idx == safeIndex) lineColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionTypeBreakdownCard(sessions: List<ReviewSession>) {
    val quizSessions = sessions.filter { it.sessionType == ReviewSessionType.DAILY_QUIZ }
    val flashcardSessions = sessions.filter { it.sessionType == ReviewSessionType.FLASHCARD_REVIEW }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("progress_breakdown_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Sessiya növü üzrə nəticələr",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            BreakdownRow(
                label = "Gündəlik məşq",
                sessions = quizSessions
            )
            BreakdownRow(
                label = "Kart təkrarı",
                sessions = flashcardSessions
            )
        }
    }
}

@Composable
private fun BreakdownRow(label: String, sessions: List<ReviewSession>) {
    val success = sessions.sumOf { it.successfulItems }
    val total = sessions.sumOf { it.totalItems }
    val ratio = if (total > 0) success / total.toFloat() else 0f
    val percent = (ratio * 100).toInt()

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$label (${sessions.size} sessiya)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$success / $total ($percent%)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        ProgressBar(ratio)
    }
}

@Composable
private fun RecentSessionsCard(sessions: List<ReviewSession>) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("progress_recent_sessions_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Son təkrar sessiyaları",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            sessions.forEach { session ->
                val isQuiz = session.sessionType == ReviewSessionType.DAILY_QUIZ
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isQuiz) Icons.Default.School else Icons.Default.Style,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isQuiz) "Gündəlik məşq" else "Kart təkrarı",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${dateFormat.format(Date(session.completedAt))} · ${session.successfulItems}/${session.totalItems} uğurlu",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (session.isSuccessful) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Text(
                                text = "${session.accuracyPercent}% · +${session.xpEarned} XP",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (session.isSuccessful) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildTimelinePoints(
    sessions: List<ReviewSession>,
    range: ProgressRange
): List<DailyProgressPoint> {
    val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val labelFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val todayKey = keyFormat.format(Date())

    val groupedByDay = sessions.groupBy { keyFormat.format(Date(it.completedAt)) }

    val dateKeys: List<Pair<String, String>> = if (range.days != null) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, -(range.days - 1))
        }
        List(range.days) {
            val time = calendar.time
            val pair = keyFormat.format(time) to labelFormat.format(time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            pair
        }
    } else {
        val earliest = sessions.minOfOrNull { it.completedAt }
        if (earliest == null) {
            val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }
            List(7) {
                val time = calendar.time
                val pair = keyFormat.format(time) to labelFormat.format(time)
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                pair
            }
        } else {
            val startCal = Calendar.getInstance().apply {
                timeInMillis = earliest
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val endCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val result = mutableListOf<Pair<String, String>>()
            while (!startCal.after(endCal) && result.size < 60) {
                val time = startCal.time
                result.add(keyFormat.format(time) to labelFormat.format(time))
                startCal.add(Calendar.DAY_OF_YEAR, 1)
            }
            if (result.size < 7) {
                val padCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }
                List(7) {
                    val time = padCal.time
                    val pair = keyFormat.format(time) to labelFormat.format(time)
                    padCal.add(Calendar.DAY_OF_YEAR, 1)
                    pair
                }
            } else {
                result
            }
        }
    }

    return dateKeys.map { (dateKey, label) ->
        val daySessions = groupedByDay[dateKey].orEmpty()
        DailyProgressPoint(
            dateKey = dateKey,
            label = label,
            isToday = dateKey == todayKey,
            successfulItems = daySessions.sumOf { it.successfulItems },
            totalItems = daySessions.sumOf { it.totalItems },
            successfulSessions = daySessions.count { it.isSuccessful },
            xpEarned = daySessions.sumOf { it.xpEarned }
        )
    }
}

package com.rusdili.pro.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rusdili.pro.core.common.AchievementCategory
import com.rusdili.pro.core.common.AchievementProgress
import com.rusdili.pro.core.ui.ProgressBar

@Composable
internal fun AchievementsShowcaseCard(
    achievements: List<AchievementProgress>,
    onOpenAll: () -> Unit
) {
    val unlockedCount = achievements.count { it.isUnlocked }
    val ordered = remember(achievements) {
        achievements.sortedWith(
            compareByDescending<AchievementProgress> { it.isUnlocked }
                .thenByDescending { it.progressFraction }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("home_achievements_card"),
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
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Nailiyyətlər",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Nailiyyətlər",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (unlockedCount > 0) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = "$unlockedCount / ${achievements.size} nişan",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (unlockedCount > 0) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(ordered, key = { it.definition.id }) { item ->
                    BadgeMedallionTile(
                        achievement = item,
                        onClick = onOpenAll
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val nextMilestone = ordered.firstOrNull { !it.isUnlocked }
                Text(
                    text = if (nextMilestone != null) {
                        "Növbəti: ${nextMilestone.definition.titleAz} (${nextMilestone.clampedCurrent}/${nextMilestone.definition.targetValue})"
                    } else {
                        "Bütün rəqəmsal nişanlar qazanıldı!"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TextButton(
                    onClick = onOpenAll,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("view_all_achievements_button")
                ) {
                    Text("Hamısına bax")
                }
            }
        }
    }
}

@Composable
private fun BadgeMedallionTile(
    achievement: AchievementProgress,
    onClick: () -> Unit
) {
    val unlocked = achievement.isUnlocked
    Surface(
        modifier = Modifier
            .width(108.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("badge_tile_${achievement.definition.id}"),
        shape = RoundedCornerShape(16.dp),
        color = if (unlocked) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.background
        }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BadgeMedallionIcon(
                category = achievement.definition.category,
                unlocked = unlocked,
                badgeCode = achievement.definition.badgeCode
            )
            Text(
                text = achievement.definition.titleAz,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (unlocked) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (unlocked) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Text(
                text = if (unlocked) {
                    "Qazanılıb"
                } else {
                    "${achievement.clampedCurrent}/${achievement.definition.targetValue}"
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (unlocked) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun BadgeMedallionIcon(
    category: AchievementCategory,
    unlocked: Boolean,
    badgeCode: String
) {
    val icon = categoryIcon(category)
    val bgColor = if (unlocked) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val borderColor = if (unlocked) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val tint = if (unlocked) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier.size(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(width = if (unlocked) 2.5.dp else 1.dp, color = borderColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (unlocked) icon else Icons.Default.Lock,
                contentDescription = "$badgeCode nişanı",
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Surface(
            modifier = Modifier.align(Alignment.BottomEnd),
            shape = RoundedCornerShape(8.dp),
            color = if (unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        ) {
            Text(
                text = badgeCode,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AchievementsBottomSheet(
    achievements: List<AchievementProgress>,
    totalQuestionsCompleted: Int,
    totalLessonsCompleted: Int,
    totalXpEarned: Int,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<AchievementCategory?>(null) }
    val filtered = remember(achievements, selectedCategory) {
        val base = if (selectedCategory == null) {
            achievements
        } else {
            achievements.filter { it.definition.category == selectedCategory }
        }
        base.sortedWith(
            compareByDescending<AchievementProgress> { it.isUnlocked }
                .thenByDescending { it.progressFraction }
        )
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rəqəmsal nişanlar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${achievements.count { it.isUnlocked }} / ${achievements.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MilestoneStatChip(
                    label = "Sual",
                    value = "$totalQuestionsCompleted",
                    modifier = Modifier.weight(1f)
                )
                MilestoneStatChip(
                    label = "Dərs",
                    value = "$totalLessonsCompleted",
                    modifier = Modifier.weight(1f)
                )
                MilestoneStatChip(
                    label = "Ümumi XP",
                    value = "$totalXpEarned",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("Hamısı") }
                )
                AchievementCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                        },
                        label = { Text(category.labelAz) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(360.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.definition.id }) { item ->
                    AchievementDetailRow(item)
                }
            }
        }
    }
}

@Composable
private fun MilestoneStatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AchievementDetailRow(item: AchievementProgress) {
    val unlocked = item.isUnlocked
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("achievement_item_${item.definition.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgeMedallionIcon(
                category = item.definition.category,
                unlocked = unlocked,
                badgeCode = item.definition.badgeCode
            )
            Spacer(Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.definition.titleAz,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (unlocked) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Qazanılıb",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Qazanılıb",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Text(
                            text = "${item.clampedCurrent} / ${item.definition.targetValue}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = item.definition.descriptionAz,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ProgressBar(
                    progress = item.progressFraction,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun categoryIcon(category: AchievementCategory): ImageVector = when (category) {
    AchievementCategory.STREAK -> Icons.Default.LocalFireDepartment
    AchievementCategory.QUIZ -> Icons.Default.Quiz
    AchievementCategory.LESSONS -> Icons.Default.MenuBook
    AchievementCategory.XP -> Icons.Default.AutoAwesome
}

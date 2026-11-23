package com.rusdili.pro.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rusdili.pro.core.common.LearningLanguage
import com.rusdili.pro.core.common.NewsItem

@Composable
fun AppLogo(modifier: Modifier = Modifier, size: Dp = 36.dp) {
    Image(
        painter = painterResource(id = R.drawable.img_core_rusdili_logo),
        contentDescription = "LL - Learn Language loqosu",
        contentScale = ContentScale.Crop,
        modifier = modifier.size(size).clip(RoundedCornerShape(10.dp))
    )
}

@Composable
fun SovaMascotAvatar(
    language: LearningLanguage,
    size: Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    val drawableRes = when (language) {
        LearningLanguage.RUSSIAN -> R.drawable.mascot_sova_ru
        LearningLanguage.SPANISH -> R.drawable.mascot_sova_es
        LearningLanguage.FRENCH -> R.drawable.mascot_sova_fr
    }
    val mascotDescription = when (language) {
        LearningLanguage.RUSSIAN -> "Sova maskotu — qulaqlıqlı mavi bayquş"
        LearningLanguage.SPANISH -> "Sova maskotu — qırmızı şlyapalı sarı bayquş"
        LearningLanguage.FRENCH -> "Sova maskotu — beretli bənövşəyi bayquş"
    }
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = mascotDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
    )
}

@Composable
fun SovaMascotCard(
    language: LearningLanguage,
    onSpeakGreeting: () -> Unit,
    onChangeLanguage: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val subtitle = when (language) {
        LearningLanguage.RUSSIAN -> "Sova səninlə rus dilini öyrənir"
        LearningLanguage.SPANISH -> "Sova səninlə ispan dilini öyrənir"
        LearningLanguage.FRENCH -> "Sova səninlə fransız dilini öyrənir"
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_sova_mascot_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SovaMascotAvatar(
                language = language,
                size = 86.dp,
                modifier = Modifier.testTag("sova_mascot_image")
            )
            Spacer(Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = language.greeting,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .testTag("sova_greeting_text")
                        )
                    }
                    IconButton(
                        onClick = onSpeakGreeting,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("mascot_speak_greeting")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "${language.displayNameAz} salamlaşmasını səsləndir",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = "${language.displayNameAz} · ${language.nativeName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (onChangeLanguage != null) {
                    OutlinedButton(
                        onClick = onChangeLanguage,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("mascot_switch_language_button")
                    ) {
                        Text("Dili dəyiş")
                    }
                }
            }
        }
    }
}

@Composable
fun AppButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier.fillMaxWidth()) { Text(text) }
}

@Composable
fun ProgressBar(progress: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun LessonCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun QuizCard(question: String, options: List<String>, onAnswer: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(question, style = MaterialTheme.typography.titleMedium)
            options.forEach { option ->
                Button(onClick = { onAnswer(option) }, modifier = Modifier.fillMaxWidth()) {
                    Text(option)
                }
            }
        }
    }
}

@Composable
fun NewsCard(item: NewsItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (item.unconfirmed) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "Təsdiqlənməyib",
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            item.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Xəbər şəkli",
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                )
            }
            Text(item.titleAz, style = MaterialTheme.typography.titleMedium)
            Text(item.summaryAz, style = MaterialTheme.typography.bodyMedium)
            Text(item.original, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(item.source, style = MaterialTheme.typography.labelMedium)
                Text(item.publishedAt, style = MaterialTheme.typography.labelSmall)
            }
            item.sourceUrl?.let { url ->
                val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                OutlinedButton(onClick = { uriHandler.openUri(url) }) {
                    Text("Mənbə")
                }
            }
        }
    }
}

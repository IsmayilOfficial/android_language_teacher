package com.rusdili.pro.feature.book

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.ui.AppButton
import com.rusdili.pro.core.ui.LessonCard
import com.rusdili.pro.core.ui.ProgressBar

@Composable
fun BookScreen(viewModel: BookViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val current = state) {
        UiState.Loading -> CircularProgressIndicator(Modifier.padding(32.dp))
        is UiState.Error -> Text(current.message, Modifier.padding(24.dp))
        is UiState.Success -> BookContent(current.data, viewModel)
    }
}
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BookContent(data: BookData, viewModel: BookViewModel) {
    var selectedWord by remember { mutableStateOf<String?>(null) }
    BackHandler(enabled = data.selectedChapterId != null) {
        if (data.selectedLessonIndex != null) viewModel.backToChapter() else viewModel.closeChapter()
    }
    when {
        data.selectedLessonIndex != null -> LessonContent(data, viewModel)
        data.selectedChapter != null -> ChapterContent(data, viewModel) { word ->
            selectedWord = word
            viewModel.lookupWord(word)
        }
        else -> ChapterList(data, viewModel)
    }
    selectedWord?.let { token ->
        ModalBottomSheet(
            onDismissRequest = {
                selectedWord = null
                viewModel.clearLookup()
            }
        ) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    token.trim('.', ',', '!', '?', ':', ';', '"', '«', '»', '¡', '¿'),
                    fontWeight = FontWeight.Bold
                )
                if (data.lookupLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Tərcümə: ${data.wordLookup?.word?.translation ?: "Lüğətdə tapılmadı"}")
                    Text("Qrammatik forma: ${data.wordLookup?.grammaticalForm ?: "Məlumat yoxdur"}")
                }
            }
        }
    }
}

@Composable
private fun ChapterList(data: BookData, viewModel: BookViewModel) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("İnteraktiv kitab · ${data.language.displayNameAz}", style = MaterialTheme.typography.headlineMedium)
        Text("Növbəti fəsil əvvəlkinin yekun testindən ən azı 80% topladıqda açılır.")
        data.chapters.forEach { row ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable(enabled = !row.locked) {
                    viewModel.openChapter(row.chapter.id)
                }
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("${row.chapter.id}. fəsil · ${row.chapter.title}", fontWeight = FontWeight.SemiBold)
                        Text(if (row.score > 0) "Nəticə: ${row.score}%" else "Başlanmayıb")
                        ProgressBar(row.score / 100f, Modifier.padding(top = 8.dp))
                    }
                    if (row.locked) Icon(Icons.Default.Lock, contentDescription = "Bağlıdır")
                }
            }
        }
    }
}

@Composable
private fun ChapterContent(
    data: BookData,
    viewModel: BookViewModel,
    onWordSelected: (String) -> Unit
) {
    val chapter = data.selectedChapter ?: return
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = viewModel::closeChapter) { Text("← Fəsillər") }
        Text("${chapter.id}. ${chapter.title}", style = MaterialTheme.typography.headlineSmall)
        LessonCard("Qayda kartı", chapter.rule)
        Text("Nümunələr — sözə toxunaraq tərcüməsinə bax", style = MaterialTheme.typography.titleMedium)
        chapter.examples.forEach { example ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(Modifier.weight(1f).horizontalScroll(rememberScrollState())) {
                            example.russian.split(Regex("\\s+")).forEach { token ->
                                Text(
                                    token,
                                    modifier = Modifier.clickable { onWordSelected(token) }.padding(end = 4.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.speak(example.russian) }) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = "${data.language.displayNameAz} səsləndir"
                            )
                        }
                    }
                    Text(example.azerbaijani)
                    Text("Transliterasiya: ${example.transliteration}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Text("Dərslər", style = MaterialTheme.typography.titleLarge)
        chapter.lessons.forEachIndexed { index, lesson ->
            AppButton("Dərs ${index + 1}: $lesson", { viewModel.startLesson(chapter.id, index) })
        }
        AppButton("Yekun test", { viewModel.startLesson(chapter.id, 3) })
    }
}

@Composable
private fun LessonContent(data: BookData, viewModel: BookViewModel) {
    val chapter = data.selectedChapter ?: return
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = viewModel::nextLesson) { Text("← ${chapter.title}") }
        Text(
            if (data.selectedLessonIndex == 3) "Yekun test · ${chapter.title}"
            else "Dərs ${data.selectedLessonIndex?.plus(1)} · ${chapter.title}",
            style = MaterialTheme.typography.headlineSmall
        )
        if (data.finished) {
            Text("Nəticə: ${data.lessonScore}%  ·  Qazanılan XP: ${data.earnedXp}")
            if (data.selectedLessonIndex == 3 && data.lessonScore < 80) {
                Text("Fəsli açmaq üçün ən azı 80% toplamalısan. Qaydanı təkrar edib yenidən sına.")
            } else if (data.selectedLessonIndex == 3) {
                Text("Əla! Bu fəsli tamamladın və növbəti fəsil açıldı.")
            }
            AppButton(
                if (data.selectedLessonIndex == 3) "Fəsillərə qayıt" else "Növbəti dərs",
                viewModel::nextLesson
            )
            return
        }
        val exercise = data.currentExercise ?: return
        Text("Tapşırıq ${data.exerciseIndex + 1} / 5 · ${exercise.type}")
        ProgressBar((data.exerciseIndex + 1) / 5f)
        Text(exercise.question, style = MaterialTheme.typography.titleMedium)
        if (data.exerciseIndex == 3) {
            val tokens = remember(data.selectedChapterId, data.selectedLessonIndex, data.exerciseIndex) {
                exercise.answer.split(Regex("\\s+")).shuffled()
            }
            val selectedIndices = remember(data.selectedChapterId, data.selectedLessonIndex, data.exerciseIndex) {
                mutableStateListOf<Int>()
            }
            Text("Sözlərə toxunaraq düzgün sıra ilə düz")
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tokens.forEachIndexed { index, token ->
                    if (index !in selectedIndices) {
                        OutlinedButton(
                            onClick = { selectedIndices.add(index) },
                            enabled = !data.answered
                        ) { Text(token) }
                    }
                }
            }
            Text("Seçdiyin sıra: ${selectedIndices.joinToString(" ") { tokens[it] }}")
            if (selectedIndices.size == tokens.size) {
                AppButton(
                    "Cümləni yoxla",
                    { viewModel.answer(selectedIndices.joinToString(" ") { tokens[it] }) }
                )
            }
        } else if (data.exerciseIndex == 4) {
            var typedAnswer by rememberSaveable(data.selectedChapterId, data.selectedLessonIndex) {
                mutableStateOf("")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = typedAnswer,
                    onValueChange = { typedAnswer = it },
                    label = { Text("Eşitdiyini yaz") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { viewModel.speak(exercise.answer) }) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Dinlə")
                }
            }
            AppButton("Cavabı yoxla", { viewModel.answer(typedAnswer) }, Modifier)
        } else {
            exercise.options.forEach { option ->
                OutlinedButton(
                    onClick = { viewModel.answer(option) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !data.answered
                ) { Text(option) }
            }
        }
        if (data.answered) {
            Text(
                data.feedback,
                color = if (data.feedback.startsWith("Doğrudur")) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            AppButton("Davam et", viewModel::continueLesson)
        }
        Spacer(Modifier.height(20.dp))
    }
}

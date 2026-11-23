package com.rusdili.pro.feature.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.common.Word
import com.rusdili.pro.core.ui.AppButton
import com.rusdili.pro.core.ui.ProgressBar

@Composable
fun PracticeScreen(viewModel: PracticeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val current = state) {
        UiState.Loading -> CircularProgressIndicator(Modifier.padding(32.dp))
        is UiState.Error -> Text(current.message, Modifier.padding(24.dp))
        is UiState.Success -> PracticeContent(current.data, viewModel)
    }
}

@Composable
private fun PracticeContent(data: PracticeData, viewModel: PracticeViewModel) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Məşq", style = MaterialTheme.typography.headlineMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { viewModel.setMode(PracticeMode.DAILY) },
                modifier = Modifier.testTag("quiz_mode_daily")
            ) { Text("Gündəlik məşq") }
            OutlinedButton(
                onClick = { viewModel.setMode(PracticeMode.FLASHCARDS) },
                modifier = Modifier.testTag("quiz_mode_flashcards")
            ) { Text("Kartlar") }
        }
        if (data.mode == PracticeMode.DAILY) DailyPractice(data, viewModel) else Flashcards(data, viewModel)
    }
}

@Composable
private fun DailyPractice(data: PracticeData, viewModel: PracticeViewModel) {
    if (data.questions.isEmpty()) {
        Text("Gündəlik məşq üçün əvvəlcə Kitab bölməsində bir fəsli 80% və ya daha yüksək nəticə ilə tamamla.")
        return
    }
    if (data.questionIndex >= data.questions.size) {
        Text("Gündəlik məşq tamamlandı!", style = MaterialTheme.typography.titleLarge)
        Text("Nəticə: ${data.correctAnswers} / ${data.questions.size} düzgün")
        AppButton("Yenidən məşq et", viewModel::restartPractice)
        return
    }
    val question = data.currentQuestion ?: return
    val russianInQuestion = extractRussianText(question.exercise.question)
    val isListeningExercise = question.exercise.type == "Dinlə və yaz"
    val russianAnswer = extractRussianText(question.exercise.answer)

    Text("Sual ${data.questionIndex + 1} / ${data.questions.size} · ${question.chapterTitle}")
    ProgressBar((data.questionIndex + 1) / data.questions.size.toFloat())
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            question.exercise.question,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        if (russianInQuestion != null) {
            IconButton(
                onClick = { viewModel.speak(russianInQuestion) },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("quiz_daily_speak_question")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Sualdakı rusca ifadəni səsləndir"
                )
            }
        }
    }
    if ((isListeningExercise || data.answered) && russianAnswer != null) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { viewModel.speak(russianAnswer) },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("quiz_daily_speak_answer")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(if (isListeningExercise) "Dinlə" else "Düzgün cavabı dinlə")
            }
            TextButton(
                onClick = { viewModel.speak(russianAnswer, slow = true) },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("quiz_daily_speak_answer_slow")
            ) {
                Text("Yavaş")
            }
        }
    }
    question.exercise.options.forEachIndexed { index, option ->
        val optionSpeech = extractRussianText(option)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.answerPractice(option) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("quiz_option_$index"),
                enabled = !data.answered
            ) { Text(option) }
            if (optionSpeech != null) {
                IconButton(
                    onClick = { viewModel.speak(optionSpeech) },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("quiz_option_speak_$index")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Variantı səsləndir: $optionSpeech"
                    )
                }
            }
        }
    }
    if (data.answered) {
        Text(
            data.feedback,
            color = if (data.feedback.startsWith("Doğrudur")) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error
        )
        AppButton("Növbəti sual", viewModel::nextQuestion)
    }
}

@Composable
private fun Flashcards(data: PracticeData, viewModel: PracticeViewModel) {
    val word = data.currentCard
    if (word == null) {
        Text("Bu gün üçün kartlar tamamlandı. Səhv cavab verilən sözlər təkrar vaxtı çatanda görünəcək.")
        return
    }
    Text("Qalan kart: ${data.words.size}")
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    word.russian,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { viewModel.speak(word.russian) },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("quiz_flashcard_speak_word")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Sözü rusca səsləndir: ${word.russian}"
                    )
                }
                TextButton(
                    onClick = { viewModel.speak(word.russian, slow = true) },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("quiz_flashcard_speak_word_slow")
                ) {
                    Text("Yavaş")
                }
            }
            if (data.showingBack) WordBack(word, onSpeak = { viewModel.speak(it) })
            else Text("Tərcüməni görmək üçün karta toxun.")
        }
    }
    AppButton(if (data.showingBack) "Tərcüməni gizlət" else "Kartı çevir", viewModel::flipCard)
    if (data.showingBack) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { viewModel.rateCard(false) },
                modifier = Modifier.weight(1f).testTag("quiz_flashcard_unknown")
            ) {
                Text("Bilmirəm")
            }
            OutlinedButton(
                onClick = { viewModel.rateCard(true) },
                modifier = Modifier.weight(1f).testTag("quiz_flashcard_known")
            ) {
                Text("Bilirəm")
            }
        }
    }
}

@Composable
private fun WordBack(word: Word, onSpeak: (String) -> Unit) {
    Text(word.translation, style = MaterialTheme.typography.titleLarge)
    word.gender?.let { Text("Cins: $it") }
    word.aspectPair?.let { Text("Feil cütü: ${word.russian} — $it") }
    if (word.gender != null) {
        Text("Hal cədvəli (tək / cəm):")
        val cases = flashcardCases[word.russian] ?: generatedFlashcardCases(word.russian)
        cases.forEach { (case, singular, plural) ->
            Text("$case: $singular / $plural", style = MaterialTheme.typography.bodySmall)
        }
    }
    if (word.example.isNotBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Nümunə: ${word.example}",
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onSpeak(word.example) },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("quiz_flashcard_speak_example")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Nümunə cümləni səsləndir"
                )
            }
        }
    }
}

private val cyrillicSegmentRegex = Regex("[\\u0400-\\u04FF]+(?:[\\s,.'\"!?-]+[\\u0400-\\u04FF]+)*")

private fun extractRussianText(text: String): String? {
    val matches = cyrillicSegmentRegex.findAll(text)
        .map { it.value.trim() }
        .filter { it.isNotBlank() }
        .toList()
    if (matches.isEmpty()) return null
    return matches.joinToString(", ")
}

private val flashcardCases = mapOf(
    "книга" to listOf(
        Triple("Adlıq", "книга", "книги"), Triple("Yiyəlik", "книги", "книг"),
        Triple("Yönlük", "книге", "книгам"), Triple("Təsirlik", "книгу", "книги"),
        Triple("Vasitəlik", "книгой", "книгами"), Triple("Yerlik", "книге", "книгах")
    ),
    "стол" to listOf(
        Triple("Adlıq", "стол", "столы"), Triple("Yiyəlik", "стола", "столов"),
        Triple("Yönlük", "столу", "столам"), Triple("Təsirlik", "стол", "столы"),
        Triple("Vasitəlik", "столом", "столами"), Triple("Yerlik", "столе", "столах")
    ),
    "окно" to listOf(
        Triple("Adlıq", "окно", "окна"), Triple("Yiyəlik", "окна", "окон"),
        Triple("Yönlük", "окну", "окнам"), Triple("Təsirlik", "окно", "окна"),
        Triple("Vasitəlik", "окном", "окнами"), Triple("Yerlik", "окне", "окнах")
    ),
    "дом" to listOf(
        Triple("Adlıq", "дом", "дома"), Triple("Yiyəlik", "дома", "домов"),
        Triple("Yönlük", "дому", "домам"), Triple("Təsirlik", "дом", "дома"),
        Triple("Vasitəlik", "домом", "домами"), Triple("Yerlik", "доме", "домах")
    ),
    "день" to listOf(
        Triple("Adlıq", "день", "дни"), Triple("Yiyəlik", "дня", "дней"),
        Triple("Yönlük", "дню", "дням"), Triple("Təsirlik", "день", "дни"),
        Triple("Vasitəlik", "днём", "днями"), Triple("Yerlik", "дне", "днях")
    )
)

private fun generatedFlashcardCases(word: String): List<Triple<String, String, String>> {
    val stem = when {
        word.endsWith("а") || word.endsWith("я") || word.endsWith("о") || word.endsWith("е") ||
            word.endsWith("ь") || word.endsWith("й") -> word.dropLast(1)
        else -> word
    }
    val singular: List<String>
    val plural: List<String>
    when {
        word.endsWith("а") -> {
            singular = listOf(word, "${stem}ы", "${stem}е", "${stem}у", "${stem}ой", "${stem}е")
            plural = listOf("${stem}ы", stem, "${stem}ам", "${stem}ы", "${stem}ами", "${stem}ах")
        }
        word.endsWith("я") -> {
            singular = listOf(word, "${stem}и", "${stem}е", "${stem}ю", "${stem}ей", "${stem}е")
            plural = listOf("${stem}и", "${stem}ь", "${stem}ям", "${stem}и", "${stem}ями", "${stem}ях")
        }
        word.endsWith("о") || word.endsWith("е") -> {
            singular = listOf(word, "${stem}а", "${stem}у", word, "${stem}ом", "${stem}е")
            plural = listOf("${stem}а", "${stem}ов", "${stem}ам", "${stem}а", "${stem}ами", "${stem}ах")
        }
        else -> {
            val genitive = if (word.endsWith("ь") || word.endsWith("й")) "${stem}я" else "${word}а"
            val dative = if (word.endsWith("ь") || word.endsWith("й")) "${stem}ю" else "${word}у"
            val pluralNominative = if (word.endsWith("ь") || word.endsWith("й")) "${stem}и" else "${word}ы"
            singular = listOf(word, genitive, dative, genitive, "${word}ом", "${word}е")
            plural = listOf(pluralNominative, "${word}ов", "${stem}ам", pluralNominative, "${stem}ами", "${stem}ах")
        }
    }
    val names = listOf("Adlıq", "Yiyəlik", "Yönlük", "Təsirlik", "Vasitəlik", "Yerlik")
    return names.indices.map { Triple(names[it], singular[it], plural[it]) }
}

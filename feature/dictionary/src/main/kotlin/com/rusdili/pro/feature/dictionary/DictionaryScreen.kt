package com.rusdili.pro.feature.dictionary

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.common.Word

@Composable
fun DictionaryScreen(viewModel: DictionaryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val current = state) {
        UiState.Loading -> CircularProgressIndicator(Modifier.padding(32.dp))
        is UiState.Error -> Text(current.message, Modifier.padding(24.dp))
        is UiState.Success -> DictionaryContent(current.data, viewModel)
    }
}

@Composable
private fun DictionaryContent(data: DictionaryData, viewModel: DictionaryViewModel) {
    var selectedWord by remember { mutableStateOf<Word?>(null) }
    var wordToAddToDeck by remember { mutableStateOf<Word?>(null) }
    var activeReviewDeckId by remember { mutableStateOf<Long?>(null) }

    val activeReviewDeck = activeReviewDeckId?.let { id -> data.decks.firstOrNull { it.id == id } }
    if (activeReviewDeck != null) {
        DeckReviewScreen(
            deck = activeReviewDeck,
            onSpeak = viewModel::speak,
            onStopSpeech = viewModel::stopSpeech,
            onRecordReview = viewModel::recordFlashcardReview,
            onExit = { activeReviewDeckId = null }
        )
        return
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Lüğət", style = MaterialTheme.typography.headlineMedium)
        val hasCyrillicQuery = remember(data.query) { containsCyrillic(data.query) }
        OutlinedTextField(
            value = data.query,
            onValueChange = viewModel::search,
            label = { Text("Rusca və ya azərbaycanca axtar") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (data.query.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (hasCyrillicQuery) {
                            IconButton(
                                onClick = { viewModel.speak(data.query) },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("dict_search_speak_query")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Axtarılan sözü səsləndir: ${data.query}",
                                    tint = if (data.speakingText == data.query.trim()) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        LocalContentColor.current
                                    }
                                )
                            }
                        }
                        IconButton(
                            onClick = { viewModel.search("") },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("dict_clear_search_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Axtarışı təmizlə")
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dict_search_input"),
            singleLine = true
        )
        if (!data.isSpeechAvailable) {
            Text(
                text = "Qeyd: Bu cihazda rus dili səs paketi aktiv deyil.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("dict_tts_unavailable_notice")
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = data.section == DictionarySection.ALL,
                onClick = { viewModel.selectSection(DictionarySection.ALL) },
                label = { Text("Bütün sözlər") },
                modifier = Modifier.testTag("dict_tab_all")
            )
            FilterChip(
                selected = data.section == DictionarySection.FAVORITES,
                onClick = { viewModel.selectSection(DictionarySection.FAVORITES) },
                label = { Text("Seçilmişlər (${data.favoriteWords.size})") },
                modifier = Modifier.testTag("dict_tab_favorites")
            )
            FilterChip(
                selected = data.section == DictionarySection.FLASHCARDS,
                onClick = { viewModel.selectSection(DictionarySection.FLASHCARDS) },
                label = { Text("Kart dəstləri (${data.decks.size})") },
                modifier = Modifier.testTag("dict_tab_flashcards")
            )
            FilterChip(
                selected = data.section == DictionarySection.HISTORY,
                onClick = { viewModel.selectSection(DictionarySection.HISTORY) },
                label = { Text("Tarixçə (${data.history.size})") },
                modifier = Modifier.testTag("dict_tab_history")
            )
        }
        when (data.section) {
            DictionarySection.FLASHCARDS -> {
                FlashcardsDeckSection(
                    decks = data.decks,
                    allWords = data.allWords,
                    favoriteWords = data.favoriteWords,
                    onCreateDeck = viewModel::createDeck,
                    onDeleteDeck = viewModel::deleteDeck,
                    onToggleWordInDeck = viewModel::toggleWordInDeck,
                    onStartReview = { deckId -> activeReviewDeckId = deckId }
                )
            }
            DictionarySection.HISTORY -> {
                SearchHistorySection(
                    history = data.history,
                    onSelectEntry = { entry ->
                        val matchedWord = entry.matchedRussian?.let { russian ->
                            data.allWords.firstOrNull { it.russian == russian }
                        }
                        if (matchedWord != null) {
                            selectedWord = matchedWord
                            viewModel.recordWordLookup(matchedWord)
                        } else {
                            viewModel.selectHistoryEntry(entry)
                        }
                    },
                    onSpeak = { viewModel.speak(it) },
                    onRemoveEntry = viewModel::removeHistoryEntry,
                    onClearAll = viewModel::clearHistory
                )
            }
            DictionarySection.ALL, DictionarySection.FAVORITES -> {
                if (data.section == DictionarySection.ALL && data.query.isBlank() && data.history.isNotEmpty()) {
                    RecentSearchChipsRow(
                        history = data.history,
                        onSelectEntry = viewModel::selectHistoryEntry,
                        onRemoveEntry = viewModel::removeHistoryEntry,
                        onClearAll = viewModel::clearHistory
                    )
                }
                if (data.words.isEmpty()) {
                    if (data.section == DictionarySection.ALL && hasCyrillicQuery) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "«${data.query.trim()}» lüğətdə tapılmadı, amma tələffüzünü dinləyə bilərsən.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Button(
                                    onClick = { viewModel.speak(data.query) },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("dict_empty_speak_query")
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text("Səsləndir")
                                }
                            }
                        }
                    } else {
                        Text(
                            if (data.section == DictionarySection.FAVORITES) "Seçilmiş söz yoxdur"
                            else "Uyğun söz tapılmadı"
                        )
                    }
                }
                data.words.forEach { word ->
                    val isWordSpeaking = data.speakingText == word.russian
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            selectedWord = word
                            viewModel.recordWordLookup(word)
                        }
                    ) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(word.russian, style = MaterialTheme.typography.titleLarge)
                            Text(word.translation)
                            word.gender?.let { Text("Cins: $it", style = MaterialTheme.typography.bodySmall) }
                        }
                        IconButton(
                            onClick = { viewModel.speak(word.russian) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("dict_speak_${word.russian}")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Sözü rusca səsləndir: ${word.russian}",
                                tint = if (isWordSpeaking) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    LocalContentColor.current
                                }
                            )
                        }
                        IconButton(
                            onClick = { wordToAddToDeck = word },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("dict_add_to_deck_${word.russian}")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.PlaylistAdd,
                                contentDescription = "Kart dəstinə əlavə et: ${word.russian}"
                            )
                        }
                        IconButton(
                            onClick = { viewModel.toggleFavorite(word) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("dict_favorite_${word.russian}")
                        ) {
                            Icon(
                                if (word.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (word.isFavorite) "Seçilmişlərdən sil" else "Seçilmişlərə əlavə et"
                            )
                        }
                    }
                }
            }
        }
    }
    }
    wordToAddToDeck?.let { word ->
        AddWordToDeckDialog(
            word = word,
            decks = data.decks,
            onToggleWordInDeck = viewModel::toggleWordInDeck,
            onCreateDeckWithWord = { deckName, russian ->
                viewModel.createDeck(deckName, "", listOf(russian))
            },
            onDismiss = { wordToAddToDeck = null }
        )
    }
    selectedWord?.let { word ->
        AlertDialog(
            onDismissRequest = {
                viewModel.stopSpeech()
                selectedWord = null
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        word.russian,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.speak(word.russian) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("dict_dialog_speak_word")
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
                                .testTag("dict_dialog_speak_word_slow")
                        ) {
                            Text("Yavaş")
                        }
                    }
                }
            },
            text = {
                Column(
                    Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Tərcümə: ${word.translation}")
                    if (word.aspectPair != null) {
                        Text("Feil növü və cütü: ${word.russian} — ${word.aspectPair}")
                        VerbTenses(word)
                    } else if (word.gender != null) {
                        Text("Cins: ${word.gender}")
                        CaseTable(word.russian, word.gender)
                    } else {
                        Text("Bu söz üçün cins və hal cədvəli tətbiq edilmir.")
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
                                onClick = { viewModel.speak(word.example) },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("dict_dialog_speak_example")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Nümunə cümləni səsləndir"
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = {
                            wordToAddToDeck = word
                            viewModel.stopSpeech()
                            selectedWord = null
                        },
                        modifier = Modifier.testTag("dict_dialog_add_to_deck")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.PlaylistAdd,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Dəstə əlavə et")
                    }
                    TextButton(
                        onClick = {
                            viewModel.stopSpeech()
                            selectedWord = null
                        },
                        modifier = Modifier.testTag("dict_dialog_close_button")
                    ) { Text("Bağla") }
                }
            }
        )
    }
}

@Composable
private fun CaseTable(word: String, gender: String?) {
    val rows = caseForms[word] ?: generatedCaseForms(word, gender)
    Text("Hal cədvəli (tək / cəm)", style = MaterialTheme.typography.titleSmall)
    rows.forEach { (case, singular, plural) ->
        Text("$case: $singular / $plural", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun VerbTenses(word: Word) {
    val conjugation = verbForms[word.russian] ?: listOf(
        word.russian,
        word.russian,
        word.russian,
        word.russian,
        word.russian,
        word.russian
    )
    val perfective = word.russian in perfectiveVerbs
    if (perfective) {
        Text("İndiki zaman: yoxdur (bitmiş növün indiki zamanı olmur).")
    } else {
        Text("İndiki zaman: я ${conjugation[0]}, ты ${conjugation[1]}, он/она ${conjugation[2]}, мы ${conjugation[3]}, вы ${conjugation[4]}, они ${conjugation[5]}")
    }
    val pastForms = irregularPast[word.russian]
    val pastStem = word.russian.removeSuffix("ть")
    Text(
        pastForms ?: "Keçmiş zaman: он ${pastStem}л, она ${pastStem}ла, " +
            "оно ${pastStem}ло, они ${pastStem}ли"
    )
    val future = if (perfective) {
        verbForms[word.russian]?.firstOrNull()?.let { "Gələcək zaman: я $it" }
            ?: "Gələcək zaman: bitmiş növ"
    } else {
        val resultVerb = word.aspectPair?.let { verbForms[it]?.firstOrNull() }
        "Gələcək zaman: я буду ${word.russian}" +
            (resultVerb?.let { "; bitmiş nəticə: я $it" } ?: "")
    }
    Text(future)
}

private val caseForms = mapOf(
    "стол" to listOf(
        Triple("Adlıq", "стол", "столы"), Triple("Yiyəlik", "стола", "столов"),
        Triple("Yönlük", "столу", "столам"), Triple("Təsirlik", "стол", "столы"),
        Triple("Vasitəlik", "столом", "столами"), Triple("Yerlik", "столе", "столах")
    ),
    "книга" to listOf(
        Triple("Adlıq", "книга", "книги"), Triple("Yiyəlik", "книги", "книг"),
        Triple("Yönlük", "книге", "книгам"), Triple("Təsirlik", "книгу", "книги"),
        Triple("Vasitəlik", "книгой", "книгами"), Triple("Yerlik", "книге", "книгах")
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
    "мама" to listOf(
        Triple("Adlıq", "мама", "мамы"), Triple("Yiyəlik", "мамы", "мам"),
        Triple("Yönlük", "маме", "мамам"), Triple("Təsirlik", "маму", "мам"),
        Triple("Vasitəlik", "мамой", "мамами"), Triple("Yerlik", "маме", "мамах")
    ),
    "брат" to listOf(
        Triple("Adlıq", "брат", "братья"), Triple("Yiyəlik", "брата", "братьев"),
        Triple("Yönlük", "брату", "братьям"), Triple("Təsirlik", "брата", "братьев"),
        Triple("Vasitəlik", "братом", "братьями"), Triple("Yerlik", "брате", "братьях")
    ),
    "друг" to listOf(
        Triple("Adlıq", "друг", "друзья"), Triple("Yiyəlik", "друга", "друзей"),
        Triple("Yönlük", "другу", "друзьям"), Triple("Təsirlik", "друга", "друзей"),
        Triple("Vasitəlik", "другом", "друзьями"), Triple("Yerlik", "друге", "друзьях")
    ),
    "вода" to listOf(
        Triple("Adlıq", "вода", "воды"), Triple("Yiyəlik", "воды", "вод"),
        Triple("Yönlük", "воде", "водам"), Triple("Təsirlik", "воду", "воды"),
        Triple("Vasitəlik", "водой", "водами"), Triple("Yerlik", "воде", "водах")
    ),
    "день" to listOf(
        Triple("Adlıq", "день", "дни"), Triple("Yiyəlik", "дня", "дней"),
        Triple("Yönlük", "дню", "дням"), Triple("Təsirlik", "день", "дни"),
        Triple("Vasitəlik", "днём", "днями"), Triple("Yerlik", "дне", "днях")
    ),
    "ночь" to listOf(
        Triple("Adlıq", "ночь", "ночи"), Triple("Yiyəlik", "ночи", "ночей"),
        Triple("Yönlük", "ночи", "ночам"), Triple("Təsirlik", "ночь", "ночи"),
        Triple("Vasitəlik", "ночью", "ночами"), Triple("Yerlik", "ночи", "ночах")
    ),
    "время" to listOf(
        Triple("Adlıq", "время", "времена"), Triple("Yiyəlik", "времени", "времён"),
        Triple("Yönlük", "времени", "временам"), Triple("Təsirlik", "время", "времена"),
        Triple("Vasitəlik", "временем", "временами"), Triple("Yerlik", "времени", "временах")
    ),
    "человек" to listOf(
        Triple("Adlıq", "человек", "люди"), Triple("Yiyəlik", "человека", "людей"),
        Triple("Yönlük", "человеку", "людям"), Triple("Təsirlik", "человека", "людей"),
        Triple("Vasitəlik", "человеком", "людьми"), Triple("Yerlik", "человеке", "людях")
    ),
    "глаз" to listOf(
        Triple("Adlıq", "глаз", "глаза"), Triple("Yiyəlik", "глаза", "глаз"),
        Triple("Yönlük", "глазу", "глазам"), Triple("Təsirlik", "глаз", "глаза"),
        Triple("Vasitəlik", "глазом", "глазами"), Triple("Yerlik", "глазе", "глазах")
    ),
    "рука" to listOf(
        Triple("Adlıq", "рука", "руки"), Triple("Yiyəlik", "руки", "рук"),
        Triple("Yönlük", "руке", "рукам"), Triple("Təsirlik", "руку", "руки"),
        Triple("Vasitəlik", "рукой", "руками"), Triple("Yerlik", "руке", "руках")
    ),
    "море" to listOf(
        Triple("Adlıq", "море", "моря"), Triple("Yiyəlik", "моря", "морей"),
        Triple("Yönlük", "морю", "морям"),         Triple("Təsirlik", "море", "моря"),
        Triple("Vasitəlik", "морем", "морями"), Triple("Yerlik", "море", "морях")
    ),
    "слово" to listOf(
        Triple("Adlıq", "слово", "слова"), Triple("Yiyəlik", "слова", "слов"),
        Triple("Yönlük", "слову", "словам"), Triple("Təsirlik", "слово", "слова"),
        Triple("Vasitəlik", "словом", "словами"), Triple("Yerlik", "слове", "словах")
    ),
    "учитель" to listOf(
        Triple("Adlıq", "учитель", "учителя"), Triple("Yiyəlik", "учителя", "учителей"),
        Triple("Yönlük", "учителю", "учителям"), Triple("Təsirlik", "учителя", "учителей"),
        Triple("Vasitəlik", "учителем", "учителями"), Triple("Yerlik", "учителе", "учителях")
    )
)

private val verbForms = mapOf(
    "читать" to listOf("читаю", "читаешь", "читает", "читаем", "читаете", "читают"),
    "говорить" to listOf("говорю", "говоришь", "говорит", "говорим", "говорите", "говорят"),
    "писать" to listOf("пишу", "пишешь", "пишет", "пишем", "пишете", "пишут"),
    "смотреть" to listOf("смотрю", "смотришь", "смотрит", "смотрим", "смотрите", "смотрят"),
    "видеть" to listOf("вижу", "видишь", "видит", "видим", "видите", "видят"),
    "слушать" to listOf("слушаю", "слушаешь", "слушает", "слушаем", "слушаете", "слушают"),
    "брать" to listOf("беру", "берёшь", "берёт", "берём", "берёте", "берут"),
    "давать" to listOf("даю", "даёшь", "даёт", "даём", "даёте", "дают"),
    "помогать" to listOf("помогаю", "помогаешь", "помогает", "помогаем", "помогаете", "помогают"),
    "начинать" to listOf("начинаю", "начинаешь", "начинает", "начинаем", "начинаете", "начинают"),
    "заканчивать" to listOf("заканчиваю", "заканчиваешь", "заканчивает", "заканчиваем", "заканчиваете", "заканчивают"),
    "делать" to listOf("делаю", "делаешь", "делает", "делаем", "делаете", "делают"),
    "готовить" to listOf("готовлю", "готовишь", "готовит", "готовим", "готовите", "готовят"),
    "учить" to listOf("учу", "учишь", "учит", "учим", "учите", "учат"),
    "идти" to listOf("иду", "идёшь", "идёт", "идём", "идёте", "идут"),
    "ходить" to listOf("хожу", "ходишь", "ходит", "ходим", "ходите", "ходят"),
    "ехать" to listOf("еду", "едешь", "едет", "едем", "едете", "едут"),
    "ездить" to listOf("езжу", "ездишь", "ездит", "ездим", "ездите", "ездят"),
    "лететь" to listOf("лечу", "летишь", "летит", "летим", "летите", "летят"),
    "летать" to listOf("летаю", "летаешь", "летает", "летаем", "летаете", "летают"),
    "учиться" to listOf("учусь", "учишься", "учится", "учимся", "учитесь", "учатся"),
    "улыбаться" to listOf("улыбаюсь", "улыбаешься", "улыбается", "улыбаемся", "улыбаетесь", "улыбаются"),
    "любить" to listOf("люблю", "любишь", "любит", "любим", "любите", "любят"),
    "жить" to listOf("живу", "живёшь", "живёт", "живём", "живёте", "живут"),
    "знать" to listOf("знаю", "знаешь", "знает", "знаем", "знаете", "знают"),
    "понимать" to listOf("понимаю", "понимаешь", "понимает", "понимаем", "понимаете", "понимают"),
    "пить" to listOf("пью", "пьёшь", "пьёт", "пьём", "пьёте", "пьют"),
    "есть" to listOf("ем", "ешь", "ест", "едим", "едите", "едят"),
    "спать" to listOf("сплю", "спишь", "спит", "спим", "спите", "спят"),
    "играть" to listOf("играю", "играешь", "играет", "играем", "играете", "играют"),
    "искать" to listOf("ищу", "ищешь", "ищет", "ищем", "ищете", "ищут"),
    "ждать" to listOf("жду", "ждёшь", "ждёт", "ждём", "ждёте", "ждут"),
    "открывать" to listOf("открываю", "открываешь", "открывает", "открываем", "открываете", "открывают"),
    "закрывать" to listOf("закрываю", "закрываешь", "закрывает", "закрываем", "закрываете", "закрывают"),
    "встречать" to listOf("встречаю", "встречаешь", "встречает", "встречаем", "встречаете", "встречают"),
    "встречаться" to listOf("встречаюсь", "встречаешься", "встречается", "встречаемся", "встречаетесь", "встречаются"),
    "работать" to listOf("работаю", "работаешь", "работает", "работаем", "работаете", "работают"),
    "покупать" to listOf("покупаю", "покупаешь", "покупает", "покупаем", "покупаете", "покупают"),
    "думать" to listOf("думаю", "думаешь", "думает", "думаем", "думаете", "думают"),
    "сделать" to listOf("сделаю", "сделаешь", "сделает", "сделаем", "сделаете", "сделают"),
    "прочитать" to listOf("прочитаю", "прочитаешь", "прочитает", "прочитаем", "прочитаете", "прочитают"),
    "написать" to listOf("напишу", "напишешь", "напишет", "напишем", "напишете", "напишут"),
    "сказать" to listOf("скажу", "скажешь", "скажет", "скажем", "скажете", "скажут"),
    "посмотреть" to listOf("посмотрю", "посмотришь", "посмотрит", "посмотрим", "посмотрите", "посмотрят"),
    "увидеть" to listOf("увижу", "увидишь", "увидит", "увидим", "увидите", "увидят"),
    "послушать" to listOf("послушаю", "послушаешь", "послушает", "послушаем", "послушаете", "послушают"),
    "взять" to listOf("возьму", "возьмёшь", "возьмёт", "возьмём", "возьмёте", "возьмут"),
    "дать" to listOf("дам", "дашь", "даст", "дадим", "дадите", "дадут"),
    "помочь" to listOf("помогу", "поможешь", "поможет", "поможем", "поможете", "помогут"),
    "начать" to listOf("начну", "начнёшь", "начнёт", "начнём", "начнёте", "начнут"),
    "закончить" to listOf("закончу", "закончишь", "закончит", "закончим", "закончите", "закончат"),
    "купить" to listOf("куплю", "купишь", "купит", "купим", "купите", "купят"),
    "приготовить" to listOf("приготовлю", "приготовишь", "приготовит", "приготовим", "приготовите", "приготовят"),
    "выучить" to listOf("выучу", "выучишь", "выучит", "выучим", "выучите", "выучат"),
    "подумать" to listOf("подумаю", "подумаешь", "подумает", "подумаем", "подумаете", "подумают")
)

private val perfectiveVerbs = setOf(
    "сделать", "прочитать", "написать", "сказать", "посмотреть", "увидеть",
    "послушать", "взять", "дать", "помочь", "начать", "закончить", "купить",
    "приготовить", "выучить", "подумать"
)

private val irregularPast = mapOf(
    "идти" to "Keçmiş zaman: он шёл, она шла, оно шло, они шли",
    "есть" to "Keçmiş zaman: он ел, она ела, оно ело, они ели",
    "жить" to "Keçmiş zaman: он жил, она жила, оно жило, они жили",
    "помочь" to "Keçmiş zaman: он помог, она помогла, оно помогло, они помогли",
    "дать" to "Keçmiş zaman: он дал, она дала, оно дало, они дали"
)

private fun generatedCaseForms(word: String, gender: String?): List<Triple<String, String, String>> {
    val names = listOf("Adlıq", "Yiyəlik", "Yönlük", "Təsirlik", "Vasitəlik", "Yerlik")
    val feminine = gender == "qadın"
    val neuter = gender == "orta"
    val stem = when {
        word.endsWith("а") || word.endsWith("я") || word.endsWith("о") || word.endsWith("е") ->
            word.dropLast(1)
        word.endsWith("ь") -> word.dropLast(1)
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
        neuter -> {
            singular = listOf(word, "${stem}а", "${stem}у", word, "${stem}ом", "${stem}е")
            plural = listOf("${stem}а", "${stem}ов", "${stem}ам", "${stem}а", "${stem}ами", "${stem}ах")
        }
        feminine && word.endsWith("ь") -> {
            singular = listOf(word, "${stem}и", "${stem}и", word, "${stem}ью", "${stem}и")
            plural = listOf("${stem}и", "${stem}ей", "${stem}ям", "${stem}и", "${stem}ями", "${stem}ях")
        }
        else -> {
            val genitive = if (word.endsWith("ь")) "${stem}я" else "${word}а"
            val dative = if (word.endsWith("ь")) "${stem}ю" else "${word}у"
            val instrumental = if (word.endsWith("ь")) "${stem}ем" else "${word}ом"
            val prepositional = if (word.endsWith("ь")) "${stem}е" else "${word}е"
            val pluralNominative = if (word.endsWith("ь") || word.endsWith("й")) "${stem}и" else "${word}ы"
            singular = listOf(word, genitive, dative, genitive, instrumental, prepositional)
            plural = listOf(
                pluralNominative, "${word}ов", "${stem}ам", pluralNominative,
                "${stem}ами", "${stem}ах"
            )
        }
    }
    return names.indices.map { index -> Triple(names[index], singular[index], plural[index]) }
}

private val cyrillicCharRegex = Regex("[\\u0400-\\u04FF]")

private fun containsCyrillic(text: String): Boolean = cyrillicCharRegex.containsMatchIn(text)

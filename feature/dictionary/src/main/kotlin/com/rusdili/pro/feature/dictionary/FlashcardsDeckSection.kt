package com.rusdili.pro.feature.dictionary

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rusdili.pro.core.common.FlashcardDeck
import com.rusdili.pro.core.common.Word
import com.rusdili.pro.core.ui.AppButton
import com.rusdili.pro.core.ui.ProgressBar

@Composable
internal fun FlashcardsDeckSection(
    decks: List<FlashcardDeck>,
    allWords: List<Word>,
    favoriteWords: List<Word>,
    onCreateDeck: (name: String, description: String, initialWords: List<String>) -> Unit,
    onDeleteDeck: (deckId: Long) -> Unit,
    onToggleWordInDeck: (deckId: Long, wordRussian: String, currentlyInDeck: Boolean) -> Unit,
    onStartReview: (deckId: Long) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var manageDeckId by remember { mutableStateOf<Long?>(null) }
    var deleteConfirmDeck by remember { mutableStateOf<FlashcardDeck?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .testTag("dict_create_deck_button")
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Yeni kart dəsti yarat")
        }

        if (decks.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dict_flashcards_empty_state"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Style,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Hələ kart dəstin yoxdur",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Seçilmiş sözlərindən və ya lüğətdəki istənilən sözlərdən öz xüsusi təkrar dəstlərini yarat.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (favoriteWords.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                onCreateDeck(
                                    "Seçilmiş sözlər",
                                    "Lüğətdən seçilmiş sözlərin təkrarı",
                                    favoriteWords.map(Word::russian)
                                )
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("dict_quick_deck_from_favorites")
                        ) {
                            Text("Seçilmişlərdən dəst yarat (${favoriteWords.size})")
                        }
                    }
                }
            }
        } else {
            decks.forEach { deck ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deck_card_${deck.id}"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = deck.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (deck.description.isNotBlank()) {
                                    Text(
                                        text = deck.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "${deck.words.size} söz",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (deck.words.isNotEmpty()) {
                            Text(
                                text = deck.words.take(5).joinToString(" · ") { it.russian } +
                                    if (deck.words.size > 5) " ..." else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = "Dəstdə hələ söz yoxdur. Söz əlavə etmək üçün «Sözlər» düyməsinə toxun.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onStartReview(deck.id) },
                                enabled = deck.words.isNotEmpty(),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("deck_review_${deck.id}")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Təkrar et")
                            }
                            OutlinedButton(
                                onClick = { manageDeckId = deck.id },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("deck_manage_words_${deck.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Sözlər")
                            }
                            IconButton(
                                onClick = { deleteConfirmDeck = deck },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("deck_delete_${deck.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "${deck.name} dəstini sil",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateDeckDialog(
            allWords = allWords,
            favoriteWords = favoriteWords,
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, description, initialWords ->
                onCreateDeck(name, description, initialWords)
                showCreateDialog = false
            }
        )
    }

    manageDeckId?.let { targetId ->
        val deck = decks.firstOrNull { it.id == targetId }
        if (deck != null) {
            ManageDeckWordsDialog(
                deck = deck,
                allWords = allWords,
                favoriteWords = favoriteWords,
                onToggleWord = { wordRussian, isCurrentlyInDeck ->
                    onToggleWordInDeck(deck.id, wordRussian, isCurrentlyInDeck)
                },
                onDismiss = { manageDeckId = null }
            )
        } else {
            manageDeckId = null
        }
    }

    deleteConfirmDeck?.let { deck ->
        AlertDialog(
            onDismissRequest = { deleteConfirmDeck = null },
            title = { Text("Dəsti silmək istəyirsən?") },
            text = { Text("«${deck.name}» kart dəsti silinəcək. Lüğətdəki sözlərin özləri qalacaq.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteDeck(deck.id)
                        deleteConfirmDeck = null
                    },
                    modifier = Modifier.testTag("confirm_delete_deck_${deck.id}")
                ) {
                    Text("Sil", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmDeck = null }) { Text("Ləğv et") }
            }
        )
    }
}

@Composable
internal fun DeckReviewScreen(
    deck: FlashcardDeck,
    onSpeak: (String, Boolean) -> Unit,
    onStopSpeech: () -> Unit,
    onRecordReview: (Boolean) -> Unit,
    onExit: () -> Unit
) {
    BackHandler {
        onStopSpeech()
        onExit()
    }

    var sessionKey by remember(deck.id) { mutableIntStateOf(0) }
    val queue = remember(deck.id, sessionKey) {
        mutableStateListOf<Word>().apply { addAll(deck.words) }
    }
    var knownCount by remember(deck.id, sessionKey) { mutableIntStateOf(0) }
    var repeatCount by remember(deck.id, sessionKey) { mutableIntStateOf(0) }
    var isFlipped by remember(deck.id, sessionKey) { mutableStateOf(false) }

    val totalCount = deck.words.size.coerceAtLeast(1)
    val currentCard = queue.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    onStopSpeech()
                    onExit()
                },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("deck_review_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Kart dəstləri")
            }
        }

        Text(
            text = deck.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (deck.words.isEmpty()) {
            Text("Bu dəstdə söz yoxdur. Əvvəlcə dəstə söz əlavə et.")
            AppButton("Dəstlərə qayıt", onExit)
            return@Column
        }

        Text(
            text = "Qalan: ${queue.size}  ·  Bildim: $knownCount",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ProgressBar(knownCount / totalCount.toFloat())

        if (currentCard == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("deck_review_completed_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "Dəst tamamlandı!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Bütün $knownCount sözü təkrar etdin ($repeatCount dəfə yenidən baxış).")
                    Button(
                        onClick = { sessionKey++ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("deck_review_restart")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Yenidən təkrar et")
                    }
                    OutlinedButton(
                        onClick = {
                            onStopSpeech()
                            onExit()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                    ) {
                        Text("Dəstlərə qayıt")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isFlipped = !isFlipped }
                    .testTag("deck_review_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentCard.russian,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { onSpeak(currentCard.russian, false) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("deck_review_speak_word")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Sözü səsləndir: ${currentCard.russian}"
                            )
                        }
                        TextButton(
                            onClick = { onSpeak(currentCard.russian, true) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("deck_review_speak_word_slow")
                        ) {
                            Text("Yavaş")
                        }
                    }

                    AnimatedVisibility(visible = isFlipped) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = currentCard.translation,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            currentCard.gender?.let { Text("Cins: $it") }
                            currentCard.aspectPair?.let {
                                Text("Feil cütü: ${currentCard.russian} — $it")
                            }
                            if (currentCard.example.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Nümunə: ${currentCard.example}",
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { onSpeak(currentCard.example, false) },
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("deck_review_speak_example")
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Nümunə cümləni səsləndir"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (!isFlipped) {
                        Text(
                            text = "Tərcüməni görmək üçün karta toxun",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            AppButton(
                text = if (isFlipped) "Cavabı gizlət" else "Kartı çevir",
                onClick = { isFlipped = !isFlipped },
                modifier = Modifier.testTag("deck_review_flip_button")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onStopSpeech()
                        val card = queue.removeAt(0)
                        queue.add(card)
                        repeatCount++
                        isFlipped = false
                        onRecordReview(false)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("deck_review_again")
                ) {
                    Text("Yenidən bax")
                }
                Button(
                    onClick = {
                        onStopSpeech()
                        queue.removeAt(0)
                        knownCount++
                        isFlipped = false
                        onRecordReview(true)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("deck_review_known")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Bildim")
                }
            }
        }
    }
}

@Composable
private fun CreateDeckDialog(
    allWords: List<Word>,
    favoriteWords: List<Word>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, initialWords: List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var onlyFavorites by remember { mutableStateOf(favoriteWords.isNotEmpty()) }
    var filterQuery by remember { mutableStateOf("") }
    val selected = remember {
        mutableStateListOf<String>().apply {
            addAll(favoriteWords.map(Word::russian))
        }
    }

    val sourceList = if (onlyFavorites) favoriteWords else allWords
    val visibleWords = remember(sourceList, filterQuery) {
        val trimmed = filterQuery.trim()
        if (trimmed.isEmpty()) sourceList
        else sourceList.filter {
            it.russian.contains(trimmed, ignoreCase = true) ||
                it.translation.contains(trimmed, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni kart dəsti", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Dəstin adı") },
                    placeholder = { Text("Məsələn: Gündəlik feillər") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_deck_name_input")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Qısa izah (istəyə bağlı)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_deck_desc_input")
                )

                Text(
                    text = "Sözləri seç (${selected.size} seçilib)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = onlyFavorites,
                        onClick = { onlyFavorites = true },
                        label = { Text("Seçilmişlər (${favoriteWords.size})") }
                    )
                    FilterChip(
                        selected = !onlyFavorites,
                        onClick = { onlyFavorites = false },
                        label = { Text("Bütün sözlər") }
                    )
                }

                OutlinedTextField(
                    value = filterQuery,
                    onValueChange = { filterQuery = it },
                    label = { Text("Söz axtar") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (visibleWords.isEmpty()) {
                    Text(
                        text = if (onlyFavorites) {
                            "Seçilmiş söz yoxdur. «Bütün sözlər» seçərək istənilən sözü əlavə edə bilərsən."
                        } else {
                            "Uyğun söz tapılmadı"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        visibleWords.forEach { word ->
                            val isSelected = word.russian in selected
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSelected) selected.remove(word.russian)
                                        else selected.add(word.russian)
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            if (word.russian !in selected) selected.add(word.russian)
                                        } else {
                                            selected.remove(word.russian)
                                        }
                                    }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(word.russian, fontWeight = FontWeight.Medium)
                                    Text(
                                        word.translation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, description, selected.toList()) },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("create_deck_confirm_button")
            ) {
                Text("Yarat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Ləğv et") }
        }
    )
}

@Composable
private fun ManageDeckWordsDialog(
    deck: FlashcardDeck,
    allWords: List<Word>,
    favoriteWords: List<Word>,
    onToggleWord: (wordRussian: String, currentlyInDeck: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var onlyFavorites by remember { mutableStateOf(false) }
    var filterQuery by remember { mutableStateOf("") }
    val deckWordKeys = remember(deck.words) { deck.words.mapTo(mutableSetOf()) { it.russian } }

    val sourceList = if (onlyFavorites) favoriteWords else allWords
    val visibleWords = remember(sourceList, filterQuery) {
        val trimmed = filterQuery.trim()
        if (trimmed.isEmpty()) sourceList
        else sourceList.filter {
            it.russian.contains(trimmed, ignoreCase = true) ||
                it.translation.contains(trimmed, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("${deck.name} · ${deck.words.size} söz", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !onlyFavorites,
                        onClick = { onlyFavorites = false },
                        label = { Text("Hamısı") }
                    )
                    FilterChip(
                        selected = onlyFavorites,
                        onClick = { onlyFavorites = true },
                        label = { Text("Seçilmişlər (${favoriteWords.size})") }
                    )
                }
                OutlinedTextField(
                    value = filterQuery,
                    onValueChange = { filterQuery = it },
                    label = { Text("Söz axtar") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (visibleWords.isEmpty()) {
                        Text(
                            text = "Uyğun söz tapılmadı",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        visibleWords.forEach { word ->
                            val inDeck = word.russian in deckWordKeys
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleWord(word.russian, inDeck) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = inDeck,
                                    onCheckedChange = { onToggleWord(word.russian, inDeck) }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(word.russian, fontWeight = FontWeight.Medium)
                                    Text(
                                        word.translation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Hazırdır") }
        }
    )
}

@Composable
internal fun AddWordToDeckDialog(
    word: Word,
    decks: List<FlashcardDeck>,
    onToggleWordInDeck: (deckId: Long, wordRussian: String, currentlyInDeck: Boolean) -> Unit,
    onCreateDeckWithWord: (name: String, wordRussian: String) -> Unit,
    onDismiss: () -> Unit
) {
    var newDeckName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("«${word.russian}» dəstə əlavə et", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (decks.isEmpty()) {
                    Text(
                        text = "Hələ kart dəstin yoxdur. Aşağıda yeni dəst yaradıb bu sözü dərhal əlavə edə bilərsən.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    decks.forEach { deck ->
                        val inDeck = deck.words.any { it.russian == word.russian }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleWordInDeck(deck.id, word.russian, inDeck) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = inDeck,
                                onCheckedChange = { onToggleWordInDeck(deck.id, word.russian, inDeck) },
                                modifier = Modifier.testTag("add_word_deck_checkbox_${deck.id}")
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(deck.name, fontWeight = FontWeight.Medium)
                                Text(
                                    "${deck.words.size} söz",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = newDeckName,
                    onValueChange = { newDeckName = it },
                    label = { Text("Və ya yeni dəst adı") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_new_deck_name_input")
                )
                OutlinedButton(
                    onClick = {
                        if (newDeckName.isNotBlank()) {
                            onCreateDeckWithWord(newDeckName, word.russian)
                            newDeckName = ""
                        }
                    },
                    enabled = newDeckName.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("quick_new_deck_create_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Yeni dəstə əlavə et")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Bağla") }
        }
    )
}

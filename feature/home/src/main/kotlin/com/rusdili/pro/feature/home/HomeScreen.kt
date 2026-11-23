package com.rusdili.pro.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.common.AppConstants
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
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val current = state) {
        UiState.Loading -> CircularProgressIndicator(Modifier.padding(32.dp))
        is UiState.Error -> Text(current.message, Modifier.padding(24.dp))
        is UiState.Success -> HomeContent(
            current.data, onBook, onPractice, onNews
        )
    }
}

@Composable
private fun HomeContent(
    data: HomeData,
    onBook: () -> Unit,
    onPractice: () -> Unit,
    onNews: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("RusDili Pro", style = MaterialTheme.typography.headlineMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = "Ardıcıl günlər",
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(" ${data.streak}")
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Bugünkü hədəf", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                ProgressBar(data.xpToday / AppConstants.DAILY_GOAL_XP.toFloat())
                Text("Bu gün ${data.xpToday} / ${AppConstants.DAILY_GOAL_XP} XP", Modifier.padding(top = 8.dp))
            }
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Davam et", style = MaterialTheme.typography.titleMedium)
                Text(data.nextLesson)
                AppButton("Davam et", onBook)
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
        AppButton("Hamısını gör", onNews)
        AppButton("Gündəlik məşqə başla", onPractice, Modifier.padding(bottom = 12.dp))
    }
}

package com.rusdili.pro.feature.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rusdili.pro.core.common.UiState
import com.rusdili.pro.core.ui.NewsCard

@Composable
fun NewsScreen(viewModel: NewsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val current = state) {
        UiState.Loading -> CircularProgressIndicator(Modifier.padding(32.dp))
        is UiState.Error -> Text(current.message, Modifier.padding(24.dp))
        is UiState.Success -> NewsContent(current.data, viewModel)
    }
}

@Composable
private fun NewsContent(data: NewsData, viewModel: NewsViewModel) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Xəbərlər", style = MaterialTheme.typography.headlineMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { viewModel.selectTab(NewsTab.TRUMP) },
                modifier = Modifier.weight(1f)
            ) { Text("Tramp") }
            OutlinedButton(
                onClick = { viewModel.selectTab(NewsTab.WORLD) },
                modifier = Modifier.weight(1f)
            ) { Text("Dünya") }
        }
        if (data.tab == NewsTab.WORLD && data.refreshing) CircularProgressIndicator()
        if (data.error != null) {
            Text(data.error, color = MaterialTheme.colorScheme.error)
            if (data.tab == NewsTab.WORLD) {
                OutlinedButton(onClick = viewModel::refreshWorld) { Text("Yenidən yoxla") }
            }
        }
        if (data.items.isEmpty() && !data.refreshing) Text("Hələlik yeni xəbər yoxdur")
        data.items.forEach { item -> NewsCard(item) }
        if (data.tab == NewsTab.WORLD && data.items.isNotEmpty()) {
            OutlinedButton(onClick = viewModel::refreshWorld) { Text("Xəbərləri yenilə") }
        }
    }
}

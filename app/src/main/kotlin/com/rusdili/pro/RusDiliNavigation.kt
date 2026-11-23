package com.rusdili.pro

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.rusdili.pro.feature.book.BookScreen
import com.rusdili.pro.feature.dictionary.DictionaryScreen
import com.rusdili.pro.feature.home.HomeScreen
import com.rusdili.pro.feature.news.NewsScreen
import com.rusdili.pro.feature.quiz.PracticeScreen

private data class TabDestination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    TabDestination("home", "Ev", Icons.Default.Home),
    TabDestination("book", "Kitab", Icons.Default.Book),
    TabDestination("practice", "Məşq", Icons.Default.School),
    TabDestination("dictionary", "Lüğət", Icons.Default.Language),
    TabDestination("news", "Xəbərlər", Icons.Default.Newspaper)
)

@Composable
fun RusDiliNavigation() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentDestination = backStack?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(
                                tab.route,
                                navOptions {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            )
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(
                    onBook = { navController.navigate("book") },
                    onPractice = { navController.navigate("practice") },
                    onNews = { navController.navigate("news") }
                )
            }
            composable("book") { BookScreen() }
            composable("practice") { PracticeScreen() }
            composable("dictionary") { DictionaryScreen() }
            composable("news") { NewsScreen() }
        }
    }
}

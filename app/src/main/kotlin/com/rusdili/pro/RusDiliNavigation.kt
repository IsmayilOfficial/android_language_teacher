package com.rusdili.pro

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.rusdili.pro.core.ui.AppLogo
import com.rusdili.pro.feature.book.BookScreen
import com.rusdili.pro.feature.dictionary.DictionaryScreen
import com.rusdili.pro.feature.home.HomeScreen
import com.rusdili.pro.feature.news.NewsScreen
import com.rusdili.pro.feature.quiz.PracticeScreen

private data class TabDestination(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val tabs = listOf(
    TabDestination("home", "Ev", Icons.Default.Home),
    TabDestination("book", "Kitab", Icons.Default.Book),
    TabDestination("practice", "Məşq", Icons.Default.School),
    TabDestination("dictionary", "Lüğət", Icons.Default.Language),
    TabDestination("news", "Xəbərlər", Icons.Default.Newspaper)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RusDiliNavigation(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentDestination = backStack?.destination
    val currentTab = tabs.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppLogo(
                            modifier = Modifier.testTag("global_app_logo"),
                            size = 32.dp
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "LL - Learn Language",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            currentTab?.let { tab ->
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                },
                actions = {
                    ThemeTogglePill(
                        isDarkTheme = isDarkTheme,
                        onToggle = onToggleTheme,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
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
                        label = { Text(tab.label) },
                        modifier = Modifier.testTag("nav_tab_${tab.route}")
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

@Composable
private fun ThemeTogglePill(
    isDarkTheme: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lightBg by animateColorAsState(
        targetValue = if (!isDarkTheme) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "light_bg"
    )
    val lightTint by animateColorAsState(
        targetValue = if (!isDarkTheme) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "light_tint"
    )
    val darkBg by animateColorAsState(
        targetValue = if (isDarkTheme) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "dark_bg"
    )
    val darkTint by animateColorAsState(
        targetValue = if (isDarkTheme) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "dark_tint"
    )
    val description = if (isDarkTheme) {
        "İşıqlı temaya keç"
    } else {
        "Tünd temaya keç"
    }

    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .semantics {
                role = Role.Switch
                contentDescription = description
                stateDescription = if (isDarkTheme) "Tünd tema aktivdir" else "İşıqlı tema aktivdir"
            }
            .testTag("global_theme_toggle")
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(lightBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LightMode,
                    contentDescription = null,
                    tint = lightTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(darkBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = darkTint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

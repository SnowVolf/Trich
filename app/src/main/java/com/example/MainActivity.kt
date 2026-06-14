package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.navigation.Boards
import com.example.navigation.Drafts
import com.example.navigation.History
import com.example.navigation.Favorites
import com.example.navigation.NavigationState
import com.example.navigation.Navigator
import com.example.navigation.NewThreadDest
import com.example.navigation.Settings
import com.example.navigation.ThreadDest
import com.example.navigation.ThreadListDest
import com.example.navigation.rememberNavigationState
import com.example.navigation.toEntries
import com.example.ui.boards.BoardsScreen
import com.example.ui.drafts.DraftsScreen
import com.example.ui.history.HistoryScreen
import com.example.ui.favorites.FavoritesScreen
import com.example.ui.settings.SettingsScreen
import com.example.uikit.theme.MyApplicationTheme
import com.example.ui.thread.ThreadScreen
import com.example.ui.threadlist.ThreadListScreen
import com.example.ui.newthread.NewThreadScreen
import com.example.ui.settings.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.navigation.GalleryDest
import com.example.worker.WorkManagerHelper

/**
 * Главная точка входа в приложение (UI слой).
 * Настраивает тему, DI (через Koin), навигацию и воркеры.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = koinViewModel()
            val state by settingsViewModel.state.collectAsState()
            
            val context = LocalContext.current
            LaunchedEffect(state.backgroundCheckFavorites) {
                WorkManagerHelper.scheduleFavoritesWorker(context, state.backgroundCheckFavorites)
            }
            
            MyApplicationTheme(
                darkTheme = state.isDarkTheme,
                dynamicColor = false
            ) {
                AppNavigation(intent)
            }
        }
    }

    internal var newIntentCallback: ((android.content.Intent) -> Unit)? = null

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        newIntentCallback?.invoke(intent)
    }
}

/**
 * Определяет граф навигации для всего приложения и обрабатывает deeplinks/внешние интент-запросы.
 * @param initialIntent Начальный Intent с которым было запущено Activity (для deeplink парсинга).
 */
@Composable
fun AppNavigation(initialIntent: android.content.Intent?) {
    val navigationState = rememberNavigationState(
        startRoute = Boards,
        topLevelRoutes = setOf(Boards, History, Drafts, Favorites, Settings)
    )
    val navigator = remember { Navigator(navigationState) }

    val context = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.runtime.DisposableEffect(context) {
        val activity = context as MainActivity
        val callback: (android.content.Intent) -> Unit = { intent ->
            if (intent.action == android.content.Intent.ACTION_VIEW) {
                val url = intent.dataString
                if (url != null) {
                    val boardRegex = Regex(".*/([a-z0-9]+)/?$")
                    val threadRegex = Regex(".*/([a-z0-9]+)/res/([0-9]+)\\.html.*")
                    val threadMatch = threadRegex.find(url)
                    val boardMatch = boardRegex.find(url)
                    
                    if (threadMatch != null) {
                        val board = threadMatch.groupValues[1]
                        val thread = threadMatch.groupValues[2].toIntOrNull()
                        val uri = android.net.Uri.parse(url)
                        val scrollToPost = uri.getQueryParameter("scrollToPost")?.toIntOrNull()
                        if (thread != null) {
                            navigator.navigate(ThreadDest(board, thread, scrollToPost))
                        }
                    } else if (boardMatch != null) {
                        val board = boardMatch.groupValues[1]
                        navigator.navigate(ThreadListDest(board))
                    }
                }
            }
        }
        activity.newIntentCallback = callback
        if (initialIntent != null) {
            callback(initialIntent)
        }
        onDispose {
            activity.newIntentCallback = null
        }
    }
    
    val entryProvider = entryProvider {
        entry<Boards> { 
            BoardsScreen(
                onNavigateToBoard = { board ->
                    navigator.navigate(ThreadListDest(board))
                },
                onHistoryClick = { navigator.navigate(History) },
                onDraftsClick = { navigator.navigate(Drafts) },
                onFavoritesClick = { navigator.navigate(Favorites) },
                onSettingsClick = { navigator.navigate(Settings) }
            )
        }
        entry<History> {
            HistoryScreen(
                onNavigateToThread = { board, thread ->
                    navigator.navigate(ThreadDest(board, thread))
                },
                onBoardsClick = { navigator.navigate(Boards) },
                onDraftsClick = { navigator.navigate(Drafts) },
                onFavoritesClick = { navigator.navigate(Favorites) },
                onSettingsClick = { navigator.navigate(Settings) }
            )
        }
        entry<Drafts> {
            DraftsScreen(
                onNavigateToThread = { board, thread ->
                    navigator.navigate(ThreadDest(board, thread))
                },
                onBoardsClick = { navigator.navigate(Boards) },
                onHistoryClick = { navigator.navigate(History) },
                onFavoritesClick = { navigator.navigate(Favorites) },
                onSettingsClick = { navigator.navigate(Settings) }
            )
        }
        entry<Favorites> {
            FavoritesScreen(
                onNavigateToThread = { board, thread ->
                    navigator.navigate(ThreadDest(board, thread))
                },
                onBackClick = { navigator.goBack() },
                onBoardsClick = { navigator.navigate(Boards) },
                onHistoryClick = { navigator.navigate(History) },
                onDraftsClick = { navigator.navigate(Drafts) },
                onSettingsClick = { navigator.navigate(Settings) }
            )
        }
        entry<Settings> {
            SettingsScreen(
                onBackClick = { navigator.goBack() },
                onBoardsClick = { navigator.navigate(Boards) },
                onHistoryClick = { navigator.navigate(History) },
                onFavoritesClick = { navigator.navigate(Favorites) },
                onDraftsClick = { navigator.navigate(Drafts) }
            )
        }
        entry<ThreadListDest> { dest ->
            ThreadListScreen(
                board = dest.board,
                onBackClick = { navigator.goBack() },
                onNavigateToThread = { board, thread ->
                    navigator.navigate(ThreadDest(board, thread))
                },
                onHistoryClick = { navigator.navigate(History) },
                onDraftsClick = { navigator.navigate(Drafts) },
                onFavoritesClick = { navigator.navigate(Favorites) },
                onSettingsClick = { navigator.navigate(Settings) },
                onNewThreadClick = { board -> navigator.navigate(NewThreadDest(board)) }
            )
        }
        entry<NewThreadDest> { dest ->
            NewThreadScreen(
                board = dest.board,
                onBackClick = { navigator.goBack() }
            )
        }
        entry<ThreadDest> { dest ->
            ThreadScreen(
                board = dest.board,
                threadNum = dest.threadNum,
                scrollToPost = dest.scrollToPost,
                onBackClick = { navigator.goBack() },
                onBoardsClick = { navigator.navigate(Boards) },
                onHistoryClick = { navigator.navigate(History) },
                onDraftsClick = { navigator.navigate(Drafts) },
                onFavoritesClick = { navigator.navigate(Favorites) },
                onSettingsClick = { navigator.navigate(Settings) },
                onNavigateToGallery = { urls, index -> navigator.navigate(GalleryDest(urls, index)) },
                onNavigateToBoard = { targetBoard -> navigator.navigate(ThreadListDest(targetBoard)) }
            )
        }
        entry<GalleryDest> { dest ->
            com.example.ui.gallery.GalleryScreen(
                urls = dest.urls,
                initialIndex = dest.initialIndex,
                onBackClick = { navigator.goBack() }
            )
        }
    }
    
    NavDisplay(
        entries = navigationState.toEntries(entryProvider),
        onBack = { navigator.goBack() },
        transitionSpec = {
            val initialRoute = initialState.key
            val targetRoute = targetState.key

            when (initialRoute) {
                is ThreadListDest if targetRoute is ThreadDest -> {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(400)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { -it / 3 },
                        animationSpec = tween(400)
                    )
                }

                is ThreadDest if targetRoute is ThreadListDest -> {
                    slideInHorizontally(
                        initialOffsetX = { -it / 3 },
                        animationSpec = tween(400)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(400)
                    )
                }

                is Boards if targetRoute is ThreadListDest -> {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(400)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { -it / 3 },
                        animationSpec = tween(400)
                    )
                }

                is ThreadListDest if targetRoute is Boards -> {
                    slideInHorizontally(
                        initialOffsetX = { -it / 3 },
                        animationSpec = tween(400)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(400)
                    )
                }

                else -> {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                }
            }
        }
    )
}

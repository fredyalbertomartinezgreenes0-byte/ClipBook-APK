package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.local.DatabaseSeeder
import com.example.data.local.NexaDatabase
import com.example.data.local.entity.StoryEntity
import com.example.data.repository.NexaRepository
import com.example.ui.components.NexaBottomBar
import com.example.ui.components.NexaDrawer
import com.example.ui.components.NexaTopBar
import com.example.ui.navigation.NexaScreen
import com.example.ui.screens.*
import com.example.ui.theme.NexaBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexaApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val database = remember { NexaDatabase.getInstance(context) }
    val repository = remember { NexaRepository(database.socialDao()) }

    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        DatabaseSeeder.seedIfEmpty(database.socialDao())
        isInitialized = true
    }

    if (!isInitialized) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NexaBackground),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator(color = com.example.ui.theme.NexaCyan)
        }
        return
    }

    val currentUser by repository.currentUser.collectAsState(initial = null)
    val currentVerification by repository.currentUserVerification.collectAsState(initial = null)
    val notifications by if (currentUser != null) {
        repository.getNotifications(currentUser!!.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }
    val unreadNotifs = notifications.count { !it.isRead }

    var currentScreen by remember { mutableStateOf(NexaScreen.FEED) }
    var selectedProfileUserId by remember { mutableStateOf<Long?>(null) }
    var selectedChatUserId by remember { mutableStateOf<Long?>(null) }
    var selectedStory by remember { mutableStateOf<StoryEntity?>(null) }
    var showCreateStoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            // Auto login as creator account (Fredy)
            repository.switchUser(1L)
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Back button handling
    BackHandler(enabled = currentScreen != NexaScreen.FEED && currentScreen != NexaScreen.AUTH) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            currentScreen = NexaScreen.FEED
        }
    }

    // Modal Drawer wrapper
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentScreen != NexaScreen.AUTH && currentScreen != NexaScreen.STORY_VIEWER,
        drawerContent = {
            NexaDrawer(
                currentUser = currentUser,
                currentVerification = currentVerification,
                onNavigate = { screen ->
                    currentScreen = screen
                },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                },
                onLogout = {
                    coroutineScope.launch { drawerState.close() }
                    currentScreen = NexaScreen.AUTH
                }
            )
        }
    ) {
        val showTopAndBottomBars = currentScreen in listOf(
            NexaScreen.FEED,
            NexaScreen.FRIENDS,
            NexaScreen.NOTIFICATIONS,
            NexaScreen.PROFILE
        )

        Scaffold(
            topBar = {
                if (showTopAndBottomBars) {
                    NexaTopBar(
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onOpenSearch = {
                            currentScreen = NexaScreen.SEARCH
                        },
                        onOpenMessages = {
                            currentScreen = NexaScreen.MESSAGES
                        },
                        userCoins = currentUser?.coins ?: 1000L,
                        onOpenWallet = {
                            currentScreen = NexaScreen.WALLET
                        }
                    )
                }
            },
            bottomBar = {
                if (showTopAndBottomBars) {
                    NexaBottomBar(
                        currentScreen = currentScreen,
                        unreadNotificationsCount = unreadNotifs,
                        onNavigate = { screen ->
                            if (screen == NexaScreen.PROFILE) {
                                selectedProfileUserId = currentUser?.id ?: 1L
                            }
                            currentScreen = screen
                        }
                    )
                }
            },
            containerColor = NexaBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    NexaScreen.FEED -> {
                        FeedScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onOpenCreatePost = { currentScreen = NexaScreen.CREATE_POST },
                            onOpenCreateStory = { showCreateStoryDialog = true },
                            onOpenStory = { story ->
                                selectedStory = story
                                currentScreen = NexaScreen.STORY_VIEWER
                            },
                            onOpenProfile = { targetId ->
                                selectedProfileUserId = targetId
                                currentScreen = NexaScreen.PROFILE
                            }
                        )
                    }
                    NexaScreen.FRIENDS -> {
                        FriendsScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onOpenProfile = { targetId ->
                                selectedProfileUserId = targetId
                                currentScreen = NexaScreen.PROFILE
                            }
                        )
                    }
                    NexaScreen.CREATE_POST -> {
                        CreatePostScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onBack = { currentScreen = NexaScreen.FEED },
                            onPostCreated = { currentScreen = NexaScreen.FEED }
                        )
                    }
                    NexaScreen.NOTIFICATIONS -> {
                        NotificationsScreen(
                            repository = repository,
                            currentUser = currentUser
                        )
                    }
                    NexaScreen.PROFILE -> {
                        ProfileScreen(
                            repository = repository,
                            targetUserId = selectedProfileUserId ?: currentUser?.id ?: 1L,
                            currentUserId = currentUser?.id ?: 1L,
                            onRequestVerification = { currentScreen = NexaScreen.VERIFY_PURCHASE }
                        )
                    }
                    NexaScreen.MESSAGES -> {
                        MessagesScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onOpenConversation = { otherId ->
                                selectedChatUserId = otherId
                                currentScreen = NexaScreen.CHAT_DETAIL
                            },
                            onBack = { currentScreen = NexaScreen.FEED }
                        )
                    }
                    NexaScreen.CHAT_DETAIL -> {
                        ChatDetailScreen(
                            repository = repository,
                            currentUser = currentUser,
                            otherUserId = selectedChatUserId ?: 2L,
                            onBack = { currentScreen = NexaScreen.MESSAGES }
                        )
                    }
                    NexaScreen.GROUPS -> {
                        GroupsScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onBack = { currentScreen = NexaScreen.FEED }
                        )
                    }
                    NexaScreen.PAGES -> {
                        PagesScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onBack = { currentScreen = NexaScreen.FEED }
                        )
                    }
                    NexaScreen.SEARCH -> {
                        SearchScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onOpenProfile = { targetId ->
                                selectedProfileUserId = targetId
                                currentScreen = NexaScreen.PROFILE
                            },
                            onBack = { currentScreen = NexaScreen.FEED }
                        )
                    }
                    NexaScreen.GIFT_CODE -> {
                        GiftCodeScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onBack = { currentScreen = NexaScreen.FEED }
                        )
                    }
                    NexaScreen.VERIFY_PURCHASE -> {
                        VerifyPurchaseScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onBack = { currentScreen = NexaScreen.FEED },
                            onSuccess = {
                                selectedProfileUserId = currentUser?.id ?: 1L
                                currentScreen = NexaScreen.PROFILE
                            }
                        )
                    }
                    NexaScreen.ADMIN_PANEL -> {
                        AdminPanelScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onBack = { currentScreen = NexaScreen.FEED }
                        )
                    }
                    NexaScreen.ACCOUNT_MANAGEMENT -> {
                        AccountManagementScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onBack = { currentScreen = NexaScreen.FEED }
                        )
                    }
                    NexaScreen.MOD_MENU -> {
                        ModMenuScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onBack = { currentScreen = NexaScreen.FEED }
                        )
                    }
                    NexaScreen.WALLET -> {
                        WalletScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onNavigate = { screen -> currentScreen = screen },
                            onBack = { currentScreen = NexaScreen.FEED }
                        )
                    }
                    NexaScreen.SETTINGS -> {
                        SettingsScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onBack = { currentScreen = NexaScreen.FEED },
                            onLogout = { currentScreen = NexaScreen.AUTH }
                        )
                    }
                    NexaScreen.STORY_VIEWER -> {
                        selectedStory?.let { story ->
                            StoryViewerScreen(
                                story = story,
                                currentUser = currentUser,
                                repository = repository,
                                onClose = { currentScreen = NexaScreen.FEED }
                            )
                        } ?: run {
                            currentScreen = NexaScreen.FEED
                        }
                    }
                    NexaScreen.AUTH -> {
                        AuthScreen(
                            repository = repository,
                            onAuthSuccess = {
                                currentScreen = NexaScreen.FEED
                            }
                        )
                    }
                    else -> {
                        FeedScreen(
                            repository = repository,
                            currentUser = currentUser,
                            onOpenCreatePost = { currentScreen = NexaScreen.CREATE_POST },
                            onOpenCreateStory = { showCreateStoryDialog = true },
                            onOpenStory = { story ->
                                selectedStory = story
                                currentScreen = NexaScreen.STORY_VIEWER
                            },
                            onOpenProfile = { targetId ->
                                selectedProfileUserId = targetId
                                currentScreen = NexaScreen.PROFILE
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreateStoryDialog) {
        CreateStoryDialog(
            currentUser = currentUser,
            repository = repository,
            onDismiss = { showCreateStoryDialog = false },
            onStoryCreated = {
                showCreateStoryDialog = false
                currentScreen = NexaScreen.FEED
            }
        )
    }
}

package com.example.ulink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Simple enum to track which screen is currently showing
enum class Screen { SPLASH, LOGIN, HOME, INBOX, MAIL_DETAILS, NEW_MAIL, NAVIGATION, PROFILE, SENT, REPLY_MAIL, LEAVE_BALANCE }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Which screen is currently visible — starts on Splash
            var currentScreen by remember { mutableStateOf(Screen.SPLASH) }

            // Remembers which screen we were on before opening Navigation,
            // so its close button can send us back to the right place.
            var previousScreen by remember { mutableStateOf(Screen.HOME) }

            // Holds the mail that was tapped, so MailDetailsScreen knows which one to show
            var selectedMail by remember { mutableStateOf<MailItem?>(null) }

            // ID of the currently logged-in user — drives which leave balance data is shown.
            // TODO: replace this default with whatever LoginScreen actually returns once
            // it exposes a real user identifier from your auth flow.
            var loggedInUserId by remember { mutableStateOf("james.perera") }

            when (currentScreen) {

                Screen.SPLASH -> {
                    SplashScreen(
                        onTimeout = {
                            currentScreen = Screen.LOGIN
                        }
                    )
                }

                Screen.LOGIN -> {
                    LoginScreen(
                        onLoginSuccess = {
                            currentScreen = Screen.HOME
                        }
                    )
                }

                Screen.HOME -> {
                    HomeScreen(
                        onNavigateToInbox = {
                            currentScreen = Screen.INBOX
                        },
                        onNavigateToNavigation = {
                            previousScreen = Screen.HOME
                            currentScreen = Screen.NAVIGATION
                        },
                        onNavigateToProfile = {
                            currentScreen = Screen.PROFILE
                        }
                    )
                }

                Screen.INBOX -> {
                    InboxScreen(
                        onMailClick = { mail ->
                            selectedMail = mail
                            currentScreen = Screen.MAIL_DETAILS
                        },
                        onNewMailClick = {
                            currentScreen = Screen.NEW_MAIL
                        },
                        onProfileClick = {
                            currentScreen = Screen.PROFILE
                        },
                        onNavigateToHome = {
                            currentScreen = Screen.HOME
                        },
                        onNavigateToNavigation = {
                            previousScreen = Screen.INBOX
                            currentScreen = Screen.NAVIGATION
                        }
                    )
                }

                Screen.MAIL_DETAILS -> {
                    // selectedMail should always be set by the time we reach here,
                    // but fall back to the first sample mail just in case
                    MailDetailsScreen(
                        mail = selectedMail ?: sampleMails.first(),
                        onBackClick = {
                            currentScreen = Screen.INBOX
                        },
                        onNewMailClick = {
                            currentScreen = Screen.NEW_MAIL
                        },
                        onReplyClick = {
                            currentScreen = Screen.REPLY_MAIL
                        },
                        onProfileClick = {
                            currentScreen = Screen.PROFILE
                        }
                    )
                }

                Screen.NEW_MAIL -> {
                    NewMailScreen(
                        onBackClick = {
                            currentScreen = Screen.INBOX
                        },
                        selectedTab = Tab.INBOX,
                        onTabSelected = { tab ->
                            when (tab) {
                                Tab.HOME -> currentScreen = Screen.HOME
                                Tab.INBOX -> currentScreen = Screen.INBOX
                                Tab.APPS -> {
                                    previousScreen = Screen.NEW_MAIL
                                    currentScreen = Screen.NAVIGATION
                                }
                                Tab.CHATS -> {
                                    // "Leave" tab
                                    currentScreen = Screen.LEAVE_BALANCE
                                }
                                Tab.NOTIFICATIONS -> {
                                    // "Profile" tab
                                    currentScreen = Screen.PROFILE
                                }
                            }
                        }
                    )
                }

                Screen.NAVIGATION -> {
                    // The drawer-style screen defined in App.kt.
                    // Closing it always returns to Home.
                    App(
                        onClose = {
                            currentScreen = Screen.HOME
                        }
                    )
                }

                Screen.PROFILE -> {
                    ProfileScreen(
                        selectedTab = Tab.NOTIFICATIONS,
                        onTabSelected = { tab ->
                            when (tab) {
                                Tab.HOME -> currentScreen = Screen.HOME
                                Tab.INBOX -> currentScreen = Screen.INBOX
                                Tab.APPS -> {
                                    previousScreen = Screen.PROFILE
                                    currentScreen = Screen.NAVIGATION
                                }
                                Tab.CHATS -> {
                                    // "Leave" tab
                                    currentScreen = Screen.LEAVE_BALANCE
                                }
                                Tab.NOTIFICATIONS -> {
                                    // Already on Profile
                                }
                            }
                        },
                        onSignOutConfirmed = {
                            currentScreen = Screen.LOGIN
                        }
                    )
                }

                Screen.LEAVE_BALANCE -> {
                    LeaveBalanceScreen(
                        loggedInUserId = loggedInUserId,
                        onMenuClick = {
                            previousScreen = Screen.LEAVE_BALANCE
                            currentScreen = Screen.NAVIGATION
                        },
                        onLeaveHistoryClick = {
                            // TODO: point this at your Leave History screen once it exists
                        },
                        onNavigateToHome = {
                            currentScreen = Screen.HOME
                        },
                        onNavigateToInbox = {
                            currentScreen = Screen.INBOX
                        },
                        onNavigateToProfile = {
                            currentScreen = Screen.PROFILE
                        },
                        onNavigateToApps = {
                            previousScreen = Screen.LEAVE_BALANCE
                            currentScreen = Screen.NAVIGATION
                        }
                    )
                }

                Screen.REPLY_MAIL -> {
                    // selectedMail should still hold the original mail from
                    // when MAIL_DETAILS was opened, but fall back just in case
                    ReplyMailScreen(
                        mail = selectedMail ?: sampleMails.first(),
                        onBackClick = {
                            currentScreen = Screen.MAIL_DETAILS
                        },
                        onSendClick = { replyText ->
                            // Record the reply in the shared Sent store (Sent.kt),
                            // then take the user to the Sent screen to see it.
                            SentMailStore.addMail(
                                SentMailItem(
                                    to = selectedMail?.sender ?: "",
                                    subject = "Re: ${selectedMail?.subject ?: ""}",
                                    message = replyText,
                                    time = SimpleDateFormat("h.mma", Locale.US)
                                        .format(Date())
                                        .lowercase(Locale.US)
                                )
                            )
                            currentScreen = Screen.SENT
                        }
                    )
                }

                Screen.SENT -> {
                    SentScreen(
                        onNavigateToHome = {
                            currentScreen = Screen.HOME
                        },
                        onNavigateToInbox = {
                            currentScreen = Screen.INBOX
                        },
                        onNavigateToNavigation = {
                            previousScreen = Screen.SENT
                            currentScreen = Screen.NAVIGATION
                        }
                    )
                }
            }
        }
    }
}
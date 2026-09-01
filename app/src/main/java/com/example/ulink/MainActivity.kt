package com.example.ulink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Simple enum to track which screen is currently showing
enum class Screen { SPLASH, LOGIN, HOME, INBOX, MAIL_DETAILS, NEW_MAIL, NAVIGATION, PROFILE, SENT, REPLY_MAIL }

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
                    // NewMailScreen's current signature only takes onBackClick —
                    // it no longer exposes onNavigateToHome or onMailSent, so
                    // sending a mail here does NOT navigate to Sent anymore.
                    NewMailScreen(
                        onBackClick = {
                            currentScreen = Screen.INBOX
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
                    // ProfileScreen's current signature no longer exposes
                    // onNavigateToNavigation, so its center logo tap doesn't
                    // navigate anywhere yet either (see note below).
                    ProfileScreen(
                        selectedTab = Tab.HOME,
                        onTabSelected = { tab ->
                            when (tab) {
                                Tab.HOME -> currentScreen = Screen.HOME
                                Tab.INBOX -> currentScreen = Screen.INBOX
                                Tab.CHATS -> {
                                    // TODO: point this at your actual chats screen once it exists
                                    currentScreen = Screen.HOME
                                }
                                Tab.NOTIFICATIONS -> {
                                    // TODO: point this at your actual notifications screen once it exists
                                    currentScreen = Screen.HOME
                                }
                            }
                        },
                        onSignOutConfirmed = {
                            currentScreen = Screen.LOGIN
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
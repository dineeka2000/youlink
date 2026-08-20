package com.example.ulink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

// Simple enum to track which screen is currently showing
enum class Screen { SPLASH, LOGIN, HOME, INBOX, MAIL_DETAILS, NEW_MAIL, NAVIGATION }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Which screen is currently visible — starts on Splash
            var currentScreen by remember { mutableStateOf(Screen.SPLASH) }

            // Remembers which screen we were on before opening Navigation,
            // so its close button can send us back to the right place
            // (Home or Inbox) instead of always going to Home.
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
                        onNavigateToHome = {
                            currentScreen = Screen.HOME
                        }
                    )
                }

                Screen.NEW_MAIL -> {
                    NewMailScreen(
                        onBackClick = {
                            currentScreen = Screen.INBOX
                        },
                        onNavigateToHome = {
                            currentScreen = Screen.HOME
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
            }
        }
    }
}
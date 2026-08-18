package com.example.ulink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

// Simple enum to track which screen is currently showing
enum class Screen { INBOX, MAIL_DETAILS, NEW_MAIL }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Which screen is currently visible — starts on Inbox
            var currentScreen by remember { mutableStateOf(Screen.INBOX) }

            // Holds the mail that was tapped, so MailDetailsScreen knows which one to show
            var selectedMail by remember { mutableStateOf<MailItem?>(null) }

            when (currentScreen) {

                Screen.INBOX -> {
                    InboxScreen(
                        onMailClick = { mail ->
                            selectedMail = mail
                            currentScreen = Screen.MAIL_DETAILS
                        },
                        onNewMailClick = {
                            currentScreen = Screen.NEW_MAIL
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
                        }
                    )
                }

                Screen.NEW_MAIL -> {
                    NewMailScreen(
                        onBackClick = {
                            currentScreen = Screen.INBOX
                        }
                    )
                }    }
        }
    }
}
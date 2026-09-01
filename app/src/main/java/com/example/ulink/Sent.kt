package com.example.ulink


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// One sent mail's data
data class SentMailItem(
    val to: String,
    val subject: String,
    val message: String,
    val time: String
)

// Shared in-memory store: NewMailScreen adds to this when the user taps Send,
// and SentScreen reads from it to display everything sent so far.
// NOTE: this only lives as long as the app process — it resets on app restart.
// Swap this out for a real database (Room) or backend call later if you need
// the sent list to persist across app restarts.
object SentMailStore {
    val sentMails = mutableStateListOf<SentMailItem>()

    fun addMail(mail: SentMailItem) {
        // Newest sent mail appears at the top
        sentMails.add(0, mail)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToInbox: () -> Unit = {},
    onNavigateToNavigation: () -> Unit = {}
) {
    // No dedicated "Sent" tab exists in the bottom bar yet, so this just
    // tracks Home/Inbox taps like the other screens do.
    var selectedTab by remember { mutableStateOf(Tab.INBOX) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sent", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF14508C))
            )
        },
        bottomBar = {
            CustomTabBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
//                    when (tab) {
//                        Tab.HOME -> onNavigateToHome()
//                        Tab.INBOX -> onNavigateToInbox()
//                    }
                },
                onCenterButtonClick = onNavigateToNavigation
            )
        }
    ) { padding ->
        if (SentMailStore.sentMails.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No sent mail yet", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(SentMailStore.sentMails) { mail ->
                    SentMailRow(mail)
                }
            }
        }
    }
}

@Composable
private fun SentMailRow(mail: SentMailItem) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF14508C)),
                contentAlignment = Alignment.Center
            ) {
                Text("To", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mail.to.ifBlank { "(no recipient)" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = mail.subject.ifBlank { "(no subject)" },
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
                Text(
                    text = mail.message,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(mail.time, color = Color.Gray, fontSize = 11.sp)
        }
        HorizontalDivider(color = Color(0xFFEFEFEF))
    }
}
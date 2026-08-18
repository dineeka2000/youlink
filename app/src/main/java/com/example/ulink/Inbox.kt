// ---------------- Inbox.kt ----------------
package com.example.ulink

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.layout.ContentScale
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween

// One mail row's data
data class MailItem(
    val sender: String,
    val initials: String,
    val avatarColor: Color,
    val category: String,
    val subject: String,
    val preview: String,
    val time: String,
    val isRead: Boolean
)

// Sample mail data — replace with real data from your backend/database later
val sampleMails = listOf(
    MailItem("Airline Insight", "AI", Color(0xFF14508C), "IT", "Airline Insight", "Enjoy faster access, better features, and...", "8.20am", isRead = false),
    MailItem("Airline Insight", "EC", Color(0xFF8B2E2E), "HR", "Airline Insight", "Enjoy faster access, better features, and...", "8.20am", isRead = false),
    MailItem("Airline Insight", "EC", Color(0xFFC96A6A), "HR", "Airline Insight", "Enjoy faster access, better features, and...", "8.20am", isRead = true),
    MailItem("Sarah Underwood", "SU", Color(0xFF7B4FA0), "SVN", "Sarah Underwood", "Enjoy faster access, better features, and...", "8.20am", isRead = true),
    MailItem("IT Security", "IS", Color(0xFF4CAF50), "IT", "IT Security", "Our policies have been updated. Kindly take...", "8.20am", isRead = true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    onMailClick: (MailItem) -> Unit = {},
    onNewMailClick: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedTab by remember { mutableStateOf(Tab.INBOX) }

    // Controls whether the New Mail button shows text or just the icon
    var fabExpanded by remember { mutableStateOf(true) }

    // After 2 seconds, collapse the button to icon-only
    LaunchedEffect(Unit) {
        delay(2000)
        fabExpanded = false
    }

    val filteredMails = if (selectedCategory == "All") {
        sampleMails
    } else {
        sampleMails.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inbox", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: handle menu click */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu),
                            contentDescription = "Menu",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(width = 50.dp, height = 50.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 35.dp,
                                    bottomStart = 35.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            )
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile photo",
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF14508C))
            )
        },
        bottomBar = {
            CustomTabBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onCenterButtonClick = { /* TODO: handle apps/logo click */ }
            )
        },
        // New Mail button: expanded (with text) first, then fades to icon-only after 2s
        floatingActionButton = {
            Crossfade(
                targetState = fabExpanded,
                animationSpec = tween(durationMillis = 500)
            ) { expanded ->
                if (expanded) {
                    ExtendedFloatingActionButton(
                        onClick = onNewMailClick,
                        containerColor = Color(0xFF14508C),
                        contentColor = Color.White,
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.newmail),
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        text = { Text("New Mail") }
                    )
                } else {
                    FloatingActionButton(
                        onClick = onNewMailClick,
                        containerColor = Color(0xFF14508C),
                        contentColor = Color.White
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.newmail),
                            contentDescription = "New Mail",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "HR", "IT", "SVN").forEach { category ->
                    CategoryTab(
                        label = category,
                        isSelected = category == selectedCategory,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filteredMails) { mail ->
                    MailRow(mail = mail, onClick = { onMailClick(mail) })
                }
            }
        }
    }
}

@Composable
private fun CategoryTab(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Color(0xFF176EBC) else Color(0xFFEFEFEF))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.DarkGray,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MailRow(mail: MailItem, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (!mail.isRead) Color(0xFFEAF3FB) else Color.White)
                .clickable { onClick() }
                .padding(start = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(if (!mail.isRead) mail.avatarColor else Color.Transparent)
            )

            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(mail.avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(mail.initials, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mail.sender,
                        fontWeight = if (!mail.isRead) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                    Text(
                        text = mail.subject,
                        fontWeight = if (!mail.isRead) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                    Text(
                        text = mail.preview,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(mail.time, color = Color.Gray, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.attach),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        HorizontalDivider(color = Color(0xFFEFEFEF))
    }
}
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale

data class MailItem(
    val sender: String,
    val initials: String,
    val avatarColor: Color,
    val category: String,
    val subject: String,
    val preview: String,
    val time: String,
    val isRead: MutableState<Boolean>,
    val hasAttachment: Boolean = false
)

val sampleMails = listOf(
    MailItem("Airline Insight", "AI", Color(0xFF14508C), "IT", "Airline Insight", "Dear James, Please find the attached files for...", "8.20am", mutableStateOf(false), hasAttachment = true),
    MailItem("Airline Insight", "EC", Color(0xFF8B2E2E), "HR", "Airline Insight", "Dear James, Please find the attached files for...", "8.20am", mutableStateOf(false), hasAttachment = true),
    MailItem("Airline Insight", "EC", Color(0xFFC96A6A), "HR", "Airline Insight", "Dear James, Please find the attached files for...", "8.20am", mutableStateOf(true), hasAttachment = true),
    MailItem("Sarah Underwood", "SU", Color(0xFF7B4FA0), "SVN", "Sarah Underwood", "Dear James, Please find the attached files for...", "8.20am", mutableStateOf(true), hasAttachment = false),
    MailItem("IT Security", "IS", Color(0xFF4CAF50), "IT", "IT Security", "Our policies have been updated. Kindly take...", "8.20am", mutableStateOf(true), hasAttachment = false),
    MailItem("Airline Insight", "AI", Color(0xFF14508C), "IT", "Airline Insight", "Dear James, Please find the attached files for...", "8.20am", mutableStateOf(false), hasAttachment = true),
    MailItem("Airline Insight", "EC", Color(0xFF8B2E2E), "HR", "Airline Insight", "Dear James, Please find the attached files for...", "8.20am", mutableStateOf(false), hasAttachment = true),
    MailItem("Airline Insight", "EC", Color(0xFFC96A6A), "HR", "Airline Insight", "Dear James, Please find the attached files for...", "8.20am", mutableStateOf(true), hasAttachment = true),
    MailItem("Sarah Underwood", "SU", Color(0xFF7B4FA0), "SVN", "Sarah Underwood", "Dear James, Please find the attached files for...", "8.20am", mutableStateOf(true), hasAttachment = false),
    MailItem("IT Security", "IS", Color(0xFF4CAF50), "IT", "IT Security", "Our policies have been updated. Kindly take...", "8.20am", mutableStateOf(true), hasAttachment = false)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    onMailClick: (MailItem) -> Unit = {},
    onNewMailClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToNavigation: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedTab by remember { mutableStateOf(Tab.INBOX) }
    val listState = rememberLazyListState()

    // Gmail-style FAB behavior: expanded (icon + "New Mail" label) while at the top
    // or scrolling up, collapses to a plain circular icon while scrolling down.
    var fabExpanded by remember { mutableStateOf(true) }

    LaunchedEffect(listState) {
        var previousIndex = listState.firstVisibleItemIndex
        var previousOffset = listState.firstVisibleItemScrollOffset

        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                fabExpanded = when {
                    index == 0 && offset == 0 -> true // always expanded at the very top
                    index != previousIndex -> index < previousIndex // scrolled a full item — check direction
                    else -> offset <= previousOffset // same item — compare offsets
                }
                previousIndex = index
                previousOffset = offset
            }
    }

    val filteredMails = if (selectedFilter == "All") {
        sampleMails
    } else {
        sampleMails.filter { !it.isRead.value }
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
                            .background(Color.White)
                            .clickable { onProfileClick() },
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
                onTabSelected = { tab ->
                    selectedTab = tab
                    if (tab == Tab.HOME) {
                        onNavigateToHome()
                    }
                },
                onCenterButtonClick = onNavigateToNavigation
            )
        },
        floatingActionButton = {
            // Native Material3 morph: smoothly resizes/reshapes between a pill
            // (icon + "New Mail" text) and a plain circular icon — same motion Gmail uses.
            ExtendedFloatingActionButton(
                onClick = onNewMailClick,
                expanded = fabExpanded,
                containerColor = Color(0xFF14508C),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 10.dp
                ),
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.newmail),
                        contentDescription = if (fabExpanded) null else "New Mail",
                        modifier = Modifier.size(25.dp)
                    )
                },
                text = { Text("New Mail") }
            )
        }
    )
    { padding ->

        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Unread").forEach { filter ->
                    CategoryTab(
                        label = filter,
                        isSelected = filter == selectedFilter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(filteredMails) { index, mail ->
                    MailRow(
                        mail = mail,
                        onClick = {
                            mail.isRead.value = true
                            onMailClick(mail)
                        }
                    )
                    // Divider drawn between rows (skip after the last item)
                    if (index != filteredMails.lastIndex) {
                        HorizontalDivider(
                            color = Color(0xFFD9D9D9),
                            thickness = 1.dp,
                            modifier = Modifier.padding(start = 0.dp, end = 0.dp)
                        )
                    }
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
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MailRow(mail: MailItem, onClick: () -> Unit) {
    val isRead = mail.isRead.value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (!isRead) Color(0xFFEAF3FB) else Color.White)
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 6.dp)
                .width(4.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(50))
                // All unread indicator lines use this fixed brand blue
                .background(if (!isRead) Color(0xFF14508C) else Color.Transparent)
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
                Text(mail.initials, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mail.sender,
                    fontWeight = if (!isRead) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp
                )
                Text(
                    text = mail.preview,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(mail.time, color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}
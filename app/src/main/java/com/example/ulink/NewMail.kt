package com.example.ulink

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ulink.R

// -------------------- NEW MAIL SCREEN --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMailScreen(
    onBackClick: () -> Unit = {}, // called when back icon is tapped -> go to Inbox
    selectedTab: Tab = Tab.INBOX,
    onTabSelected: (Tab) -> Unit = {}
) {

    // State for the 3 input fields + the mail body
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    // Holds every file/image the user has attached so far
    val attachments = remember { mutableStateListOf<Uri>() }

    // System file/photo picker — lets the user pick multiple images or documents at once
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        attachments.addAll(uris)
    }

    Scaffold(
        // Top bar: back icon, title, send icon
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Mail", color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Attach button — opens the system picker for images/documents, icon size increased
                    IconButton(onClick = {
                        filePickerLauncher.launch(arrayOf("image/*", "application/pdf", "*/*"))
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.attach),
                            contentDescription = "Attach",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { /* TODO: handle send, include `attachments` list */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.send),
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF14508C))
            )
        },
        // Bottom bar: shared curved navbar (Home, Chats, Apps, Notifications, Inbox)
        bottomBar = {
            CustomTabBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                MailField(label = "From :", value = from, onValueChange = { from = it })
                MailField(label = "To :", value = to, onValueChange = { to = it })
                MailField(label = "Subject :", value = subject, onValueChange = { subject = it })
            }

            // Attached files preview row — only shows if something has been attached
            if (attachments.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(attachments) { uri ->
                        AttachmentChip(
                            fileName = uri.lastPathSegment ?: "Attachment",
                            onRemove = { attachments.remove(uri) }
                        )
                    }
                }
            }

            // The actual message-composing area, Gmail-style — takes up the remaining space
            TextField(
                value = body,
                onValueChange = { body = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                placeholder = { Text("Compose email") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

// One attached file shown as a small removable chip
@Composable
private fun AttachmentChip(fileName: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE0E0E0))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.attach),
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = fileName,
            fontSize = 12.sp,
            color = Color.DarkGray,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "✕",
            fontSize = 12.sp,
            color = Color.Red,
            modifier = Modifier.clickable { onRemove() }
        )
    }
}

// Reusable row: bold label + underlined text field, like in the design
@Composable
fun MailField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                singleLine = true,
                shape = RoundedCornerShape(0.dp)
            )
        }
        HorizontalDivider(color = Color.Gray)
    }
}
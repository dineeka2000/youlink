package com.example.ulink

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState

// Reply screen — opened when the user taps "Reply" on MailDetailsScreen.
// mail: the original email being replied to (used to show sender/subject and quoted text)
// onBackClick: closes this screen without sending
// onSendClick: called with the typed reply text when the user taps send
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyMailScreen(
    mail: MailItem,
    onBackClick: () -> Unit = {},
    onSendClick: (String) -> Unit = {}
) {
    // Holds what the user types as their reply
    var replyText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reply", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },

                //


                actions = {
                    // Send button — disabled (dimmed) until the user types something,
                    // just like Gmail greys out send on an empty reply
                    IconButton(
                        onClick = { onSendClick(replyText) },
                        enabled = replyText.isNotBlank()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.send),
                            contentDescription = "Send",
                            tint = if (replyText.isNotBlank()) Color.White else Color.White.copy(alpha = 0.4f)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF14508C))
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // "To" row — Gmail-style recipient chip showing who you're replying to
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("To:", color = Color.Gray, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(mail.avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(mail.initials, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(mail.sender, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
            HorizontalDivider(color = Color(0xFFEFEFEF))

            // Subject row — auto-prefixed with "Re:" like Gmail does
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Re: ${mail.subject}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            HorizontalDivider(color = Color(0xFFEFEFEF))

            // The actual reply text field — where the user types their message
            TextField(
                value = replyText,
                onValueChange = { replyText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp)
                    .padding(horizontal = 4.dp),
                placeholder = { Text("Compose your reply...") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFEFEFEF))

            // Quoted original message — collapsed context under the reply,
            // same idea as Gmail showing "On [date], [sender] wrote: ..." below your draft
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "On ${mail.time}, ${mail.sender} wrote:",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Vertical quote bar + original message text, like Gmail's quoted block
                Row {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .padding(vertical = 2.dp)
                            .background(Color(0xFFCCCCCC))
                            .fillMaxHeight()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                                    "Integer dictum mattis sem id vulputate. Donec pharetra lacus " +
                                    "in risus vehicula, at facilisis lectus posuere.",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Original attachment shown as reference, same chip style as MailDetails.kt
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.attach),
                        contentDescription = "Attachment",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Attachment", fontSize = 13.sp)
                }
            }
        }
    }
}
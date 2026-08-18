package com.example.ulink

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Full-detail view of a single mail, opened when a mail row is tapped in Inbox.kt
@OptIn(ExperimentalMaterial3Api::class) // fixes "experimental API" errors on CenterAlignedTopAppBar
@Composable
fun MailDetailsScreen(
    mail: MailItem,
    onBackClick: () -> Unit = {},
    onNewMailClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(Tab.INBOX) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(mail.subject, color = Color.White, fontWeight = FontWeight.Bold) },
                // Menu icon on the left, same as Inbox screen
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                // Profile icon on the right, same as Inbox screen

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

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text = "Enjoy faster access, better features, and a smoother experience right from your phone.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                //


                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(mail.avatarColor),
                        contentAlignment = Alignment.Center


                    )

                    {
                        Text(mail.initials, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    }


                    Spacer(modifier = Modifier.width(8.dp))
                    Text(mail.sender, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(mail.time, color = Color.Gray, fontSize = 12.sp)
                }



                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                            "Integer dictum mattis sem id vulputate. Donec pharetra lacus " +
                            "in risus vehicula, at facilisis lectus posuere.\n\n" +
                            "Nulla pulvinar laoreet massa, ut vehicula magna ultrices ut. " +
                            "Praesent lobortis sagittis neque, non elementum odio eleifend ut.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(16.dp))

                Text("BRs,", fontSize = 13.sp)
                Text(mail.sender, fontWeight = FontWeight.Bold, fontSize = 13.sp,color = Color.DarkGray,)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 50.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = { /* TODO: handle reply */ },
                    border = BorderStroke(1.dp, Color(0xFF176EBC)),
                    modifier = Modifier.width(195.69.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = null,
                        tint = Color(0xFF176EBC),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reply", color = Color(0xFF176EBC))
                }
            }
        }
    }
}
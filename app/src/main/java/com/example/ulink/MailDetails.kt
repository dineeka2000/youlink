package com.example.ulink

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailDetailsScreen(
    mail: MailItem,
    attachmentUrl: String = "", // NEW: URL of the attached file to download; empty = no real file yet
    onBackClick: () -> Unit = {},
    onNewMailClick: () -> Unit = {},
    onReplyClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(Tab.INBOX) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(mail.subject, color = Color.White, fontWeight = FontWeight.Bold) },
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
                onTabSelected = { selectedTab = it },
                onCenterButtonClick = { /* TODO: handle apps/logo click */ }
            )
        }
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
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(mail.avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(mail.initials, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(mail.sender, fontWeight = FontWeight.Bold,fontSize = 16.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(mail.time, color = Color.Gray, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                            "Integer dictum mattis sem id vulputate. Donec pharetra lacus " +
                            "in risus vehicula, at facilisis lectus posuere.\n\n" +
                            "Nulla pulvinar laoreet massa, ut vehicula magna ultrices ut. " +
                            "Praesent lobortis sagittis neque, non elementum odio eleifend ut.",
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Attachment chip — same design as before, just made clickable to trigger the download
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(6.dp))
                        .clickable {
                            downloadAttachment(context, attachmentUrl, "${mail.subject}_attachment")
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.attach),
                        contentDescription = "Attachment",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Attachment", fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("BRs,", fontSize = 15.sp)
                Text(mail.sender, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 50.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = onReplyClick,
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

// Downloads a file using Android's built-in DownloadManager, saving it to the Downloads folder
private fun downloadAttachment(context: Context, url: String, fileName: String) {
    if (url.isBlank()) {
        Toast.makeText(context, "No attachment available to download", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading attachment...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, "Downloading attachment...", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
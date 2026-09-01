package com.example.ulink

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.ulink.R

// -------------------- NEW MAIL SCREEN --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMailScreen(
    onBackClick: () -> Unit = {} // called when back icon is tapped -> go to Inbox
) {

    // State for the 3 input fields + the mail body
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    // Holds every file/image the user has attached so far
    val attachments = remember { mutableStateListOf<Uri>() }

    // Track which bottom tab is selected
    var selectedTab by remember { mutableStateOf(Tab.HOME) }

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
        // Bottom bar: your senior's curved navbar with the floating center logo
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

// -------------------- CUSTOM CURVED NAVBAR (from senior's code) --------------------

enum class Tab { HOME, CHATS, INBOX, NOTIFICATIONS }

class CenterInwardCurveShape(
    private val notchWidthDp: Float = 120f,
    private val notchDepthDp: Float = 33f,
    private val shoulderDp: Float = 20f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val notchWidth = with(density) { notchWidthDp.dp.toPx() }
        val notchDepth = with(density) { notchDepthDp.dp.toPx() }
        val shoulder = with(density) { shoulderDp.dp.toPx() }

        val yTop = 0f
        val yBottom = size.height
        val cx = size.width / 2f

        val width = minOf(notchWidth, size.width - 2f)
        val depth = maxOf(0f, minOf(notchDepth, size.height))
        val leftX = maxOf(0f, cx - width / 2f)
        val rightX = minOf(size.width, cx + width / 2f)

        val sMax = maxOf(0f, width / 2f - 1f)
        val s = maxOf(0f, minOf(shoulder, sMax))
        val c1 = s
        val c2 = maxOf(s, width / 4f)

        val path = Path().apply {
            moveTo(0f, yTop)
            lineTo(leftX, yTop)

            cubicTo(
                leftX + c1, yTop,
                cx - c2, yTop + depth,
                cx, yTop + depth
            )

            cubicTo(
                cx + c2, yTop + depth,
                rightX - c1, yTop,
                rightX, yTop
            )

            lineTo(size.width, yTop)
            lineTo(size.width, yBottom)
            lineTo(0f, yBottom)
            close()
        }

        return Outline.Generic(path)
    }
}

@Composable
fun CustomTabBar(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit,
    onCenterButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .shadow(elevation = 4.dp, shape = CenterInwardCurveShape())
                .background(
                    color = Color(0xFF14508C),
                    shape = CenterInwardCurveShape()
                )
        )

        // 5 equal-width slots: Home | Chats | Apps label | Notifications | Inbox.
        // Each item centers within its OWN fifth, and the middle slot mirrors
        // TabItem's icon + spacer height with an invisible Spacer so the "Apps"
        // text lands on the exact same baseline as the other labels.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(
                label = "Home",
                iconRes = R.drawable.home,
                isSelected = selectedTab == Tab.HOME,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(Tab.HOME) }
            )

            TabItem(
                label = "Chats",
                iconRes = R.drawable.home,
                isSelected = selectedTab == Tab.CHATS,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(Tab.CHATS) }
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(29.dp)) // 25.dp icon + 4.dp spacer, matching TabItem
                Text(
                    text = "Apps",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }

            TabItem(
                label = "Notifications",
                iconRes = R.drawable.home,
                isSelected = selectedTab == Tab.NOTIFICATIONS,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(Tab.NOTIFICATIONS) }
            )

            TabItem(
                label = "Inbox",
                iconRes = R.drawable.mail,
                isSelected = selectedTab == Tab.INBOX,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(Tab.INBOX) }
            )
        }

        // Floating center logo button — icon floats above the bar.
        // The "Apps" label now lives in the Row above, aligned with the other tabs.
        val centerInteractionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .offset(y = (-38).dp)
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(
                    interactionSource = centerInteractionSource,
                    indication = null
                ) { onCenterButtonClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Apps",
                modifier = Modifier.size(120.dp)
            )

        }
    }
}

@Composable
private fun TabItem(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(width = 22.dp, height = 20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isSelected) Color.White else Color.White
        )
    }
}
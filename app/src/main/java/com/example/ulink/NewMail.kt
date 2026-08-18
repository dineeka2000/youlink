package com.example.ulink

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
import com.example.ulink.R

// -------------------- NEW MAIL SCREEN --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMailScreen(
    onBackClick: () -> Unit = {} // called when back icon is tapped -> go to Inbox
) {

    // State for the 3 input fields
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }

    // Track which bottom tab is selected
    var selectedTab by remember { mutableStateOf(Tab.HOME) }

    Scaffold(
        // Top bar: back icon, title, send icon
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Mail", color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { // now navigates back to Inbox
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: handle send */ }) {
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

        // Body: From / To / Subject fields, each with a bottom line
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            MailField(label = "From :", value = from, onValueChange = { from = it })
            MailField(label = "To :", value = to, onValueChange = { to = it })
            MailField(label = "Subject :", value = subject, onValueChange = { subject = it })
        }
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

// Which tab is currently selected
enum class Tab { HOME, INBOX }

// Draws the bar's top edge with an inward notch/curve in the center,
// so the floating logo circle appears to sit inside a dip.
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
            .height(90.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        // Curved background bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shadow(elevation = 4.dp, shape = CenterInwardCurveShape())
                .background(
                    color = Color(0xFF14508C),
                    shape = CenterInwardCurveShape()
                )
        )

        // Row of tab items on top of the curved bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TabItem(
                label = "Home",
                iconRes = R.drawable.home,
                isSelected = selectedTab == Tab.HOME,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(Tab.HOME) }
            )
            TabItem(
                label = "Inbox",
                iconRes = R.drawable.mail,
                isSelected = selectedTab == Tab.INBOX,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(Tab.INBOX) }
            )
        }

        // Floating center logo button, sitting inside the notch
        Box(
            modifier = Modifier
                .offset(y = (-50).dp)
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onCenterButtonClick() },
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

// Single tab item: icon + label, greys out when not selected
@Composable
private fun TabItem(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(width = 27.dp, height = 25.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color.White
        )
    }
}
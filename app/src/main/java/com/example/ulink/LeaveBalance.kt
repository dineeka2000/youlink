// ---------------- LeaveBalance.kt ----------------
package com.example.ulink

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat

// ---------- Data model ----------

/**
 * One row of the leave balance table.
 * entitlement / balance are nullable: null represents "N/A" in the UI.
 * utilized / pendingApproval always show a number (0.00 when not applicable).
 */
data class LeaveBalanceItem(
    val leaveType: String,
    val entitlement: Double?,
    val balance: Double?,
    val utilized: Double,
    val pendingApproval: Double
)

// ---------- Per-user leave data ----------

/**
 * Leave balances keyed by logged-in user ID. Replace this with a real API/database
 * call in production — this local map is only a stand-in so each user's table is
 * still driven by their own array of LeaveBalanceItem, never someone else's.
 */
object LeaveRepository {

    private val leaveDataByUser: Map<String, Array<LeaveBalanceItem>> = mapOf(
        "james.perera" to arrayOf(
            LeaveBalanceItem("Annual", 21.00, 15.00, 3.00, 3.00),
            LeaveBalanceItem("Annual B/F", 1.00, 0.00, 1.00, 0.00),
            LeaveBalanceItem("Duty Leave", null, null, 0.00, 0.00),
            LeaveBalanceItem("Medical", 14.00, 14.00, 0.00, 0.00),
            LeaveBalanceItem("Medical- Accident Leave", null, null, 0.00, 0.00),
            LeaveBalanceItem("Nopay Leave", null, null, 0.00, 0.00),
            LeaveBalanceItem("Short Leave", null, null, 0.00, 0.00)
        )
        // Add one entry per user here, e.g. "sarah.underwood" to arrayOf(...)
    )

    /** Returns the logged-in user's own leave balance array only. */
    fun getLeaveBalance(userId: String): Array<LeaveBalanceItem> {
        return leaveDataByUser[userId] ?: emptyArray()
    }
}

// ---------- Screen ----------

@Composable
fun LeaveBalanceScreen(
    loggedInUserId: String, // ID of the currently authenticated user — decides which array is shown
    onMenuClick: () -> Unit = {},
    onLeaveHistoryClick: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToInbox: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}, // Tab.PROFILE in the bottom bar
    onNavigateToApps: () -> Unit = {}
) {
    // Simple in-place navigation: no NavController needed.
    // When true, this composable shows ApplyLeaveScreen instead of the leave balance UI.
    var showApplyLeave by remember { mutableStateOf(false) }

    // Controls the "My Leave" popup (Apply Leave / Short Leave choice) shown
    // when the user taps the Apply Leave card.
    var showLeaveOptionsDialog by remember { mutableStateOf(false) }

    if (showApplyLeave) {
        ApplyLeaveScreen(
            onBackClick = { showApplyLeave = false },
            onApplyClick = { formState ->
                // TODO: submit formState to your repository/API
                showApplyLeave = false
            },
            onResetClick = { /* form resets itself internally */ }
        )
        return
    }

    // Tab enum (from BottomBar.kt) is: HOME, LEAVE, APPS, PROFILE, INBOX.
    // This screen IS the "Leave" destination, so it starts already selected on Tab.LEAVE.
    var selectedTab by remember { mutableStateOf(Tab.LEAVE) }

    // Same status bar treatment as HomeScreen — the header below now paints
    // its own background behind the status bar, since setting
    // window.statusBarColor directly is a no-op on edge-to-edge Android versions.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    var isBalanceExpanded by remember { mutableStateOf(true) }

    // Only this user's own data is ever loaded — re-fetched if loggedInUserId changes (e.g. re-login).
    val leaveBalance = remember(loggedInUserId) { LeaveRepository.getLeaveBalance(loggedInUserId) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // Plain Box instead of Surface — Surface always clips its content to
            // its shape, which would slice off the top of the floating center
            // logo button whenever it overflows above the bar's bounds.
            // Box has no clip, so the logo can pop out above the bar as intended.
            // CustomTabBar extends its own blue background behind the nav bar,
            // so no extra padding is needed here.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                CustomTabBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        when (tab) {
                            Tab.HOME -> onNavigateToHome()
                            Tab.INBOX -> onNavigateToInbox()
                            Tab.APPS -> onNavigateToApps()
                            Tab.PROFILE -> onNavigateToProfile()
                            else -> {} // Tab.LEAVE — already on this screen
                        }
                    }
                )
            }
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(bottom = padding.calculateBottomPadding())
                .fillMaxSize()
        ) {

            // ---- Header: image background with a dark gradient overlay, same as HomeScreen ----
            val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp + statusBarHeight)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.titleb),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 18.dp, end = 12.dp, top = statusBarHeight + 6.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    //IconButton(onClick = onMenuClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu),
                            contentDescription = "Menu",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                   // }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Leaves",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 12.dp)
            ) {

                LeaveBalanceCard(
                    leaveBalance = leaveBalance,
                    expanded = isBalanceExpanded,
                    onToggle = { isBalanceExpanded = !isBalanceExpanded }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ActionCard(
                    title = "Apply Leave",
                    subtitle = "Request a new leave for you time off",
                    iconRes = R.drawable.leave,
                    accentColor = Color(0xFF6FCF97),
                    onClick = { showLeaveOptionsDialog = true }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ActionCard(
                    title = "My Leave History",
                    subtitle = "View your pass requests and status",
                    iconRes = R.drawable.myleave,
                    accentColor = Color(0xFFF2994A),
                    onClick = onLeaveHistoryClick
                )
            }
        }
    }

    // "My Leave" popup — offers Apply Leave / Short Leave.
    if (showLeaveOptionsDialog) {
        LeaveOptionsDialog(
            onDismiss = { showLeaveOptionsDialog = false },
            onApplyLeaveSelected = {
                showLeaveOptionsDialog = false
                showApplyLeave = true
            },
            onShortLeaveSelected = {
                showLeaveOptionsDialog = false
                // TODO: hook up a Short Leave flow here when one exists
            }
        )
    }
}

@Composable
private fun LeaveBalanceCard(
    leaveBalance: Array<LeaveBalanceItem>,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Leave Balance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color.DarkGray
                )
            }

            if (expanded) {
                HorizontalDivider(color = Color(0xFFE5E5E5))

                // Table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD6D6D6))
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    Text(
                        "Leave Type",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1.3f)
                    )
                    Text(
                        "Entitlement",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Balance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Utilized",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Pending\nApproval",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Table rows — built from the user's own LeaveBalanceItem array
                leaveBalance.forEachIndexed { index, item ->
                    val rowBackground = if (index % 2 == 0) Color.White else Color(0xFFD3D3D3)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(rowBackground)
                            .padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = item.leaveType,
                            fontSize = 13.sp,
                            color = Color(0xFF333333),
                            modifier = Modifier.weight(1.3f)
                        )
                        Text(
                            text = item.entitlement?.let { "%.2f".format(it) } ?: "N/A",
                            fontSize = 13.sp,
                            // Available (non-N/A) entitlement/balance figures are shown in green
                            color = if (item.entitlement != null) Color(0xFF2E7D32) else Color(0xFF666666),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = item.balance?.let { "%.2f".format(it) } ?: "N/A",
                            fontSize = 13.sp,
                            color = if (item.balance != null) Color(0xFF2E7D32) else Color(0xFF666666),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "%.2f".format(item.utilized),
                            fontSize = 13.sp,
                            color = if (item.pendingApproval > 0) Color(0xFF2E7D32) else Color(0xFF333333),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "%.2f".format(item.pendingApproval),
                            fontSize = 13.sp,
                            color = if (item.pendingApproval > 0) Color(0xFF2E7D32) else Color(0xFF333333),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (index != leaveBalance.lastIndex) {
                        HorizontalDivider(color = Color(0xFFEFEFEF))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        // Normal black drop shadow (Card's default elevation shadow, not tinted).
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
            ) {
                // The drawable already contains the colored blob + icon together,
                // so it's placed directly in the corner instead of drawing a
                // separate colored circle behind a tinted icon.
                // Crop (not Fit) so the blob fills the card's full height on the
                // right instead of shrinking down to fit inside a small square.
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(110.dp),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp, end = 90.dp)
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }

            // Thick accent stroke along the card's bottom edge — clipped by the
            // Card's own rounded shape, so its bottom corners stay rounded too.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(accentColor)
            )
        }
    }
}

// ---------- "My Leave" popup ----------

/**
 * The white rounded popup that appears when the Apply Leave card is tapped.
 * Offers a choice between the full Apply Leave form and Short Leave.
 */
@Composable
private fun LeaveOptionsDialog(
    onDismiss: () -> Unit,
    onApplyLeaveSelected: () -> Unit,
    onShortLeaveSelected: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "My Leave",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LeaveOptionButton(
                        label = "Apply Leave",
                        onClick = onApplyLeaveSelected,
                        modifier = Modifier.weight(1f)
                    )
                    LeaveOptionButton(
                        label = "Short Leave",
                        onClick = onShortLeaveSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaveOptionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val blue = Color(0xFF1B5FC2)
    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(width = 1.dp, color = blue, shape = RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = blue,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}
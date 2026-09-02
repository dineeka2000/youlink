// ---------------- LeaveBalance.kt ----------------
package com.example.ulink

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    onApplyLeaveClick: () -> Unit = {},
    onLeaveHistoryClick: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToInbox: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}, // Tab.NOTIFICATIONS is labeled "Profile" in the bottom bar
    onNavigateToApps: () -> Unit = {}
) {
    // Real Tab enum (from BottomBar.kt) is: HOME, CHATS, APPS, INBOX, NOTIFICATIONS.
    // The bar's labels are: Home, Leave (Tab.CHATS), Apps, Profile (Tab.NOTIFICATIONS), Inbox.
    // This screen IS the "Leave" destination, so it starts already selected on Tab.CHATS.
    var selectedTab by remember { mutableStateOf(Tab.CHATS) }
    var isBalanceExpanded by remember { mutableStateOf(true) }

    // Only this user's own data is ever loaded — re-fetched if loggedInUserId changes (e.g. re-login).
    val leaveBalance = remember(loggedInUserId) { LeaveRepository.getLeaveBalance(loggedInUserId) }

    Scaffold(
        topBar = { LeaveBalanceTopBar(onMenuClick = onMenuClick) },
        bottomBar = {
            // CustomTabBar only takes selectedTab + onTabSelected — the center
            // logo button already calls onTabSelected(Tab.APPS) internally.
            CustomTabBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        Tab.HOME -> onNavigateToHome()
                        Tab.CHATS -> Unit // "Leave" — already on this screen, nothing to navigate to
                        Tab.APPS -> onNavigateToApps()
                        Tab.INBOX -> onNavigateToInbox()
                        Tab.NOTIFICATIONS -> onNavigateToProfile() // "Profile"
                    }
                }
            )
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
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
                iconRes = R.drawable.applyleave,
                blobColor = Color(0xFF6FCF97),
                onClick = onApplyLeaveClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            ActionCard(
                title = "My Leave History",
                subtitle = "View your pass requests and status",
                iconRes = R.drawable.myleave,
                blobColor = Color(0xFFF2994A),
                onClick = onLeaveHistoryClick
            )
        }
    }
}

@Composable
private fun LeaveBalanceTopBar(onMenuClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF0B3D6B), Color(0xFF1E7ED6))
                )
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "Leaves",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center)
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
                    val rowBackground = if (index % 2 == 0) Color.White else Color(0xFFF7F7F7)

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
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "%.2f".format(item.pendingApproval),
                            fontSize = 13.sp,
                            color = Color(0xFF333333),
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
    blobColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
        ) {
            // Quarter-circle color blob peeking out from the bottom-right corner,
            // clipped by the card's own rounded shape.
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 65.dp, y = 65.dp)
                    .clip(CircleShape)
                    .background(blobColor)
            )

            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 26.dp)
                    .size(28.dp)
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
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog


// Profile screen — shows user info, sign out button, and app/support footer
// onBackClick: usually not used here since there's no back icon in the design, kept for consistency
// onSignOutConfirmed: called after the user confirms sign out in the popup
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    selectedTab: Tab = Tab.HOME,
    onTabSelected: (Tab) -> Unit = {},
    onSignOutConfirmed: () -> Unit = {}
) {
    // Controls whether the sign-out confirmation popup is showing
    var showSignOutDialog by remember { mutableStateOf(false) }

    Scaffold(
        // Let the background image draw behind the status bar; bottom bar still gets its own inset
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color(0xFFEFEFEF),
        bottomBar = {
            CustomTabBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { padding ->

        // Background image (bg.png) covers the top portion, including the status bar area
        Box(
            modifier = Modifier
                .padding(bottom = padding.calculateBottomPadding())
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp), // expanded top bar background, drawn from the very top edge
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.fillMaxSize()) {

                // "Profile" title over the background image — pushed below the status bar
                Text(
                    text = "Profile",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Photo circle + white card share one Box now, so the circle's
                // bottom half can overlap down onto the top of the card instead
                // of floating separately above it with a gap in between.
                Box(modifier = Modifier.fillMaxWidth()) {

                    // White card holding name, role, phone, and sign-out button.
                    // Top padding (60.dp) leaves room above the card for the top
                    // half of the circle to sit on the blue background; the extra
                    // inner top padding (70.dp) pushes the name text down so it
                    // clears the circle's bottom edge, which lands inside the card.
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(top = 90.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "James Syahir (25222)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SOFTWARE DEVELOPMENT MANAGER",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.phone),
                                contentDescription = "Phone",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1234",
                                color = Color.DarkGray,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Sign out button — outlined, red, pill-shapeda
                        OutlinedButton(
                            onClick = { showSignOutDialog = true },
                            shape = RoundedCornerShape(50),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.signout),
                                contentDescription = null,
                                tint = Color(0xFFE91E25),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log out", color = Color(0xFFE91E25), fontWeight = FontWeight.Bold)
                        }
                    }

                    // Profile photo circle — aligned to the top-center of the shared
                    // Box, so its top 60.dp sits on the blue background and its
                    // bottom 60.dp overlaps down onto the card underneath it.
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(135.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile photo",
                            modifier = Modifier
                                .size(108.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(19.dp))

                // Footer card: version, help text, support line, company logo
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 14.dp), // vertical padding trimmed from 20.dp to 14.dp
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("V 1.0.0.2", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Need help? Contact IT Service Desk",
                        color = Color.DarkGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "(Ext: 3000) | 24x7 Support",
                        color = Color.DarkGray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp)) // trimmed from 12.dp to 4.dp
                    // Company logo image replacing the "SriLankan IT Systems" text
                    Image(
                        painter = painterResource(id = R.drawable.slogo),
                        contentDescription = "Company logo",
                        modifier = Modifier
                            .height(32.dp)
                            .wrapContentWidth(unbounded = true),
                        contentScale = ContentScale.FillHeight
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

    // Sign-out confirmation popup — icon badge, clear hierarchy, solid action buttons
    if (showSignOutDialog) {
        Dialog(onDismissRequest = { showSignOutDialog = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Confirm Logout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You'll need to sign in again to access your account.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B6B6B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Cancel / Log Out — full-width, stacked so both read as equally
                // important actions rather than a dismissive text-link pair
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showSignOutDialog = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0D0D0)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF3A3A3A))
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }

                    Button(
                        onClick = {
                            showSignOutDialog = false
                            onSignOutConfirmed()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF05497B))
                    ) {
                        Text("Log Out", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
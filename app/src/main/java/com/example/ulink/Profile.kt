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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Profile photo, centered, overlapping the white card below it
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer box is intentionally NOT clipped, so the camera badge
                    // can hang over the edge of the circular photo without being cut off
                    Box(
                        modifier = Modifier.size(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner circular white backdrop + photo (this one IS clipped)
                        Box(
                            modifier = Modifier
                                .size(120.dp)
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

                        // Small camera icon badge, bottom-right of the photo — sits on the
                        // outer unclipped box so it renders fully on top, not cropped
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF14508C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.camera),
                                contentDescription = "Change photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // White card holding name, role, phone, and sign-out button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(24.dp),
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

                    // Sign out button — outlined, red, pill-shaped
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
                        Text("Sign out", color = Color(0xFFE91E25), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer card: version, help text, support line, company logo
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(20.dp),
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
                    Spacer(modifier = Modifier.height(12.dp))
                    // Company logo image replacing the "SriLankan IT Systems" text
                    Image(
                        painter = painterResource(id = R.drawable.slogo),
                        contentDescription = "Company logo",
                        modifier = Modifier.height(28.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

    // Sign-out confirmation popup — simple "Confirm Logout" style dialog
    if (showSignOutDialog) {
        Dialog(onDismissRequest = { showSignOutDialog = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFDBE7FC))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Confirm Logout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Are you sure you want to log out?",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // "No" and "Yes" as simple text buttons, right-aligned
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showSignOutDialog = false }) {
                        Text("No", color = Color(0xFF0657A1), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        showSignOutDialog = false
                        onSignOutConfirmed()
                    }) {
                        Text("Yes", color = Color(0xFF0657A1), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
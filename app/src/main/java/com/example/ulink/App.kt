package com.example.ulink

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

// -----------------------------------------------------------------------
// Data model for a collapsible navigation section
// -----------------------------------------------------------------------
data class NavSection(
    val title: String,
    val items: List<String>
)

private val navSections = listOf(
    NavSection(
        title = "Applications",
        items = listOf(
            "AeroOps",
            "ARD - Intranet",
            "AQRS (GHDR)",
            "Baggage Processing System (BPS)",
            "Crew Space",
            "EasyPass",
            "FDCA",
            "GAL Update",
            "IPV",
            "Interview Assessment System",
            "oneworld Traning (Refresher)",
            "PriceWise",
            "RevenuePlus Cargo",
            "Staff Travel",
            "Veritas",
            "AeroTrack",
            "Airline Insight",
            "BCCS",
            "Call Billing",
            "DPub",
            "eLearnment System",
            "Finesse Suite",
            "Government Travel",

        )
    ),
    NavSection(
        title = "Corporate Information",
        items = listOf(
            "About Us",
            "Leadership Team",
            "Press Releases",
            "Annual Reports"
        )
    ),
    NavSection(
        title = "Divisions",
        items = listOf(
            "Finance",
            "Operations",
            "Human Resources",
            "Technology"
        )
    )
)

// -----------------------------------------------------------------------
// Main App composable
// -----------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(onClose: () -> Unit = {}) {

    // Track which sections are expanded. All start expanded to match the
    // screenshot (chevrons pointing up).
    val expandedStates = remember {
        mutableStateMapOf(*navSections.map { it.title to true }.toTypedArray())
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ---- Background image (appback.png) ----
        Image(
            painter = painterResource(id = R.drawable.appback),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ---- Scrollable content (sections only — close button lives outside this) ----
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // Reserve space at the top so content starts below the close button
            Spacer(modifier = Modifier.height(104.dp))

            // Navigation sections
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                navSections.forEach { section ->
                    NavSectionItem(
                        section = section,
                        expanded = expandedStates[section.title] ?: false,
                        onToggle = {
                            expandedStates[section.title] =
                                !(expandedStates[section.title] ?: false)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // ---- Close button: pinned on top, outside the scrollable area,
        // ---- with an explicit clickable modifier + high zIndex so nothing
        // ---- above it in the hierarchy can ever swallow the tap. ----
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 20.dp)
                .zIndex(10f)
                .size(48.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClose
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// -----------------------------------------------------------------------
// A single collapsible section (header + underline + expandable items)
// -----------------------------------------------------------------------
@Composable
fun NavSectionItem(
    section: NavSection,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Header row: title + chevron
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = Color.White
            )
        }

        // Underline
        Divider(color = Color.White.copy(alpha = 0.6f), thickness = 1.dp)

        // Expandable list of 4 items
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.fillMaxWidth()) {
                section.items.forEach { item ->
                    Text(
                        text = item,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                    )
                    Divider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------
// Preview
// -----------------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun AppPreview() {
    MaterialTheme {
        App()
    }
}
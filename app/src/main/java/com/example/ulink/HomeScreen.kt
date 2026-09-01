package com.example.ulink

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.delay


// Simple data holder for each featured link tile
data class FeaturedLink(
    val title: String,
    val imageRes: Int
)

// Data holder for each Flight Fleet card
data class FlightFleetInfo(
    val flightCode: String,
    val flightNumber: String,
    val dateTime: String,
    val from: String,
    val fromDelay: String,
    val to: String,
    val toDelay: String
)

// Data holder for each currency exchange rate card
data class CurrencyRate(
    val flagRes: Int,
    val currencyCode: String,
    val rate: String
)

// Data holder for each official article card
data class ArticleInfo(
    val day: String,
    val month: String,
    val title: String,
    val author: String
)

@Composable
fun HomeScreen(
    onNavigateToInbox: () -> Unit = {},
    onNavigateToNavigation: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var showFeaturedLinksDialog by remember { mutableStateOf(false) }

    // Tracks which tab is highlighted in the bottom nav bar (shared Tab enum from NewMail.kt)
    var selectedTab by remember { mutableStateOf(Tab.HOME) }

    // Full set shown in the "Featured Links" popup grid.
    // NOTE: replace R.drawable.f4 ... f15 with your actual icon assets.
    val allFeaturedLinks = listOf(
        FeaturedLink("BIA Flights", R.drawable.fl1),
        FeaturedLink("Safety", R.drawable.fl2),
        FeaturedLink("Crisis Management", R.drawable.fl3),
        FeaturedLink("Profile Image", R.drawable.fl4),
        FeaturedLink("Nivahana", R.drawable.fl5),
        FeaturedLink("Integrity Committee", R.drawable.fl6),
        FeaturedLink("Welfare", R.drawable.fl7),
        FeaturedLink("Ask & Know", R.drawable.fl8),
        FeaturedLink("EWORLD", R.drawable.fl9),
        FeaturedLink("Magazines", R.drawable.fl10),
        FeaturedLink("Destinations", R.drawable.fl11),
        FeaturedLink("PIF", R.drawable.fl12),
        FeaturedLink("PEF", R.drawable.fl13),
        FeaturedLink("IT Service Complaints", R.drawable.fl14),
        FeaturedLink("Monara", R.drawable.fl15)
    )

    if (showFeaturedLinksDialog) {
        FeaturedLinksDialog(
            links = allFeaturedLinks,
            onDismiss = { showFeaturedLinksDialog = false }
        )
    }

    Scaffold(
        bottomBar = {
            // Plain Box instead of Surface — Surface always clips its content to
            // its shape, which was slicing off the top of the floating center
            // logo button whenever it overflowed above the bar's bounds.
            // Box has no clip, so the logo can pop out above the bar as intended.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                CustomTabBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        if (tab == Tab.INBOX) {
                            onNavigateToInbox()
                        }
                    },
                    onCenterButtonClick = onNavigateToNavigation
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {

            // ---- Top header: image background with a dark gradient overlay ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp) // reduced from 120.dp
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
                        .padding(start = 18.dp, top = 20.dp, bottom = 10.dp), // reduced top padding
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.menu),
                        contentDescription = "Logo",
                        modifier = Modifier.size(24.dp) // slightly smaller
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hello", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("James Syahir", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Box(
                        modifier = Modifier
                            .size(width = 70.dp, height = 70.dp) // reduced from 90.dp
                            .clip(
                                RoundedCornerShape(
                                    topStart = 35.dp,
                                    bottomStart = 35.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            )
                            .background(Color.White)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile photo",
                            modifier = Modifier
                                .size(42.dp) // reduced from 54.dp
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Scrollable content below the fixed header
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                // ---- Swipeable banner carousel with dot indicators ----
                BannerCarousel()

                Spacer(modifier = Modifier.height(16.dp))

                // ---- Row of 4 category icons ----
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 26.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryItem(label = "SARA", imageRes = R.drawable.sara)
                    CategoryItem(label = "HR Space", imageRes = R.drawable.hr)
                    CategoryItem(label = "Staff Travel", imageRes = R.drawable.stafftravel)
                    CategoryItem(label = "Medicash", imageRes = R.drawable.medicash)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ---- Featured Links ----
                SectionHeader(
                    title = "Featured Links",
                    showViewAll = true,
                    onViewAllClick = { showFeaturedLinksDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val previewLinks = listOf(
                        FeaturedLink("BIA Flights", R.drawable.f1),
                        FeaturedLink("Safety", R.drawable.f2),
                        FeaturedLink("Crisis Management", R.drawable.f3),
                        FeaturedLink("Nivahana", R.drawable.f4),
                        FeaturedLink("Welfare", R.drawable.f5),
                        FeaturedLink("Monara", R.drawable.f7)
                    )

                    items(previewLinks) { link ->
                        Box(
                            modifier = Modifier
                                .size(width = 130.dp, height = 90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF2B6CB8), Color(0xFF0F3D75)),
                                        start = Offset(0f, 0f),
                                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = link.imageRes),
                                    contentDescription = link.title,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(link.title, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }

                    // "See More" tile at the end of the row — opens the full popup grid
                    item {
                        Box(
                            modifier = Modifier
                                .size(width = 130.dp, height = 90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF2B6CB8), Color(0xFF0F3D75)),
                                        start = Offset(0f, 0f),
                                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                    )
                                )
                                .clickable { showFeaturedLinksDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = "See More",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("See More", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // ---- Highlights (chip row) ----
                SectionHeader(title = "Highlights", showViewAll = false)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Company Holidays", "SVN", "Employee manuals", "Chairmens msg").forEach { label ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF1E5FA8),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(label, color = Color(0xFF1E5FA8), fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ---- Flight Fleet cards (horizontally scrollable) ----
                SectionHeader(title = "Flight Fleet", showViewAll = true, viewAllLabel = "i-Fleet")
                Spacer(modifier = Modifier.height(12.dp))

                val flightFleetList = listOf(
                    FlightFleetInfo("A33-ALN", "UL225", "12 Jun 2025 22:22 (Local)", "CMB", "Delay (60min)", "DXB", "Delay (60min)"),
                    FlightFleetInfo("A32-BLN", "UL304", "13 Jun 2025 09:10 (Local)", "CMB", "On Time", "DOH", "On Time"),
                    FlightFleetInfo("A35-CLN", "UL517", "14 Jun 2025 15:45 (Local)", "CMB", "Delay (30min)", "SIN", "Delay (30min)")
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(flightFleetList) { flight ->
                        Box(
                            modifier = Modifier
                                .width(360.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Column {
                                // Top row: airline logo + flight code, date on the right
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = R.drawable.airline),
                                            contentDescription = "Airline logo",
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                                .padding(4.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(flight.flightCode, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    }
                                    Text(
                                        flight.dateTime,
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Bottom row: CMB --- (flightline image) --- DXB
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(flight.from, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            flight.fromDelay,
                                            color = if (flight.fromDelay == "On Time") Color(0xFF2E7D32) else Color.Red,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.flightline),
                                            contentDescription = "Flight path",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(20.dp),
                                            contentScale = ContentScale.FillWidth
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(flight.flightNumber, fontSize = 15.sp, color = Color.DarkGray)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(flight.to, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            flight.toDelay,
                                            color = if (flight.toDelay == "On Time") Color(0xFF2E7D32) else Color.Red,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ---- BOC Exchange Currency Rate cards (horizontally scrollable) ----
                SectionHeader(title = "BOC Exchange Currency Rate as at 11-03-2025", showViewAll = false)
                Spacer(modifier = Modifier.height(12.dp))

                val currencyRates = listOf(
                    CurrencyRate(R.drawable.flag1, "AUD", "LKR 179.1814"),
                    CurrencyRate(R.drawable.flag1, "KWD", "LKR 870.1864"),
                    CurrencyRate(R.drawable.flag1, "GBP", "LKR 385.4210")
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(currencyRates) { currency ->
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = currency.flagRes),
                                        contentDescription = currency.currencyCode,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(currency.currencyCode, fontSize = 16.sp, color = Color.DarkGray)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Conversion Rate", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(currency.rate, fontSize = 13.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ---- Official Articles of 2025 cards (horizontally scrollable) ----
                SectionHeader(title = "Official Articles of 2025", showViewAll = false)
                Spacer(modifier = Modifier.height(12.dp))

                val articles = listOf(
                    ArticleInfo("12", "JAN", "AVIAREPS India favours SL for team offsite tour", "by The Morning"),
                    ArticleInfo("18", "FEB", "SriLankan Airlines expands regional network", "by Daily News"),
                    ArticleInfo("03", "MAR", "New crew training facility opens in Colombo", "by The Island")
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(articles) { article ->
                        Row(
                            modifier = Modifier
                                .width(320.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .width(64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE0E0E0))
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(article.day, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFB03052))
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(article.month, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    article.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(article.author, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }
}

// ---- Swipeable image carousel with dot page indicators ----
@Composable
private fun BannerCarousel() {
    val bannerImages = listOf(
        R.drawable.banner,
        R.drawable.banner,
        R.drawable.banner,
        R.drawable.banner
    )

    val pagerState = rememberPagerState(pageCount = { bannerImages.size })

    // Auto-advance to the next banner every 4 seconds. Manual swipes still
    // work as normal — this effect just keeps nudging the pager forward,
    // wrapping back to the first image after the last one.
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % bannerImages.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    // Box lets the dot indicators float ON TOP of the pager, instead of sitting below it
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentPadding = PaddingValues(horizontal = 16.dp), // leaves side gaps so neighbors peek in
            pageSpacing = 10.dp
        ) { page ->

            val isCurrentPage = pagerState.currentPage == page

            Image(
                painter = painterResource(id = bannerImages[page]),
                contentDescription = "Banner ${page + 1}",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        // Only blur the pages that AREN'T currently centered/focused
                        if (!isCurrentPage) Modifier.blur(6.dp) else Modifier
                    ),
                contentScale = ContentScale.Crop
            )
        }

        // Dot indicators, floating over the bottom of the current image
        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            repeat(bannerImages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 9.dp else 7.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}

// Reusable icon + label for the category row
@Composable
private fun CategoryItem(label: String, imageRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            modifier = Modifier
                .width(92.dp)   // increased — controls width only
                .height(76.dp), // kept the same, or adjust independently
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

// Reusable "Section Title ... View All" row
@Composable
private fun SectionHeader(
    title: String,
    showViewAll: Boolean,
    viewAllLabel: String = "View All",
    onViewAllClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        if (showViewAll) {
            Text(
                viewAllLabel,
                color = Color(0xFF1E5FA8),
                fontSize = 13.sp,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }
    }
}

// ---- Popup grid shown when "View All" is tapped on Featured Links ----
@Composable
private fun FeaturedLinksDialog(
    links: List<FeaturedLink>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Featured Links", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Manual 3-column grid (not lazy/scrollable) so the whole
                // set of tiles lays out at full height inside the dialog.
                links.chunked(3).forEach { rowLinks ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowLinks.forEach { link ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, Color(0xFF1E5FA8), RoundedCornerShape(14.dp))
                                    .clickable {
                                        // TODO: navigate to link.title's destination
                                        onDismiss()
                                    }
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = link.imageRes),
                                    contentDescription = link.title,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    link.title,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    maxLines = 2
                                )
                            }
                        }
                        // Fill any remaining slots in the last row so tiles keep equal width
                        repeat(3 - rowLinks.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
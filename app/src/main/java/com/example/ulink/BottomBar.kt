package com.example.ulink

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Matches the 5 bottom nav tabs in order: Home, Leave, Apps (center), Profile, Inbox.
enum class Tab { HOME, LEAVE, APPS, PROFILE, INBOX }

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
            cubicTo(leftX + c1, yTop, cx - c2, yTop + depth, cx, yTop + depth)
            cubicTo(cx + c2, yTop + depth, rightX - c1, yTop, rightX, yTop)
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
    onTabSelected: (Tab) -> Unit
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

        // 5 equal-width slots: Home | Leave | Apps | Profile | Inbox.
        // The middle (Apps) slot is now clickable and selectable, same as the others,
        // it just has no icon of its own here since the floating logo above serves that role.
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
                label = "Leave",
                iconRes = R.drawable.leaveicon,
                isSelected = selectedTab == Tab.LEAVE,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(Tab.LEAVE) }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(Tab.APPS) },
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
                label = "Inbox",
                iconRes = R.drawable.mail,
                isSelected = selectedTab == Tab.INBOX,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(Tab.INBOX) }
            )

            TabItem(
                label = "Profile",
                iconRes = R.drawable.picon,
                isSelected = selectedTab == Tab.PROFILE,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(Tab.PROFILE) }
            )
        }

        // Floating center logo button — same tab as the "Apps" label below it
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
                ) { onTabSelected(Tab.APPS) },
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
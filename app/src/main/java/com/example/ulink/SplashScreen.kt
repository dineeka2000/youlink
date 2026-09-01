package com.example.ulink

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.scale


@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    //screen time
    LaunchedEffect(true) {
        delay(4000)
        onTimeout()
    }

    // Box stacks layers: background -> gradient -> center logo -> bottom logo/text
    Box(modifier = Modifier.fillMaxSize()) {

        // Layer 1: background image
        Image(
            painter = painterResource(id = R.drawable.splashbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Layer 2: black gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 2.0f)),
                        startY = 600f
                    )
                )
        )

        // Layer 3: logo
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(140.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.mlogo),
                contentDescription = "App logo",
                modifier = Modifier.size(150.dp)
            )
        }

        // Layer 4: SLA logo + version text
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.whitelogo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .scale(2.5f)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text("Version 0.1", color = Color.White)
        }
    }
}


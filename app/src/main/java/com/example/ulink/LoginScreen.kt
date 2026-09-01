package com.example.ulink
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // How much of the screen (from the BOTTOM) the white section covers.
    // 1.0 = entire screen, 0.5 = bottom half, etc. Adjust this single number to move the boundary.
    val whiteFraction = 0.30f

    val cardTopY = 340.dp
    val circleSize = 140.dp
    val circleCenterY = 300.dp

    Box(modifier = Modifier.fillMaxSize()) {

        // Background image, fills the whole screen
        Image(
            painter = painterResource(id = R.drawable.splashbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // White section — takes up whiteFraction of the screen, anchored to the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(whiteFraction)
                .align(Alignment.BottomCenter)
                .background(Color.White)
        )

        // Card — positioned independently, absolute distance from the top of the screen
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = cardTopY)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "yoULink",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,

                color = Color(0xFF1E5FA8)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Collaborate with SriLankan Home",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1E5FA8),

                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("UserName") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(color = Color.LightGray)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(color = Color.LightGray)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLoginSuccess,
                enabled = username.isNotBlank() && password.isNotBlank(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E5FA8),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF1E5FA8),
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Login", fontSize = 16.sp)
            }
        }

        // Circle — positioned independently, absolute distance from the top of the screen
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = circleCenterY - (circleSize / 2))
                .size(circleSize)
                .clip(RoundedCornerShape(circleSize / 2))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App logo",
                modifier = Modifier.size(120.dp)
            )
        }

        // Bottom logo + version — anchored to the bottom of the screen
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.itlogo),
                contentDescription = "IT Systems logo",
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .scale(2.5f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Version 0.1", fontSize = 11.sp, color = Color.Gray)
        }
    }
}


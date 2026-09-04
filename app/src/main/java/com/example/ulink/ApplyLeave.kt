package com.example.ulink

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.composed
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

// ---------- Colors ----------
private val HeaderBlueStart = Color(0xFF1B5FC2)
private val HeaderBlueEnd = Color(0xFF2E86D8)
private val FieldBorder = Color(0xFFD0D0D0)
private val HintGray = Color(0xFF9E9E9E)
private val LabelGray = Color(0xFF3A3A3A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyLeaveScreen(
    onBackClick: () -> Unit = {},
    onApplyClick: (LeaveFormState) -> Unit = {},
    onResetClick: () -> Unit = {}
) {
    // ---------- Form State ----------
    var leaveYear by remember { mutableStateOf("2026") }
    var leaveType by remember { mutableStateOf("Annual") }
    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    var attachmentName by remember { mutableStateOf<String?>(null) }

    val yearOptions = listOf("2024", "2025", "2026", "2027")
    val leaveTypeOptions = listOf("Annual", "Sick", "Casual", "Maternity", "Unpaid")
    val reasonOptions = listOf("Personal", "Medical", "Family", "Travel", "Other")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {
        // ---------- Header ----------
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(
//                    Brush.horizontalGradient(listOf(HeaderBlueStart, HeaderBlueEnd))
//                )
//                .padding(top = 48.dp, bottom = 20.dp)
//        ) {
//            IconButton(
//                onClick = onBackClick,
//                modifier = Modifier.align(Alignment.CenterStart)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.ArrowBack,
//                    contentDescription = "Back",
//                    tint = Color.White
//                )
//            }

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


            Text(
                text = "Apply Leave",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            )  {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        tint = Color.White,
                        // modifier = Modifier.size(30.dp)
                    )
                }


        }

        // ---------- Form Card ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Leave Balance",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = LabelGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Leave time period : 01/01/2026 - 31/12/2026",
                fontSize = 13.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = LabelGray,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Leave Year dropdown
            FormDropdown(
                label = "Leave Year",
                selected = leaveYear,
                options = yearOptions,
                onSelected = { leaveYear = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Leave Type dropdown
            FormDropdown(
                label = "Leave Type",
                selected = leaveType,
                options = leaveTypeOptions,
                onSelected = { leaveType = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Leave From Date
            DatePickerField(
                label = "Leave From Date",
                value = fromDate,
                onDateSelected = { fromDate = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // To Date
            DatePickerField(
                label = "To Date",
                value = toDate,
                onDateSelected = { toDate = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Attachments — label and Browse button share the same row now,
            // with the button sitting right next to the label instead of below it.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Attachments", fontSize = 14.sp, color = LabelGray)
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        // TODO: launch file picker (ActivityResultContracts.GetContent)
                        attachmentName = "document.pdf"
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF616161)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Browse", color = Color.White, fontSize = 13.sp)
                }
                attachmentName?.let {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = it, fontSize = 13.sp, color = LabelGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reason for Leave dropdown
            FormDropdown(
                label = "Reason for Leave",
                selected = reason,
                options = reasonOptions,
                placeholder = "Select Reason",
                onSelected = { reason = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Comments
            Text(text = "Comments", fontSize = 14.sp, color = LabelGray)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = comments,
                onValueChange = { comments = it },
                placeholder = { Text("Type here...", color = HintGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(6.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = FieldBorder,
                    focusedBorderColor = HeaderBlueStart
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Line Admin/s   -   Multiple Admin (Leave)",
                fontSize = 13.sp,
                color = LabelGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Apply / Reset buttons
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        onApplyClick(
                            LeaveFormState(
                                leaveYear, leaveType, fromDate, toDate,
                                attachmentName, reason, comments
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderBlueStart)
                ) {
                    Text("Apply", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                    onClick = {
                        leaveYear = "2026"
                        leaveType = "Annual"
                        fromDate = ""
                        toDate = ""
                        reason = ""
                        comments = ""
                        attachmentName = null
                        onResetClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HeaderBlueStart)
                ) {
                    Text("Reset", color = HeaderBlueStart, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ---------- Reusable Dropdown ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormDropdown(
    label: String,
    selected: String,
    options: List<String>,
    placeholder: String = "",
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Text(text = label, fontSize = 14.sp, color = LabelGray)
    Spacer(modifier = Modifier.height(6.dp))
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder, color = HintGray) },
            trailingIcon = {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = LabelGray)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = FieldBorder,
                focusedBorderColor = HeaderBlueStart
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ---------- Reusable Date Picker Field ----------
@Composable
private fun DatePickerField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formatted = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                onDateSelected(formatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Text(text = label, fontSize = 14.sp, color = LabelGray)
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        placeholder = { Text("Select date", color = HintGray) },
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Pick date", tint = LabelGray)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoRipple { datePickerDialog.show() },
        shape = RoundedCornerShape(6.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = FieldBorder,
            focusedBorderColor = HeaderBlueStart
        )
    )
}

// Simple clickable modifier without ripple, for opening the date picker.
// Uses Modifier.composed { } so `remember` can be called safely here.
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = interactionSource
        ) { onClick() }
    )
}
data class LeaveFormState(
    val leaveYear: String,
    val leaveType: String,
    val fromDate: String,
    val toDate: String,
    val attachmentName: String?,
    val reason: String,
    val comments: String
)
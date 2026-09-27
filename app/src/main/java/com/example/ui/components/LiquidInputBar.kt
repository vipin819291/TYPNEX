package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyQuota
import com.example.ui.theme.TypnexCyan
import com.example.ui.theme.TypnexElectricBlue
import com.example.ui.theme.TypnexError
import com.example.ui.theme.TypnexVipGold
import com.example.ui.theme.liquidGlass

@Composable
fun LiquidInputBar(
    isDark: Boolean,
    isVip: Boolean,
    quota: DailyQuota,
    millisUntilMidnight: Long,
    isRecordingAudio: Boolean,
    recordingSeconds: Int,
    onSendMessage: (String) -> Unit,
    onStartRecording: () -> Unit,
    onStopAndSendRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    onCameraClick: () -> Unit,
    onAttachmentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    val hoursLeft = (millisUntilMidnight / (1000 * 60 * 60))
    val minutesLeft = (millisUntilMidnight / (1000 * 60)) % 60

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Quota Status Pill (Files & Chats remaining or Locked until midnight)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isVip) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(TypnexVipGold)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "VIP Blue Tick: Unlimited Files & Chats",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TypnexVipGold
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val fileText = if (quota.isFilesLocked) {
                        "⚠️ File limit reached! Resets at midnight (${hoursLeft}h ${minutesLeft}m)"
                    } else {
                        "Files: ${quota.filesSent}/10 today • Max 20MB"
                    }
                    Text(
                        text = fileText,
                        fontSize = 11.sp,
                        fontWeight = if (quota.isFilesLocked) FontWeight.Bold else FontWeight.Normal,
                        color = if (quota.isFilesLocked) TypnexError else if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }

                Text(
                    text = "Chats: ${quota.chatsSent}/100",
                    fontSize = 11.sp,
                    color = if (quota.isChatsLocked) TypnexError else if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }

        // Active Audio Recording View OR Normal Input Dock
        if (isRecordingAudio) {
            AudioRecordingBar(
                recordingSeconds = recordingSeconds,
                isDark = isDark,
                onCancel = onCancelRecording,
                onSend = onStopAndSendRecording
            )
        } else {
            // Liquid Glass Input Dock
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        shape = RoundedCornerShape(26.dp),
                        isDark = isDark,
                        elevation = 6.dp
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // File Attachment Button (Documents, PDFs, Audio, Images - NO VIDEO)
                IconButton(
                    onClick = onAttachmentClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach File",
                        tint = if (isDark) TypnexCyan else TypnexElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Direct Camera Click Button
                IconButton(
                    onClick = onCameraClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Take Photo",
                        tint = if (isDark) TypnexCyan else TypnexElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Audio Voice Record Button
                IconButton(
                    onClick = onStartRecording,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Record Audio",
                        tint = if (isDark) TypnexCyan else TypnexElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Text Input Field
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = {
                        Text(
                            text = "Type draft or message...",
                            fontSize = 14.sp,
                            color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = if (isDark) Color.White else Color(0xFF0F172A),
                        unfocusedTextColor = if (isDark) Color.White else Color(0xFF0F172A),
                        cursorColor = TypnexCyan
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    singleLine = false,
                    maxLines = 4
                )

                // Send Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (text.isNotBlank()) TypnexCyan else (if (isDark) Color(0x3038BDF8) else Color(0x300088FF))
                        )
                        .clickable(enabled = text.isNotBlank()) {
                            onSendMessage(text)
                            text = ""
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = if (text.isNotBlank()) Color.Black else (if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AudioRecordingBar(
    recordingSeconds: Int,
    isDark: Boolean,
    onCancel: () -> Unit,
    onSend: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(26.dp),
                isDark = isDark,
                accentGlow = Color(0xFFEF4444),
                elevation = 6.dp
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Red Pulsing Mic Indicator & Live Seconds Timer
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Recording",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Recording Voice Note...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFEF4444)
                )
                Text(
                    text = "0:${recordingSeconds.toString().padStart(2, '0')}",
                    fontSize = 12.sp,
                    color = if (isDark) Color.White else Color.Black
                )
            }
        }

        // Action Buttons: Cancel and Stop & Send
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel Recording",
                    tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(TypnexCyan)
                    .clickable { onSend() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Voice Note",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttachmentType
import com.example.data.model.ChatMessage
import com.example.data.model.FileAttachment
import com.example.data.model.MessageType
import com.example.ui.theme.TypnexBlueTick
import com.example.ui.theme.TypnexCyan
import com.example.ui.theme.TypnexE2eeGreen
import com.example.ui.theme.TypnexElectricBlue
import com.example.ui.theme.TypnexVipGold
import com.example.ui.theme.liquidGlass
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isDark: Boolean,
    isUserVip: Boolean,
    onAttachmentClick: (FileAttachment) -> Unit,
    onDeleteClick: (ChatMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    val isFromOwner = message.isFromOwner
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(message.timestamp))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 12.dp),
        horizontalArrangement = if (isFromOwner) Arrangement.Start else Arrangement.End
    ) {
        val bubbleShape = if (isFromOwner) {
            RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp)
        } else {
            RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomEnd = 20.dp, bottomStart = 20.dp)
        }

        val bubbleGlassGlow = if (isFromOwner) {
            if (isDark) Color(0x7038BDF8) else Color(0x600088FF)
        } else {
            if (isDark) Color(0x608B5CF6) else Color(0x7000D2FF)
        }

        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .liquidGlass(
                    shape = bubbleShape,
                    isDark = isDark,
                    alpha = if (isDark) (if (isFromOwner) 0.72f else 0.85f) else (if (isFromOwner) 0.82f else 0.90f),
                    accentGlow = bubbleGlassGlow,
                    elevation = 4.dp
                )
                .padding(12.dp)
        ) {
            Column {
                // Header: Sender Name & Verification Badge + Delete Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isFromOwner) "Typnex Support" else message.senderName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isFromOwner) {
                                if (isDark) TypnexCyan else TypnexElectricBlue
                            } else {
                                if (isDark) Color(0xFFC084FC) else Color(0xFF7C3AED)
                            }
                        )

                        if (isFromOwner) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Official Owner",
                                tint = TypnexCyan,
                                modifier = Modifier.size(13.dp)
                            )
                        } else if (isUserVip) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "VIP Blue Tick",
                                tint = TypnexBlueTick,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // Delete button
                    if (!message.isDeletedForEveryone) {
                        IconButton(
                            onClick = { onDeleteClick(message) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = if (isDark) Color(0x60FFFFFF) else Color(0x60000000),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // If message was deleted for everyone
                if (message.isDeletedForEveryone) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🚫 This message was deleted by sender",
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                } else {
                    // Main Message Text
                    if (message.text.isNotBlank()) {
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
                        )
                    }

                    // File Attachment Display (if present)
                    message.attachment?.let { attachment ->
                        Spacer(modifier = Modifier.height(8.dp))
                        FileAttachmentCard(
                            attachment = attachment,
                            isDark = isDark,
                            onClick = { onAttachmentClick(attachment) }
                        )
                    }

                    // Voice Note Player Preview (if voice)
                    if (message.messageType == MessageType.VOICE && message.voiceDurationSeconds > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        VoicePlayerBubble(
                            durationSeconds = message.voiceDurationSeconds,
                            isDark = isDark
                        )
                    }
                }

                // Footer: Timestamp & E2EE Lock
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "E2EE",
                        tint = TypnexE2eeGreen,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = formattedTime,
                        fontSize = 10.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
fun FileAttachmentCard(
    attachment: FileAttachment,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val icon: ImageVector = when (attachment.type) {
        AttachmentType.PDF -> Icons.Default.PictureAsPdf
        AttachmentType.DOCUMENT -> Icons.Default.Description
        AttachmentType.SPREADSHEET -> Icons.Default.TableChart
        AttachmentType.AUDIO -> Icons.Default.Audiotrack
        AttachmentType.ARCHIVE -> Icons.Default.FolderZip
        else -> Icons.Default.Description
    }

    val iconColor = when (attachment.type) {
        AttachmentType.PDF -> Color(0xFFEF4444)
        AttachmentType.DOCUMENT -> Color(0xFF3B82F6)
        AttachmentType.SPREADSHEET -> Color(0xFF10B981)
        AttachmentType.AUDIO -> Color(0xFFEC4899)
        AttachmentType.ARCHIVE -> Color(0xFFF59E0B)
        else -> TypnexCyan
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isDark) Color(0x351E293B) else Color(0x200088FF))
            .border(
                width = 1.dp,
                color = if (attachment.isFromOwner) TypnexVipGold else (if (isDark) Color(0x4038BDF8) else Color(0x300088FF)),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attachment.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    maxLines = 1
                )
                Text(
                    text = "${attachment.type.name} • ${attachment.sizeFormatted}",
                    fontSize = 11.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )

                // Owner Ad Gate Badge vs User Direct Open Badge
                if (attachment.isFromOwner) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TypnexVipGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Sponsored Gate • Tap to Unlock",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TypnexVipGold
                        )
                    }
                }
            }

            // Download Icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (attachment.isFromOwner) TypnexVipGold.copy(alpha = 0.2f) else Color(0x2038BDF8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    tint = if (attachment.isFromOwner) TypnexVipGold else TypnexCyan,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

@Composable
fun VoicePlayerBubble(
    durationSeconds: Int,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDark) Color(0x301E293B) else Color(0x150088FF))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(TypnexCyan),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Voice Note",
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Simulated Sound Waveform bars
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val barHeights = listOf(6, 12, 18, 14, 22, 10, 16, 20, 12, 8, 18, 24, 14, 10, 16, 8)
            barHeights.forEach { height ->
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(height.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isDark) TypnexCyan.copy(alpha = 0.7f) else TypnexElectricBlue.copy(alpha = 0.7f))
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "0:${durationSeconds.toString().padStart(2, '0')}",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
        )
    }
}

package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FileAttachment
import com.example.ui.theme.TypnexCyan
import com.example.ui.theme.TypnexDeepIndigo
import com.example.ui.theme.TypnexElectricBlue
import com.example.ui.theme.TypnexVipGold
import com.example.ui.theme.liquidGlass

/**
 * Top Permanent Banner Ad
 */
@Composable
fun PermanentTopBannerAd(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .liquidGlass(
                shape = RoundedCornerShape(14.dp),
                isDark = isDark,
                elevation = 2.dp,
                accentGlow = Color(0x500088FF)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Sponsored Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF59E0B))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "AD",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = TypnexCyan,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = "Typnex Turbo Cloud • 5-Min Turnaround",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = "High-speed document typing, conversions & drafts",
                        fontSize = 10.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            // Install / Open Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) Color(0x3038BDF8) else Color(0x200088FF))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "VISIT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) TypnexCyan else TypnexElectricBlue
                )
            }
        }
    }
}

/**
 * Bottom Permanent Banner Ad (placed directly above input dock)
 */
@Composable
fun PermanentBottomBannerAd(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .liquidGlass(
                shape = RoundedCornerShape(12.dp),
                isDark = isDark,
                elevation = 2.dp,
                accentGlow = Color(0x408B5CF6)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF10B981))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "SPONSORED",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Pro Typist Network • 100% Accuracy Guaranteed",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
                )
            }

            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/**
 * 30-Second Unskippable Long Ad (Triggered every 12 hours)
 */
@Composable
fun Long30SecondAdDialog(
    remainingSeconds: Int,
    isDark: Boolean,
    onCloseAd: () -> Unit
) {
    val progress = (30 - remainingSeconds) / 30f
    val isDone = remainingSeconds <= 0

    Dialog(
        onDismissRequest = { /* Cannot dismiss until timer finishes! */ },
        properties = DialogProperties(dismissOnBackPress = isDone, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(
                    shape = RoundedCornerShape(28.dp),
                    isDark = isDark,
                    alpha = if (isDark) 0.95f else 0.98f,
                    elevation = 16.dp,
                    accentGlow = TypnexCyan
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF59E0B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "12-HOUR SPONSORED AD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }

                    // Security / Anti-adblock verified badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = TypnexCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Unblockable",
                            fontSize = 10.sp,
                            color = TypnexCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Big Visual Showcase
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(TypnexCyan, TypnexElectricBlue, TypnexDeepIndigo))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Typnex Professional Suite",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Get your office documents, handwritten notes, and legal drafts transcribed and formatted within 5 minutes with zero errors.",
                    fontSize = 13.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = TypnexCyan,
                    trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Timer text or Close Button
                if (!isDone) {
                    Text(
                        text = "Ad ends in: ${remainingSeconds}s (Mandatory Sponsor View)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF59E0B)
                    )
                } else {
                    Button(
                        onClick = onCloseAd,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TypnexCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Continue to Typnex Chat",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * Mandatory Ad Gate for Owner Files:
 * Displayed every time the user clicks to download or open a file sent by the Owner!
 */
@Composable
fun OwnerFileAdGateDialog(
    attachment: FileAttachment,
    remainingSeconds: Int,
    isDark: Boolean,
    onDownloadUnlocked: () -> Unit
) {
    val isReady = remainingSeconds <= 0

    Dialog(
        onDismissRequest = { /* Must wait until ad completes */ },
        properties = DialogProperties(dismissOnBackPress = isReady, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(
                    shape = RoundedCornerShape(26.dp),
                    isDark = isDark,
                    alpha = if (isDark) 0.94f else 0.98f,
                    elevation = 16.dp,
                    accentGlow = TypnexVipGold
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Security header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TypnexVipGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "FILE SPONSOR GATE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }

                    Text(
                        text = "Ad-Blocker Proof",
                        fontSize = 10.sp,
                        color = TypnexVipGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(TypnexVipGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isReady) Icons.Default.Download else Icons.Default.Lock,
                        contentDescription = null,
                        tint = TypnexVipGold,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Owner File Download Gate",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )

                Text(
                    text = "File: ${attachment.name} (${attachment.sizeFormatted})",
                    fontSize = 12.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Sponsored content card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0x301E293B) else Color(0x200088FF))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "🌟 Sponsored: Typnex Verified Secure Transfer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TypnexCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Files sent from Typnex Owner are scanned, encrypted, and verified for 100% typing accuracy.",
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isReady) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = TypnexVipGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Unlocking download in ${remainingSeconds}s...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TypnexVipGold
                        )
                    }
                } else {
                    Button(
                        onClick = onDownloadUnlocked,
                        colors = ButtonDefaults.buttonColors(containerColor = TypnexVipGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Download File Now",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.ui.theme.TypnexBlueTick
import com.example.ui.theme.TypnexCyan
import com.example.ui.theme.TypnexE2eeGreen
import com.example.ui.theme.TypnexElectricBlue
import com.example.ui.theme.TypnexError
import com.example.ui.theme.TypnexVipGold
import com.example.ui.theme.liquidGlass

@Composable
fun LiquidTopBar(
    user: UserProfile?,
    isDark: Boolean,
    currentLanguageName: String,
    onLanguageClick: () -> Unit,
    onE2eeClick: () -> Unit,
    onOpenSessionsClick: () -> Unit,
    onNewFreshChat: () -> Unit,
    onClearAllChat: () -> Unit,
    onShareFile: () -> Unit,
    onToggleVip: () -> Unit,
    onSimulateOwnerFile: () -> Unit,
    onToggleTheme: () -> Unit,
    onTest30sAd: () -> Unit,
    onOpenHodDashboard: (() -> Unit)? = null,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                isDark = isDark,
                elevation = 10.dp
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Owner Avatar & Title with User's Name displayed right at the top!
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Typnex Shield / Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF0F172A) else Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "T",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = TypnexCyan
                    )
                    // Online Green Dot
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(TypnexE2eeGreen)
                            .align(Alignment.BottomEnd)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    // Top: User Name prominently displayed
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user?.name ?: "Client",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Blue Tick Verified Badge if VIP enabled
                        if (user?.isVerifiedVip == true) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Blue Tick",
                                tint = TypnexBlueTick,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Role badge
                        if (user?.role == UserRole.HOD) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TypnexVipGold)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("HOD", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        } else if (user?.role == UserRole.EMPLOYEE) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TypnexCyan)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("STAFF", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }

                    // Bottom: Typnex Official Owner 1-on-1 Status & E2EE
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onE2eeClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "E2EE",
                            tint = TypnexE2eeGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Typnex Owner • E2EE Encrypted",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }
            }

            // Right Action Buttons: Sessions History, Language Pill & 3-Dot Menu
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Preserved Sessions Button
                IconButton(
                    onClick = onOpenSessionsClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Preserved Chats",
                        tint = if (isDark) TypnexCyan else TypnexElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // If HOD, show Portal Button
                if (user?.role == UserRole.HOD && onOpenHodDashboard != null) {
                    IconButton(
                        onClick = onOpenHodDashboard,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "HOD Dashboard",
                            tint = TypnexVipGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Language Switcher Chip
                Surface(
                    onClick = onLanguageClick,
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDark) Color(0x3038BDF8) else Color(0x250088FF),
                    modifier = Modifier.height(30.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Language",
                            modifier = Modifier.size(14.dp),
                            tint = if (isDark) TypnexCyan else TypnexElectricBlue
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = currentLanguageName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                    }
                }

                // Three-Dot Menu
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .liquidGlass(
                                shape = RoundedCornerShape(16.dp),
                                isDark = isDark,
                                alpha = if (isDark) 0.92f else 0.96f
                            )
                            .width(240.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("New Chat (Preserve Old)") },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = TypnexCyan) },
                            onClick = {
                                menuExpanded = false
                                onNewFreshChat()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Preserved Chat History") },
                            leadingIcon = { Icon(Icons.Default.History, contentDescription = null, tint = TypnexElectricBlue) },
                            onClick = {
                                menuExpanded = false
                                onOpenSessionsClick()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Clear This Chat") },
                            leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color(0xFFEF4444)) },
                            onClick = {
                                menuExpanded = false
                                onClearAllChat()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Share / Export File") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onShareFile()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("E2EE Security & Keys") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TypnexE2eeGreen) },
                            onClick = {
                                menuExpanded = false
                                onE2eeClick()
                            }
                        )

                        // "The public-facing interface should not have the option to manually extend session limits
                        // or grant access permissions; the authority to manage these settings must rest solely with
                        // the employee IDs and department head logins."
                        if (user?.role == UserRole.HOD || user?.role == UserRole.EMPLOYEE) {
                            DropdownMenuItem(
                                text = {
                                    Text(if (user.isVerifiedVip) "VIP Blue Tick (Active)" else "Grant VIP Blue Tick")
                                },
                                leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null, tint = TypnexBlueTick) },
                                onClick = {
                                    menuExpanded = false
                                    onToggleVip()
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("Simulate Owner File Reply") },
                            leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24)) },
                            onClick = {
                                menuExpanded = false
                                onSimulateOwnerFile()
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = if (isDark) Color(0x30FFFFFF) else Color(0x20000000)
                        )

                        DropdownMenuItem(
                            text = { Text(if (isDark) "Switch to Light Theme" else "Switch to Dark Glass") },
                            leadingIcon = {
                                Icon(
                                    if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onToggleTheme()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Test 30s Long Ad") },
                            leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TypnexCyan) },
                            onClick = {
                                menuExpanded = false
                                onTest30sAd()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Logout", color = TypnexError) },
                            leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = TypnexError) },
                            onClick = {
                                menuExpanded = false
                                onLogout()
                            }
                        )
                    }
                }
            }
        }
    }
}

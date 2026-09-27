package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatSession
import com.example.data.model.EmployeeAccount
import com.example.ui.theme.DynamicFluidBackground
import com.example.ui.theme.TypnexBlueTick
import com.example.ui.theme.TypnexCyan
import com.example.ui.theme.TypnexE2eeGreen
import com.example.ui.theme.TypnexElectricBlue
import com.example.ui.theme.TypnexError
import com.example.ui.theme.TypnexVipGold
import com.example.ui.theme.liquidGlass
import com.example.viewmodel.TypnexViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HodDashboardScreen(
    viewModel: TypnexViewModel,
    isDark: Boolean,
    onOpenSessionChat: (ChatSession) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Employee View, 1: General Public View
    val employees by viewModel.employeesList.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()

    var newEmpName by remember { mutableStateOf("") }
    var newEmpUsername by remember { mutableStateOf("") }
    var newEmpPassword by remember { mutableStateOf("") }
    var showCreateForm by remember { mutableStateOf(false) }

    DynamicFluidBackground(isDark = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // HOD Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        shape = RoundedCornerShape(20.dp),
                        isDark = isDark,
                        elevation = 8.dp,
                        accentGlow = TypnexVipGold
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TypnexVipGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = TypnexVipGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Head of Department (HOD)",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(TypnexVipGold)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "FULL OVERSIGHT",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                            Text(
                                text = "Monitoring public chats, employee distribution & platform security",
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        }
                    }

                    // Logout / Exit Button
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x301E293B) else Color(0x15000000))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = TypnexError,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Tabs: Employee View vs General Public View
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = TypnexCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Employee View (${employees.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (selectedTab == 0) TypnexCyan else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                            )
                        }
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "General Public View (${allSessions.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (selectedTab == 1) TypnexCyan else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tab Content
            if (selectedTab == 0) {
                // EMPLOYEE VIEW
                EmployeeViewSection(
                    employees = employees,
                    publicSessionsCount = allSessions.size,
                    isDark = isDark,
                    showCreateForm = showCreateForm,
                    onToggleCreateForm = { showCreateForm = !showCreateForm },
                    newEmpName = newEmpName,
                    onNameChange = { newEmpName = it },
                    newEmpUsername = newEmpUsername,
                    onUsernameChange = { newEmpUsername = it },
                    newEmpPassword = newEmpPassword,
                    onPasswordChange = { newEmpPassword = it },
                    onCreateEmployee = {
                        if (newEmpUsername.isNotBlank() && newEmpPassword.isNotBlank()) {
                            viewModel.createEmployeeAccount(
                                username = newEmpUsername.trim(),
                                password = newEmpPassword.trim(),
                                name = newEmpName.ifBlank { newEmpUsername }
                            )
                            newEmpName = ""
                            newEmpUsername = ""
                            newEmpPassword = ""
                            showCreateForm = false
                        }
                    },
                    onToggleActive = { id, active -> viewModel.toggleEmployeeActive(id, active) },
                    onDeleteEmployee = { id -> viewModel.deleteEmployee(id) }
                )
            } else {
                // GENERAL PUBLIC VIEW
                GeneralPublicViewSection(
                    sessions = allSessions,
                    isDark = isDark,
                    onOpenChat = onOpenSessionChat,
                    onGrantVip = { sessionId -> viewModel.grantVipToSessionUser(sessionId) },
                    onClearChat = { sessionId -> viewModel.clearChatSession(sessionId) },
                    onDeleteSession = { sessionId -> viewModel.deletePublicSession(sessionId) }
                )
            }
        }
    }
}

@Composable
fun EmployeeViewSection(
    employees: List<EmployeeAccount>,
    publicSessionsCount: Int,
    isDark: Boolean,
    showCreateForm: Boolean,
    onToggleCreateForm: () -> Unit,
    newEmpName: String,
    onNameChange: (String) -> Unit,
    newEmpUsername: String,
    onUsernameChange: (String) -> Unit,
    newEmpPassword: String,
    onPasswordChange: (String) -> Unit,
    onCreateEmployee: () -> Unit,
    onToggleActive: (id: String, active: Boolean) -> Unit,
    onDeleteEmployee: (id: String) -> Unit
) {
    val activeEmployees = employees.filter { it.isActive }
    val activeCount = activeEmployees.size

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Workload distribution summary card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        shape = RoundedCornerShape(16.dp),
                        isDark = isDark,
                        elevation = 4.dp
                    )
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Workload Auto-Distribution Matrix",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TypnexCyan
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (activeCount >= 2) TypnexE2eeGreen.copy(alpha = 0.2f) else Color(0x20F59E0B))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (activeCount >= 2) "Even 50/50 Active Distribution" else "Single/Fallback Route",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeCount >= 2) TypnexE2eeGreen else Color(0xFFF59E0B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val quotaPerEmp = if (activeCount > 0) (publicSessionsCount.toDouble() / activeCount) else 0.0
                    Text(
                        text = "Total Public Sessions: $publicSessionsCount • Active Employees: $activeCount\n" +
                                if (activeCount >= 2) "Documents evenly distributed: ~${String.format(Locale.US, "%.1f", quotaPerEmp)} public clients per active employee."
                                else "When at least 2 employee IDs are active, incoming documents are split 50/50 evenly.",
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LockClock,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Inactivity Guard: Employee IDs auto-logout and go offline after 1 hour of inactivity.",
                            fontSize = 10.sp,
                            color = Color(0xFFF59E0B)
                        )
                    }
                }
            }
        }

        // Add New Employee Toggle Button
        item {
            Button(
                onClick = onToggleCreateForm,
                colors = ButtonDefaults.buttonColors(containerColor = if (showCreateForm) TypnexError else TypnexCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (showCreateForm) Icons.Default.Delete else Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showCreateForm) "Cancel New Employee" else "+ Create New Employee Credentials",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // New Employee Form
        if (showCreateForm) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            shape = RoundedCornerShape(16.dp),
                            isDark = isDark,
                            elevation = 6.dp,
                            accentGlow = TypnexCyan
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Assign New Employee Access",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = newEmpName,
                            onValueChange = onNameChange,
                            label = { Text("Full Name (e.g. Vikas Kumar)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newEmpUsername,
                            onValueChange = onUsernameChange,
                            label = { Text("Employee Username (e.g. vikas_typist)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newEmpPassword,
                            onValueChange = onPasswordChange,
                            label = { Text("Employee Password") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onCreateEmployee,
                            colors = ButtonDefaults.buttonColors(containerColor = TypnexCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Save & Issue Credentials",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        // Employees List
        items(employees, key = { it.id }) { emp ->
            val now = System.currentTimeMillis()
            val millisSinceActive = now - emp.lastActiveTimestamp
            val remainingInactivityMinutes = ((3600_000L - millisSinceActive) / (1000 * 60)).coerceAtLeast(0)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        shape = RoundedCornerShape(16.dp),
                        isDark = isDark,
                        elevation = 4.dp
                    )
                    .padding(12.dp)
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (emp.isActive) TypnexE2eeGreen.copy(alpha = 0.2f) else Color(0x2094A3B8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = if (emp.isActive) TypnexE2eeGreen else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = emp.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (emp.isActive) TypnexE2eeGreen else Color(0xFF64748B))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = if (emp.isActive) "ONLINE" else "OFFLINE",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (emp.isActive) Color.Black else Color.White
                                    )
                                }
                            }
                            Text(
                                text = "Username: ${emp.username} • Password: ••••••••",
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                            if (emp.isActive) {
                                Text(
                                    text = "Auto-logout timeout: ${remainingInactivityMinutes}m remaining",
                                    fontSize = 10.sp,
                                    color = if (remainingInactivityMinutes < 15) TypnexError else Color(0xFFF59E0B)
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Toggle Online/Offline
                        IconButton(
                            onClick = { onToggleActive(emp.id, !emp.isActive) }
                        ) {
                            Icon(
                                imageVector = if (emp.isActive) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                                contentDescription = "Toggle Status",
                                tint = if (emp.isActive) TypnexE2eeGreen else Color(0xFF94A3B8),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Delete employee credentials
                        IconButton(
                            onClick = { onDeleteEmployee(emp.id) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = TypnexError,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeneralPublicViewSection(
    sessions: List<ChatSession>,
    isDark: Boolean,
    onOpenChat: (ChatSession) -> Unit,
    onGrantVip: (sessionId: String) -> Unit,
    onClearChat: (sessionId: String) -> Unit,
    onDeleteSession: (sessionId: String) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (sessions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No public user sessions active right now.",
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }
        } else {
            items(sessions, key = { it.id }) { session ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            shape = RoundedCornerShape(16.dp),
                            isDark = isDark,
                            elevation = 4.dp
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(TypnexCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = null,
                                        tint = TypnexCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = "${session.userName} (${session.title})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isDark) Color.White else Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "Assigned to: ${session.assignedEmployeeName ?: "Typnex Auto Pool"} • ${dateFormat.format(Date(session.lastMessageTime))}",
                                        fontSize = 11.sp,
                                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                    )
                                }
                            }

                            // Open / Monitor Chat
                            Button(
                                onClick = { onOpenChat(session) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TypnexCyan),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = "Monitor",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // HOD Controls: Extend Limits / Grant VIP / Clear Chat / Delete Session
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Grant VIP / Extend Limits (Authority solely with Employee & HOD!)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TypnexBlueTick.copy(alpha = 0.15f))
                                    .clickable { onGrantVip(session.id) }
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = TypnexBlueTick,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "VIP / Limit",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TypnexBlueTick
                                    )
                                }
                            }

                            // Clear Chat Messages
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x20F59E0B))
                                    .clickable { onClearChat(session.id) }
                                    .padding(vertical = 6.dp, horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Clear Chat",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            }

                            // Delete / Terminate Session
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x20EF4444))
                                    .clickable { onDeleteSession(session.id) }
                                    .padding(vertical = 6.dp, horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = TypnexError,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Delete",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TypnexError
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

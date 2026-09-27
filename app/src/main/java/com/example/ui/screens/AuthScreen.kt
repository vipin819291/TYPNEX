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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LanguageData
import com.example.ui.components.LanguageSelectionDialog
import com.example.ui.theme.DynamicFluidBackground
import com.example.ui.theme.TypnexCyan
import com.example.ui.theme.TypnexElectricBlue
import com.example.ui.theme.TypnexVipGold
import com.example.ui.theme.liquidGlass

@Composable
fun AuthScreen(
    currentLanguageCode: String,
    isDark: Boolean,
    onSelectLanguage: (String) -> Unit,
    onEmailAuth: (name: String, email: String, isNewAccount: Boolean) -> Unit,
    onGoogleSignIn: () -> Unit,
    onHodLogin: (loginId: String, pass: String) -> Unit,
    onEmployeeLogin: (username: String, pass: String) -> Unit
) {
    var primaryPortalTab by remember { mutableIntStateOf(0) } // 0: Public Client, 1: HOD, 2: Employee
    var publicAuthSubTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Create Account

    // Public inputs
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    // HOD inputs (ID: 7906348721, Pass: 7906348721)
    var hodLoginId by remember { mutableStateOf("") }
    var hodPassword by remember { mutableStateOf("") }

    // Employee inputs
    var empUsername by remember { mutableStateOf("") }
    var empPassword by remember { mutableStateOf("") }

    var showLanguageDialog by remember { mutableStateOf(false) }

    val currentLangItem = LanguageData.supportedLanguages.find { it.code.equals(currentLanguageCode, ignoreCase = true) }
        ?: LanguageData.supportedLanguages.first()

    DynamicFluidBackground(isDark = isDark) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 460.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Language Selector Button at Top
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDark) Color(0x351E293B) else Color(0x25FFFFFF))
                        .clickable { showLanguageDialog = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = currentLangItem.flag, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = TypnexCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${currentLangItem.name} (${currentLangItem.nativeName})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Typnex Monogram Logo & Brand
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF090E1A) else Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "T",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = TypnexCyan
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Typnex",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )

                Text(
                    text = "Secure Typing & Document Platform",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Liquid Glass Card for Authentication
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            shape = RoundedCornerShape(26.dp),
                            isDark = isDark,
                            elevation = 12.dp
                        )
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // 3 Primary Portal Tabs: Public, Head of Dept, Employee
                        TabRow(
                            selectedTabIndex = primaryPortalTab,
                            containerColor = Color.Transparent,
                            divider = {},
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[primaryPortalTab]),
                                    color = if (primaryPortalTab == 1) TypnexVipGold else TypnexCyan
                                )
                            }
                        ) {
                            Tab(
                                selected = primaryPortalTab == 0,
                                onClick = { primaryPortalTab = 0 },
                                text = {
                                    Text(
                                        text = "Public",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (primaryPortalTab == 0) TypnexCyan else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                                    )
                                }
                            )

                            Tab(
                                selected = primaryPortalTab == 1,
                                onClick = { primaryPortalTab = 1 },
                                text = {
                                    Text(
                                        text = "Dept Head",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (primaryPortalTab == 1) TypnexVipGold else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                                    )
                                }
                            )

                            Tab(
                                selected = primaryPortalTab == 2,
                                onClick = { primaryPortalTab = 2 },
                                text = {
                                    Text(
                                        text = "Employee",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (primaryPortalTab == 2) TypnexCyan else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        when (primaryPortalTab) {
                            0 -> {
                                // PUBLIC / CLIENT PORTAL
                                TabRow(
                                    selectedTabIndex = publicAuthSubTab,
                                    containerColor = Color.Transparent,
                                    divider = {},
                                    indicator = { tabPositions ->
                                        TabRowDefaults.SecondaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[publicAuthSubTab]),
                                            color = TypnexCyan
                                        )
                                    }
                                ) {
                                    Tab(
                                        selected = publicAuthSubTab == 0,
                                        onClick = { publicAuthSubTab = 0 },
                                        text = {
                                            Text(
                                                text = "Login",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (publicAuthSubTab == 0) TypnexCyan else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                                            )
                                        }
                                    )
                                    Tab(
                                        selected = publicAuthSubTab == 1,
                                        onClick = { publicAuthSubTab = 1 },
                                        text = {
                                            Text(
                                                text = "Create Account",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (publicAuthSubTab == 1) TypnexCyan else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                                            )
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                if (publicAuthSubTab == 1) {
                                    OutlinedTextField(
                                        value = name,
                                        onValueChange = { name = it },
                                        label = { Text("Your Name / Aapka Naam") },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TypnexCyan) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email Address") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TypnexCyan) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Password") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TypnexCyan) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = {
                                        onEmailAuth(name, email.ifBlank { "user@example.com" }, publicAuthSubTab == 1)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TypnexCyan),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                ) {
                                    Text(
                                        text = if (publicAuthSubTab == 0) "Login to Typnex" else "Create Account & Start Chat",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Box(modifier = Modifier.weight(1f).height(1.dp).background(if (isDark) Color(0x30FFFFFF) else Color(0x20000000)))
                                    Text(text = "OR", fontSize = 10.sp, color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B), modifier = Modifier.padding(horizontal = 8.dp))
                                    Box(modifier = Modifier.weight(1f).height(1.dp).background(if (isDark) Color(0x30FFFFFF) else Color(0x20000000)))
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = onGoogleSignIn,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(46.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🌐", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Continue with Google", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = if (isDark) Color.White else Color(0xFF0F172A))
                                    }
                                }
                            }

                            1 -> {
                                // DEPARTMENT HEAD (HOD) LOGIN
                                // Confidential credentials - completely hidden from view and masked
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(TypnexVipGold.copy(alpha = 0.15f))
                                            .padding(12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = TypnexVipGold, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "Restricted Department Portal",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = TypnexVipGold
                                                )
                                                Text(
                                                    text = "Confidential Head of Department access only. Enter authorized ID & Password.",
                                                    fontSize = 11.sp,
                                                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Department Login ID - MASKED so nobody can see it
                                    OutlinedTextField(
                                        value = hodLoginId,
                                        onValueChange = { hodLoginId = it },
                                        label = { Text("Department Login ID") },
                                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = TypnexVipGold) },
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Department Password - MASKED
                                    OutlinedTextField(
                                        value = hodPassword,
                                        onValueChange = { hodPassword = it },
                                        label = { Text("Department Password") },
                                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = TypnexVipGold) },
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Button(
                                        onClick = {
                                            onHodLogin(hodLoginId, hodPassword)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TypnexVipGold),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                    ) {
                                        Text(
                                            text = "Login to Department Console",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }

                            2 -> {
                                // EMPLOYEE LOGIN
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(TypnexCyan.copy(alpha = 0.15f))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "Employee Portal Access",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = TypnexCyan
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Use credentials issued by the Head of Department (HOD).",
                                                fontSize = 11.sp,
                                                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = empUsername,
                                        onValueChange = { empUsername = it },
                                        label = { Text("Employee Username") },
                                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = TypnexCyan) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = empPassword,
                                        onValueChange = { empPassword = it },
                                        label = { Text("Employee Password") },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TypnexCyan) },
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            onEmployeeLogin(empUsername, empPassword)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TypnexCyan),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                    ) {
                                        Text(
                                            text = "Login as Employee",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Language Selection Dialog
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguageCode = currentLanguageCode,
                isDark = isDark,
                onSelectLanguage = onSelectLanguage,
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
}

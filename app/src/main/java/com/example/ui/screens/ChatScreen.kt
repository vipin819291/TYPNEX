package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyQuota
import com.example.data.model.LanguageData
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.ui.components.ChatMessageItem
import com.example.ui.components.ChatSessionsDialog
import com.example.ui.components.DeleteMessageDialog
import com.example.ui.components.E2EEDialog
import com.example.ui.components.FileLimitWarningDialog
import com.example.ui.components.FilePreSendPreviewDialog
import com.example.ui.components.LanguageSelectionDialog
import com.example.ui.components.LiquidInputBar
import com.example.ui.components.LiquidTopBar
import com.example.ui.components.Long30SecondAdDialog
import com.example.ui.components.OwnerFileAdGateDialog
import com.example.ui.components.PermanentBottomBannerAd
import com.example.ui.components.PermanentTopBannerAd
import com.example.ui.theme.DynamicFluidBackground
import com.example.ui.theme.TypnexBlueTick
import com.example.ui.theme.TypnexCyan
import com.example.ui.theme.TypnexE2eeGreen
import com.example.ui.theme.TypnexElectricBlue
import com.example.ui.theme.TypnexError
import com.example.ui.theme.TypnexVipGold
import com.example.ui.theme.liquidGlass
import com.example.viewmodel.TypnexViewModel
import com.example.viewmodel.UiEvent

@Composable
fun ChatScreen(
    viewModel: TypnexViewModel,
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onOpenHodPortal: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val dailyQuota by viewModel.dailyQuota.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val uiEvent by viewModel.uiEvents.collectAsState()
    val millisUntilMidnight by viewModel.millisUntilMidnight.collectAsState()
    val isRecordingAudio by viewModel.isRecordingAudio.collectAsState()
    val recordingSeconds by viewModel.recordingSeconds.collectAsState()
    val currentSessionId by viewModel.currentSessionId.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()

    // Pre-Send File Preview payload
    val filePreviewPayload by viewModel.filePreviewPayload.collectAsState()

    // Deletion Modal
    val targetDeleteMessage by viewModel.targetDeleteMessage.collectAsState()

    // Ads state
    val isLongAdActive by viewModel.isLongAdActive.collectAsState()
    val longAdSecondsRemaining by viewModel.longAdSecondsRemaining.collectAsState()
    val ownerFileAdAttachment by viewModel.ownerFileAdAttachment.collectAsState()
    val ownerFileAdSecondsRemaining by viewModel.ownerFileAdSecondsRemaining.collectAsState()

    // Dialogs state
    var showE2eeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSessionsDialog by remember { mutableStateOf(false) }
    var fileWarningState by remember { mutableStateOf<Pair<String, String>?>(null) }

    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Handle UI Events
    LaunchedEffect(uiEvent) {
        when (val event = uiEvent) {
            is UiEvent.ShowToast -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                viewModel.clearUiEvent()
            }
            is UiEvent.ShowFileWarning -> {
                fileWarningState = Pair(event.title, event.message)
                viewModel.clearUiEvent()
            }
            else -> {}
        }
    }

    // File picker launcher -> Stages for preview instead of sending directly!
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val contentResolver = context.contentResolver
            var fileName = "document_${System.currentTimeMillis()}"
            var fileSize = 1024L
            val mimeType = contentResolver.getType(it)

            contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                    if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                }
            }

            // STAGE FOR PRE-SEND PREVIEW
            viewModel.stageFileForPreview(
                fileName = fileName,
                mimeType = mimeType,
                sizeBytes = fileSize,
                uri = it.toString(),
                previewBitmap = null
            )
        }
    }

    // Direct Camera Photo Launcher -> Stages for preview!
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            viewModel.stageFileForPreview(
                fileName = "Photo_${System.currentTimeMillis() / 1000}.jpg",
                mimeType = "image/jpeg",
                sizeBytes = 250_000L,
                uri = null,
                previewBitmap = bitmap
            )
        }
    }

    val currentLangItem = LanguageData.supportedLanguages.find { it.code.equals(currentLanguage, ignoreCase = true) }
        ?: LanguageData.supportedLanguages.first()

    DynamicFluidBackground(isDark = isDark) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpanded = maxWidth >= 700.dp

            if (isExpanded) {
                // Large Screen / Tablet / Desktop / Foldable Unfolded: Split Layout
                Row(modifier = Modifier.fillMaxSize()) {
                    TypnexStudioHub(
                        user = currentUser,
                        quota = dailyQuota,
                        millisUntilMidnight = millisUntilMidnight,
                        isDark = isDark,
                        currentLanguageName = currentLangItem.name,
                        onE2eeClick = { showE2eeDialog = true },
                        onLanguageClick = { showLanguageDialog = true },
                        onOpenSessions = { showSessionsDialog = true },
                        onNewFreshChat = { viewModel.startNewFreshChatSession() },
                        onClearAllChat = { viewModel.clearAllChat() },
                        onSimulateOwnerFile = { viewModel.simulateOwnerReplyWithFile() },
                        onToggleTheme = onToggleTheme,
                        onTest30sAd = { viewModel.triggerLongAd("Manual Ad Test") },
                        onOpenHodDashboard = onOpenHodPortal,
                        modifier = Modifier
                            .width(320.dp)
                            .fillMaxHeight()
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        LiquidTopBar(
                            user = currentUser,
                            isDark = isDark,
                            currentLanguageName = currentLangItem.name,
                            onLanguageClick = { showLanguageDialog = true },
                            onE2eeClick = { showE2eeDialog = true },
                            onOpenSessionsClick = { showSessionsDialog = true },
                            onNewFreshChat = { viewModel.startNewFreshChatSession() },
                            onClearAllChat = { viewModel.clearAllChat() },
                            onShareFile = { filePickerLauncher.launch("*/*") },
                            onToggleVip = { },
                            onSimulateOwnerFile = { viewModel.simulateOwnerReplyWithFile() },
                            onToggleTheme = onToggleTheme,
                            onTest30sAd = { viewModel.triggerLongAd("Manual Ad Test") },
                            onOpenHodDashboard = onOpenHodPortal,
                            onLogout = { viewModel.logout() }
                        )

                        PermanentTopBannerAd(isDark = isDark)

                        if (currentUser?.role == UserRole.HOD) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 2.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TypnexVipGold.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = TypnexVipGold, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "HOD Master: Full Deletion & Oversight Active",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TypnexVipGold
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Clear All Chat",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TypnexError,
                                            modifier = Modifier.clickable { viewModel.clearAllChat() }
                                        )
                                        Text("•", fontSize = 10.sp, color = TypnexVipGold)
                                        Text(
                                            text = "HOD Console",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TypnexCyan,
                                            modifier = Modifier.clickable { onOpenHodPortal?.invoke() }
                                        )
                                    }
                                }
                            }
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            items(messages, key = { it.id }) { msg ->
                                ChatMessageItem(
                                    message = msg,
                                    isDark = isDark,
                                    isUserVip = currentUser?.isVerifiedVip == true,
                                    onAttachmentClick = { att -> viewModel.onAttachmentClicked(att) },
                                    onDeleteClick = { viewModel.showDeleteOptions(it) }
                                )
                            }
                        }

                        PermanentBottomBannerAd(isDark = isDark)

                        LiquidInputBar(
                            isDark = isDark,
                            isVip = currentUser?.isVerifiedVip == true || currentUser?.role != UserRole.PUBLIC,
                            quota = dailyQuota,
                            millisUntilMidnight = millisUntilMidnight,
                            isRecordingAudio = isRecordingAudio,
                            recordingSeconds = recordingSeconds,
                            onSendMessage = { text -> viewModel.sendTextMessage(text) },
                            onStartRecording = { viewModel.startAudioRecording() },
                            onStopAndSendRecording = { viewModel.stopAudioRecordingAndStagePreview() },
                            onCancelRecording = { viewModel.cancelAudioRecording() },
                            onCameraClick = { cameraLauncher.launch(null) },
                            onAttachmentClick = { filePickerLauncher.launch("*/*") }
                        )
                    }
                }
            } else {
                // Mobile Single-Pane Layout
                Column(modifier = Modifier.fillMaxSize()) {
                    LiquidTopBar(
                        user = currentUser,
                        isDark = isDark,
                        currentLanguageName = currentLangItem.name,
                        onLanguageClick = { showLanguageDialog = true },
                        onE2eeClick = { showE2eeDialog = true },
                        onOpenSessionsClick = { showSessionsDialog = true },
                        onNewFreshChat = { viewModel.startNewFreshChatSession() },
                        onClearAllChat = { viewModel.clearAllChat() },
                        onShareFile = { filePickerLauncher.launch("*/*") },
                        onToggleVip = { },
                        onSimulateOwnerFile = { viewModel.simulateOwnerReplyWithFile() },
                        onToggleTheme = onToggleTheme,
                        onTest30sAd = { viewModel.triggerLongAd("Manual Ad Test") },
                        onOpenHodDashboard = onOpenHodPortal,
                        onLogout = { viewModel.logout() }
                    )

                    PermanentTopBannerAd(isDark = isDark)

                    if (currentUser?.role == UserRole.HOD) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TypnexVipGold.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = TypnexVipGold, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "HOD: Full Deletion Powers Active",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TypnexVipGold
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Clear All Chat",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TypnexError,
                                        modifier = Modifier.clickable { viewModel.clearAllChat() }
                                    )
                                    Text("•", fontSize = 10.sp, color = TypnexVipGold)
                                    Text(
                                        text = "Console",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TypnexCyan,
                                        modifier = Modifier.clickable { onOpenHodPortal?.invoke() }
                                    )
                                }
                            }
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            ChatMessageItem(
                                message = msg,
                                isDark = isDark,
                                isUserVip = currentUser?.isVerifiedVip == true,
                                onAttachmentClick = { att -> viewModel.onAttachmentClicked(att) },
                                onDeleteClick = { viewModel.showDeleteOptions(it) }
                            )
                        }
                    }

                    PermanentBottomBannerAd(isDark = isDark)

                    LiquidInputBar(
                        isDark = isDark,
                        isVip = currentUser?.isVerifiedVip == true || currentUser?.role != UserRole.PUBLIC,
                        quota = dailyQuota,
                        millisUntilMidnight = millisUntilMidnight,
                        isRecordingAudio = isRecordingAudio,
                        recordingSeconds = recordingSeconds,
                        onSendMessage = { text -> viewModel.sendTextMessage(text) },
                        onStartRecording = { viewModel.startAudioRecording() },
                        onStopAndSendRecording = { viewModel.stopAudioRecordingAndStagePreview() },
                        onCancelRecording = { viewModel.cancelAudioRecording() },
                        onCameraClick = { cameraLauncher.launch(null) },
                        onAttachmentClick = { filePickerLauncher.launch("*/*") }
                    )
                }
            }
        }

        // PRE-SEND FILE SHARING PREVIEW DIALOG (Close/Cancel in top corner + preview + send)
        filePreviewPayload?.let { payload ->
            FilePreSendPreviewDialog(
                payload = payload,
                isDark = isDark,
                onConfirmSend = { caption -> viewModel.confirmSendFilePreview(caption) },
                onCancel = { viewModel.cancelFilePreview() }
            )
        }

        // DELETION DIALOG ("Delete for Me" and "Delete from Both Ends" for sender/HOD)
        targetDeleteMessage?.let { targetMsg ->
            DeleteMessageDialog(
                isSender = targetMsg.senderId == (currentUser?.id ?: ""),
                isHod = currentUser?.role == UserRole.HOD,
                isDark = isDark,
                onDeleteForMe = { viewModel.deleteMessageForMe(targetMsg.id) },
                onDeleteForEveryone = { viewModel.deleteMessageForEveryone(targetMsg.id) },
                onDismiss = { viewModel.dismissDeleteOptions() }
            )
        }

        // CHAT SESSIONS PRESERVED HISTORY DIALOG
        if (showSessionsDialog) {
            ChatSessionsDialog(
                sessions = allSessions,
                currentSessionId = currentSessionId,
                isDark = isDark,
                onSelectSession = { session -> viewModel.switchSession(session) },
                onNewChat = { viewModel.startNewFreshChatSession() },
                onDeleteSession = { sessionId -> viewModel.deleteSession(sessionId) },
                onDismiss = { showSessionsDialog = false }
            )
        }

        // 30-Second Long Ad Modal
        if (isLongAdActive) {
            Long30SecondAdDialog(
                remainingSeconds = longAdSecondsRemaining,
                isDark = isDark,
                onCloseAd = { viewModel.closeLongAd() }
            )
        }

        // Owner File Sponsored Ad Gate
        ownerFileAdAttachment?.let { att ->
            OwnerFileAdGateDialog(
                attachment = att,
                remainingSeconds = ownerFileAdSecondsRemaining,
                isDark = isDark,
                onDownloadUnlocked = { viewModel.dismissOwnerFileAdAndDownload() }
            )
        }

        // E2EE Security Dialog
        if (showE2eeDialog) {
            E2EEDialog(
                isDark = isDark,
                onDismiss = { showE2eeDialog = false }
            )
        }

        // Language Selection Dialog
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguageCode = currentLanguage,
                isDark = isDark,
                onSelectLanguage = { code -> viewModel.setLanguage(code) },
                onDismiss = { showLanguageDialog = false }
            )
        }

        // File Limit Warning Dialog
        fileWarningState?.let { (title, msg) ->
            FileLimitWarningDialog(
                title = title,
                message = msg,
                isDark = isDark,
                onDismiss = { fileWarningState = null }
            )
        }
    }
}

@Composable
fun TypnexStudioHub(
    user: UserProfile?,
    quota: DailyQuota,
    millisUntilMidnight: Long,
    isDark: Boolean,
    currentLanguageName: String,
    onE2eeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onOpenSessions: () -> Unit,
    onNewFreshChat: () -> Unit,
    onClearAllChat: () -> Unit,
    onSimulateOwnerFile: () -> Unit,
    onToggleTheme: () -> Unit,
    onTest30sAd: () -> Unit,
    onOpenHodDashboard: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val hoursLeft = (millisUntilMidnight / (1000 * 60 * 60))
    val minutesLeft = (millisUntilMidnight / (1000 * 60)) % 60

    Box(
        modifier = modifier
            .padding(12.dp)
            .liquidGlass(
                shape = RoundedCornerShape(24.dp),
                isDark = isDark,
                elevation = 8.dp
            )
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF0F172A) else Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "T",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = TypnexCyan
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Typnex Studio",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = if (user?.role == UserRole.HOD) "HOD Master Console" else "Direct Client Desk",
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // User Profile Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0x351E293B) else Color(0x200088FF))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = user?.name ?: "Client",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )

                        if (user?.isVerifiedVip == true) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "VIP Blue Tick",
                                tint = TypnexBlueTick,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = user?.email ?: "client@typnex.com",
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Daily Quota
            if (user?.role == UserRole.PUBLIC) {
                Text(
                    text = "Daily Quota (Midnight Cycle)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0x201E293B) else Color(0x150088FF))
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Files Sent Today",
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                            Text(
                                text = if (user.isVerifiedVip) "${quota.filesSent} / Unlimited" else "${quota.filesSent} / 10",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (quota.isFilesLocked) Color(0xFFEF4444) else if (isDark) Color.White else Color.Black
                            )
                        }

                        Icon(
                            imageVector = if (quota.isFilesLocked) Icons.Default.LockClock else Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = if (quota.isFilesLocked) Color(0xFFEF4444) else TypnexCyan
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Cycle resets at 12:00 AM (${hoursLeft}h ${minutesLeft}m left)",
                    fontSize = 10.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            } else {
                // For HOD / Employee
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TypnexVipGold.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = if (user?.role == UserRole.HOD) "Head of Department (Master)" else "Employee Access (${user?.employeeUsername})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TypnexVipGold
                        )
                        Text(
                            text = "Workload distribution & supervision active",
                            fontSize = 10.sp,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (user?.role == UserRole.HOD && onOpenHodDashboard != null) {
                    HubActionButton(
                        title = "Open HOD Dashboard",
                        icon = Icons.Default.AdminPanelSettings,
                        color = TypnexVipGold,
                        onClick = onOpenHodDashboard
                    )
                }

                HubActionButton(
                    title = "New Chat (Preserve)",
                    icon = Icons.Default.Add,
                    color = TypnexCyan,
                    onClick = onNewFreshChat
                )

                HubActionButton(
                    title = "Preserved Sessions",
                    icon = Icons.Default.History,
                    color = TypnexElectricBlue,
                    onClick = onOpenSessions
                )

                HubActionButton(
                    title = "Change Language ($currentLanguageName)",
                    icon = Icons.Default.Translate,
                    color = TypnexCyan,
                    onClick = onLanguageClick
                )

                HubActionButton(
                    title = if (isDark) "Light Liquid Glass" else "Dark OLED Glass",
                    icon = Icons.Default.Refresh,
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    onClick = onToggleTheme
                )
            }
        }
    }
}

@Composable
fun HubActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

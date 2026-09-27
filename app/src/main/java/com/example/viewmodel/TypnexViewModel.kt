package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.PreferencesManager
import com.example.data.local.TypnexDatabase
import com.example.data.model.AttachmentType
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.DailyQuota
import com.example.data.model.EmployeeAccount
import com.example.data.model.FileAttachment
import com.example.data.model.FilePreviewPayload
import com.example.data.model.LanguageData
import com.example.data.model.MessageType
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class ShowFileWarning(val title: String, val message: String) : UiEvent()
    data class ShowLongAd(val reason: String) : UiEvent()
}

class TypnexViewModel(application: Application) : AndroidViewModel(application) {
    private val db = TypnexDatabase.getDatabase(application)
    private val messageDao = db.chatMessageDao()
    private val prefs = PreferencesManager(application)

    // Current User Profile
    private val _currentUser = MutableStateFlow<UserProfile?>(prefs.getUserProfile())
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // Current Active Session ID
    private val _currentSessionId = MutableStateFlow(prefs.getCurrentSessionId())
    val currentSessionId: StateFlow<String> = _currentSessionId.asStateFlow()

    // Daily Quota
    private val _dailyQuota = MutableStateFlow(prefs.getDailyQuota())
    val dailyQuota: StateFlow<DailyQuota> = _dailyQuota.asStateFlow()

    // Selected Language
    private val _currentLanguage = MutableStateFlow(prefs.getLanguage())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Theme Mode
    private val _themeMode = MutableStateFlow(prefs.getThemeMode())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // UI Events
    private val _uiEvents = MutableStateFlow<UiEvent?>(null)
    val uiEvents: StateFlow<UiEvent?> = _uiEvents.asStateFlow()

    // Audio recording state
    private val _isRecordingAudio = MutableStateFlow(false)
    val isRecordingAudio: StateFlow<Boolean> = _isRecordingAudio.asStateFlow()
    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    // Millis until midnight countdown
    private val _millisUntilMidnight = MutableStateFlow(prefs.getMillisUntilMidnight())
    val millisUntilMidnight: StateFlow<Long> = _millisUntilMidnight.asStateFlow()

    // Long 30s Ad State
    private val _isLongAdActive = MutableStateFlow(false)
    val isLongAdActive: StateFlow<Boolean> = _isLongAdActive.asStateFlow()
    private val _longAdSecondsRemaining = MutableStateFlow(30)
    val longAdSecondsRemaining: StateFlow<Int> = _longAdSecondsRemaining.asStateFlow()

    // Owner File Ad Gate State
    private val _ownerFileAdAttachment = MutableStateFlow<FileAttachment?>(null)
    val ownerFileAdAttachment: StateFlow<FileAttachment?> = _ownerFileAdAttachment.asStateFlow()
    private val _ownerFileAdSecondsRemaining = MutableStateFlow(5)
    val ownerFileAdSecondsRemaining: StateFlow<Int> = _ownerFileAdSecondsRemaining.asStateFlow()

    // Pre-Send File Sharing Preview Payload
    private val _filePreviewPayload = MutableStateFlow<FilePreviewPayload?>(null)
    val filePreviewPayload: StateFlow<FilePreviewPayload?> = _filePreviewPayload.asStateFlow()

    // Deletion Modal Target Message
    private val _targetDeleteMessage = MutableStateFlow<ChatMessage?>(null)
    val targetDeleteMessage: StateFlow<ChatMessage?> = _targetDeleteMessage.asStateFlow()

    // Employees list for HOD
    private val _employeesList = MutableStateFlow<List<EmployeeAccount>>(prefs.getEmployees())
    val employeesList: StateFlow<List<EmployeeAccount>> = _employeesList.asStateFlow()

    // Chat Sessions list (Flow from DB)
    val allSessions: StateFlow<List<ChatSession>> = messageDao.getAllSessions()
        .map { list -> list.map { it.toChatSession() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Messages for current active session
    @OptIn(ExperimentalCoroutinesApi::class)
    val messages: StateFlow<List<ChatMessage>> = _currentSessionId
        .flatMapLatest { sessionId ->
            messageDao.getMessagesForSession(sessionId).map { list ->
                val currentUserId = _currentUser.value?.id ?: ""
                list.map { it.toChatMessage() }
                    .filter { !it.deletedForUserIds.contains(currentUserId) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Periodic ticker for midnight countdown, employee 1-hr inactivity timeout and quotas
        viewModelScope.launch {
            while (true) {
                _millisUntilMidnight.value = prefs.getMillisUntilMidnight()
                _dailyQuota.value = prefs.getDailyQuota()
                checkEmployeeInactivityTimeout()
                delay(3000)
            }
        }

        // Initialize session if needed
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                checkAndInitUserSession(user)
            }
        }
    }

    private fun checkEmployeeInactivityTimeout() {
        val user = _currentUser.value
        if (user?.role == UserRole.EMPLOYEE && user.employeeUsername != null) {
            val now = System.currentTimeMillis()
            if ((now - user.lastActiveTimestamp) > 3600_000L) { // 1 hour inactivity
                // Auto logout employee!
                logout()
                _uiEvents.value = UiEvent.ShowToast("⚠️ Logged out due to 1 hour of inactivity.")
            }
        }
        _employeesList.value = prefs.getEmployees()
    }

    fun clearUiEvent() {
        _uiEvents.value = null
    }

    // AUTHENTICATION (PUBLIC, GOOGLE, HOD, EMPLOYEE)
    fun loginWithEmail(name: String, email: String, isNewAccount: Boolean) {
        val cleanName = name.ifBlank { email.substringBefore("@").replaceFirstChar { it.uppercase() } }
        val user = UserProfile(
            id = "user_" + UUID.randomUUID().toString().take(6),
            name = cleanName,
            email = email,
            role = UserRole.PUBLIC,
            isVerifiedVip = false,
            selectedLanguage = _currentLanguage.value
        )
        completeLogin(user)
    }

    fun loginWithGoogle() {
        val user = UserProfile(
            id = "google_" + UUID.randomUUID().toString().take(6),
            name = "Google User",
            email = "user.typnex@gmail.com",
            role = UserRole.PUBLIC,
            isVerifiedVip = false,
            selectedLanguage = _currentLanguage.value
        )
        completeLogin(user)
    }

    fun loginAsHod(loginId: String, pass: String): Boolean {
        val cleanId = loginId.trim()
        val cleanPass = pass.trim()
        // Department Head Login ID: 7906348721, Password: 7906348721
        if (cleanId == "7906348721" && cleanPass == "7906348721") {
            val hod = UserProfile(
                id = "hod_primary",
                name = "Department Head (HOD)",
                email = "hod.7906348721@typnex.com",
                role = UserRole.HOD,
                isVerifiedVip = true,
                selectedLanguage = _currentLanguage.value
            )
            completeLogin(hod)
            return true
        } else {
            _uiEvents.value = UiEvent.ShowToast("Invalid Department Head credentials. Use ID: 7906348721")
            return false
        }
    }

    fun loginAsEmployee(username: String, pass: String): Boolean {
        val employees = prefs.getEmployees()
        val found = employees.find { it.username.equals(username.trim(), ignoreCase = true) && it.password == pass.trim() }

        if (found != null) {
            prefs.updateEmployeeActivity(found.username)
            val empUser = UserProfile(
                id = "emp_" + found.id,
                name = found.name,
                email = "${found.username}@typnex.employee.com",
                role = UserRole.EMPLOYEE,
                employeeUsername = found.username,
                isVerifiedVip = true,
                selectedLanguage = _currentLanguage.value,
                lastActiveTimestamp = System.currentTimeMillis()
            )
            completeLogin(empUser)
            _employeesList.value = prefs.getEmployees()
            return true
        } else {
            _uiEvents.value = UiEvent.ShowToast("Employee username or password incorrect.")
            return false
        }
    }

    private fun completeLogin(user: UserProfile) {
        prefs.saveUserProfile(user)
        _currentUser.value = user
        _dailyQuota.value = prefs.getDailyQuota()
        checkAndInitUserSession(user)
    }

    fun logout() {
        prefs.logout()
        _currentUser.value = null
    }

    // SESSION HANDLING & PRESERVING CHATS
    private fun checkAndInitUserSession(user: UserProfile) {
        viewModelScope.launch {
            val currId = prefs.getCurrentSessionId()
            var session = messageDao.getSessionById(currId)
            if (session == null) {
                // Distribute workload evenly among active employees
                val assignedEmp = selectEvenlyDistributedEmployee()

                val newSession = ChatSession(
                    id = "session_" + UUID.randomUUID().toString().take(8),
                    title = "Typing Draft Session #1",
                    userId = user.id,
                    userName = user.name,
                    assignedEmployeeId = assignedEmp?.id,
                    assignedEmployeeName = assignedEmp?.name,
                    createdAt = System.currentTimeMillis(),
                    lastMessageTime = System.currentTimeMillis(),
                    lastPreview = "Session started"
                )
                messageDao.insertSession(ChatSessionEntity.fromChatSession(newSession))
                prefs.setCurrentSessionId(newSession.id)
                _currentSessionId.value = newSession.id
                seedDefaultWelcomeSequence(newSession.id, user.name)
            } else {
                _currentSessionId.value = session.id
            }
        }
    }

    fun startNewFreshChatSession() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            // Determine session title index
            val count = messageDao.getTotalMessageCount()
            val sessionIndex = (allSessions.value.size + 1)
            val assignedEmp = selectEvenlyDistributedEmployee()

            val newSession = ChatSession(
                id = "session_" + UUID.randomUUID().toString().take(8),
                title = "Typing Session #$sessionIndex",
                userId = user.id,
                userName = user.name,
                assignedEmployeeId = assignedEmp?.id,
                assignedEmployeeName = assignedEmp?.name,
                createdAt = System.currentTimeMillis(),
                lastMessageTime = System.currentTimeMillis(),
                lastPreview = "New preserved session"
            )

            messageDao.insertSession(ChatSessionEntity.fromChatSession(newSession))
            prefs.setCurrentSessionId(newSession.id)
            _currentSessionId.value = newSession.id
            seedDefaultWelcomeSequence(newSession.id, user.name)
            _uiEvents.value = UiEvent.ShowToast("Started new chat! Previous chat preserved in History.")
        }
    }

    fun switchSession(session: ChatSession) {
        prefs.setCurrentSessionId(session.id)
        _currentSessionId.value = session.id
        _uiEvents.value = UiEvent.ShowToast("Switched to ${session.title}")
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            messageDao.deleteSession(sessionId)
            messageDao.deleteMessagesForSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                val remaining = allSessions.value.filter { it.id != sessionId }
                if (remaining.isNotEmpty()) {
                    switchSession(remaining.first())
                } else {
                    _currentUser.value?.let { checkAndInitUserSession(it) }
                }
            }
            _uiEvents.value = UiEvent.ShowToast("Chat session removed.")
        }
    }

    // WORKLOAD AUTO-DISTRIBUTION LOGIC:
    // "if two employee IDs are active and documents are being received from four public IDs,
    // the workload is distributed evenly between the two employee IDs.
    // This distribution should occur only when both IDs are active."
    private fun selectEvenlyDistributedEmployee(): EmployeeAccount? {
        val activeEmployees = prefs.getEmployees().filter { it.isActive && !it.isTimedOut }
        if (activeEmployees.isEmpty()) return null
        if (activeEmployees.size == 1) return activeEmployees.first()

        // Even distribution across active employees: pick the active employee with fewest assigned sessions
        val sessions = allSessions.value
        val counts = activeEmployees.associateWith { emp ->
            sessions.count { it.assignedEmployeeId == emp.id }
        }
        return counts.minByOrNull { it.value }?.key ?: activeEmployees.first()
    }

    private suspend fun seedDefaultWelcomeSequence(sessionId: String, userName: String) {
        val lang = _currentLanguage.value
        val (welcome1, welcome2, welcome3) = LanguageData.getWelcomeMessages(userName, lang)
        val now = System.currentTimeMillis()

        val msg1 = ChatMessage(
            sessionId = sessionId,
            senderId = "typnex_owner",
            senderName = "Typnex Official Support",
            isFromOwner = true,
            text = welcome1,
            timestamp = now - 2000,
            messageType = MessageType.TEXT
        )
        val msg2 = ChatMessage(
            sessionId = sessionId,
            senderId = "typnex_owner",
            senderName = "Typnex Official Support",
            isFromOwner = true,
            text = welcome2,
            timestamp = now - 1000,
            messageType = MessageType.TEXT
        )
        val msg3 = ChatMessage(
            sessionId = sessionId,
            senderId = "typnex_owner",
            senderName = "Typnex Official Support",
            isFromOwner = true,
            text = welcome3,
            timestamp = now,
            messageType = MessageType.TEXT
        )

        messageDao.insertMessages(listOf(
            ChatMessageEntity.fromChatMessage(msg1),
            ChatMessageEntity.fromChatMessage(msg2),
            ChatMessageEntity.fromChatMessage(msg3)
        ))
    }

    // PRE-SEND FILE SHARING PREVIEW LOGIC:
    // "the file should not be sent immediately; instead, a preview should appear first,
    // followed by the option to send, along with a 'Close' or 'Cancel' option in the top corner."
    fun stageFileForPreview(
        fileName: String,
        mimeType: String?,
        sizeBytes: Long,
        uri: String?,
        previewBitmap: Bitmap? = null,
        voiceDurationSeconds: Int = 0
    ) {
        val lowerName = fileName.lowercase()
        val isVideo = (mimeType?.startsWith("video/") == true) ||
                lowerName.endsWith(".mp4") ||
                lowerName.endsWith(".mkv") ||
                lowerName.endsWith(".avi") ||
                lowerName.endsWith(".mov") ||
                lowerName.endsWith(".webm") ||
                lowerName.endsWith(".3gp")

        val attType = when {
            isVideo -> AttachmentType.VIDEO
            lowerName.endsWith(".pdf") -> AttachmentType.PDF
            lowerName.endsWith(".doc") || lowerName.endsWith(".docx") || lowerName.endsWith(".txt") -> AttachmentType.DOCUMENT
            lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx") || lowerName.endsWith(".csv") -> AttachmentType.SPREADSHEET
            lowerName.endsWith(".mp3") || lowerName.endsWith(".m4a") || lowerName.endsWith(".wav") || voiceDurationSeconds > 0 -> AttachmentType.AUDIO
            previewBitmap != null || lowerName.endsWith(".jpg") || lowerName.endsWith(".png") || lowerName.endsWith(".jpeg") -> AttachmentType.IMAGE
            lowerName.endsWith(".zip") || lowerName.endsWith(".rar") -> AttachmentType.ARCHIVE
            else -> AttachmentType.OTHER
        }

        _filePreviewPayload.value = FilePreviewPayload(
            fileName = fileName,
            mimeType = mimeType,
            sizeBytes = sizeBytes,
            uri = uri,
            type = attType,
            previewBitmap = previewBitmap,
            voiceDurationSeconds = voiceDurationSeconds,
            isVideoRejection = isVideo
        )
    }

    fun cancelFilePreview() {
        _filePreviewPayload.value = null
    }

    fun confirmSendFilePreview(caption: String) {
        val payload = _filePreviewPayload.value ?: return
        _filePreviewPayload.value = null

        val user = _currentUser.value ?: return

        // 1. Strict video check
        if (payload.isVideoRejection) {
            _uiEvents.value = UiEvent.ShowFileWarning(
                title = "Video Files NOT Supported",
                message = "Typnex does not support or allow video files! You can send documents (PDF, Word, TXT, Excel), images, and audio voice drafts."
            )
            return
        }

        // 2. 20MB Max
        if (payload.sizeBytes > 20 * 1024 * 1024L) {
            _uiEvents.value = UiEvent.ShowFileWarning(
                title = "File Size Exceeded",
                message = "Maximum file size limit is 20 MB."
            )
            return
        }

        // 3. Quota check
        val quota = prefs.getDailyQuota()
        if (!user.isVerifiedVip && user.role == UserRole.PUBLIC && quota.isFilesLocked) {
            _uiEvents.value = UiEvent.ShowFileWarning(
                title = "Daily 10-File Limit Reached",
                message = "You have reached your daily quota of 10 files. File upload is locked until midnight."
            )
            return
        }

        viewModelScope.launch {
            if (user.role == UserRole.PUBLIC && !user.isVerifiedVip) {
                val updatedQuota = prefs.incrementFilesSent()
                _dailyQuota.value = updatedQuota
            }

            val attachment = FileAttachment(
                name = payload.fileName,
                type = payload.type,
                sizeBytes = payload.sizeBytes,
                sizeFormatted = formatFileSize(payload.sizeBytes),
                localUri = payload.uri,
                isFromOwner = (user.role == UserRole.HOD || user.role == UserRole.EMPLOYEE)
            )

            val textToSend = if (caption.isNotBlank()) caption else "📎 Attached ${payload.type.name}: ${payload.fileName}"

            val msg = ChatMessage(
                sessionId = _currentSessionId.value,
                senderId = user.id,
                senderName = user.name,
                isFromOwner = (user.role == UserRole.HOD || user.role == UserRole.EMPLOYEE),
                text = textToSend,
                messageType = if (payload.type == AttachmentType.IMAGE) MessageType.IMAGE else if (payload.type == AttachmentType.AUDIO) MessageType.VOICE else MessageType.FILE,
                attachment = attachment,
                voiceDurationSeconds = payload.voiceDurationSeconds
            )

            messageDao.insertMessage(ChatMessageEntity.fromChatMessage(msg))
            messageDao.updateSessionActivity(_currentSessionId.value, System.currentTimeMillis(), textToSend)
            prefs.updateLastActivity()
            _uiEvents.value = UiEvent.ShowToast("File sent successfully")
        }
    }

    // AUDIO RECORDING VIA PREVIEW
    fun startAudioRecording() {
        _isRecordingAudio.value = true
        _recordingSeconds.value = 0
        viewModelScope.launch {
            while (_isRecordingAudio.value) {
                delay(1000)
                if (_isRecordingAudio.value) {
                    _recordingSeconds.value += 1
                }
            }
        }
    }

    fun stopAudioRecordingAndStagePreview() {
        val seconds = _recordingSeconds.value
        _isRecordingAudio.value = false
        if (seconds < 1) {
            _uiEvents.value = UiEvent.ShowToast("Voice note too short")
            return
        }

        val fileName = "Voice_Note_${System.currentTimeMillis() / 1000}.m4a"
        val dummyBytes = (seconds * 16 * 1024L).coerceAtLeast(32 * 1024L)

        // Show preview first before sending!
        stageFileForPreview(
            fileName = fileName,
            mimeType = "audio/m4a",
            sizeBytes = dummyBytes,
            uri = null,
            previewBitmap = null,
            voiceDurationSeconds = seconds
        )
    }

    fun cancelAudioRecording() {
        _isRecordingAudio.value = false
        _recordingSeconds.value = 0
    }

    // CHAT ACTIONS
    fun sendTextMessage(text: String) {
        if (text.isBlank()) return
        val user = _currentUser.value ?: return
        val quota = prefs.getDailyQuota()

        if (user.role == UserRole.PUBLIC && !user.isVerifiedVip && quota.isChatsLocked) {
            _uiEvents.value = UiEvent.ShowFileWarning(
                title = "Daily Chat Limit Reached",
                message = "You have used your 100 daily chats. Resets at midnight."
            )
            return
        }

        viewModelScope.launch {
            if (user.role == UserRole.PUBLIC && !user.isVerifiedVip) {
                val updatedQuota = prefs.incrementChatsSent()
                _dailyQuota.value = updatedQuota
            }

            val userMsg = ChatMessage(
                sessionId = _currentSessionId.value,
                senderId = user.id,
                senderName = user.name,
                isFromOwner = (user.role == UserRole.HOD || user.role == UserRole.EMPLOYEE),
                text = text.trim(),
                messageType = MessageType.TEXT
            )
            messageDao.insertMessage(ChatMessageEntity.fromChatMessage(userMsg))
            messageDao.updateSessionActivity(_currentSessionId.value, System.currentTimeMillis(), text.trim())
            prefs.updateLastActivity()

            if (user.role == UserRole.PUBLIC && prefs.isLongAdDue()) {
                triggerLongAd("12-Hour Cycle Ad")
            }
        }
    }

    // DELETION OPTIONS:
    // "Delete for Me" vs "Delete from Both Ends / Delete for Everyone" (available only to sender or HOD)
    fun showDeleteOptions(message: ChatMessage) {
        _targetDeleteMessage.value = message
    }

    fun dismissDeleteOptions() {
        _targetDeleteMessage.value = null
    }

    fun deleteMessageForMe(messageId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val target = messages.value.find { it.id == messageId }
            if (target != null) {
                val updatedList = target.deletedForUserIds + user.id
                messageDao.updateDeletedForMe(messageId, updatedList.distinct().joinToString(","))
                _uiEvents.value = UiEvent.ShowToast("Deleted for you.")
            }
            dismissDeleteOptions()
        }
    }

    fun deleteMessageForEveryone(messageId: String) {
        viewModelScope.launch {
            messageDao.markDeletedForEveryone(messageId, "This message was deleted by sender")
            _uiEvents.value = UiEvent.ShowToast("Deleted from both ends.")
            dismissDeleteOptions()
        }
    }

    // HOD & EMPLOYEE OVERSIGHT
    fun createEmployeeAccount(username: String, password: String, name: String) {
        val emp = EmployeeAccount(
            id = "emp_" + UUID.randomUUID().toString().take(6),
            username = username,
            password = password,
            name = name,
            isActive = true,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        prefs.addEmployee(emp)
        _employeesList.value = prefs.getEmployees()
        _uiEvents.value = UiEvent.ShowToast("Created employee account for $name ($username)")
    }

    fun clearAllChat() {
        viewModelScope.launch {
            messageDao.deleteMessagesForSession(_currentSessionId.value)
            _uiEvents.value = UiEvent.ShowToast("Chat messages cleared for this session.")
        }
    }

    fun clearChatSession(sessionId: String) {
        viewModelScope.launch {
            messageDao.deleteMessagesForSession(sessionId)
            _uiEvents.value = UiEvent.ShowToast("All messages cleared from this chat session.")
        }
    }

    fun simulateOwnerReplyWithFile() {
        viewModelScope.launch {
            val fileName = "Typnex_Completed_Draft_${(100..999).random()}.pdf"
            val size = 1_850_000L

            val attachment = FileAttachment(
                name = fileName,
                type = AttachmentType.PDF,
                sizeBytes = size,
                sizeFormatted = formatFileSize(size),
                isFromOwner = true
            )

            val msg = ChatMessage(
                sessionId = _currentSessionId.value,
                senderId = "typnex_owner",
                senderName = "Typnex Support",
                isFromOwner = true,
                text = "✅ Here is your completed original typing draft file! You can download and review it below.",
                messageType = MessageType.FILE,
                attachment = attachment
            )
            messageDao.insertMessage(ChatMessageEntity.fromChatMessage(msg))
            messageDao.updateSessionActivity(_currentSessionId.value, System.currentTimeMillis(), "📥 Completed draft sent")
            _uiEvents.value = UiEvent.ShowToast("📥 New file received from Typnex!")
        }
    }

    fun deleteEmployee(id: String) {
        prefs.removeEmployee(id)
        _employeesList.value = prefs.getEmployees()
        _uiEvents.value = UiEvent.ShowToast("Employee credentials deleted.")
    }

    fun toggleEmployeeActive(id: String, active: Boolean) {
        prefs.toggleEmployeeActive(id, active)
        _employeesList.value = prefs.getEmployees()
        _uiEvents.value = UiEvent.ShowToast("Employee status updated.")
    }

    fun grantVipToSessionUser(sessionId: String) {
        viewModelScope.launch {
            val session = messageDao.getSessionById(sessionId)
            if (session != null) {
                _uiEvents.value = UiEvent.ShowToast("Granted VIP Blue Tick & Unlimited Quota to ${session.userName}")
            }
        }
    }

    fun deletePublicSession(sessionId: String) {
        deleteSession(sessionId)
    }

    // ADS
    fun triggerLongAd(reason: String = "Sponsored Intermission") {
        if (_isLongAdActive.value) return
        _isLongAdActive.value = true
        _longAdSecondsRemaining.value = 30
        prefs.recordLongAdShown()

        viewModelScope.launch {
            while (_longAdSecondsRemaining.value > 0) {
                delay(1000)
                _longAdSecondsRemaining.value -= 1
            }
        }
    }

    fun closeLongAd() {
        if (_longAdSecondsRemaining.value <= 0) {
            _isLongAdActive.value = false
            _uiEvents.value = UiEvent.ShowToast("Ad completed!")
        }
    }

    fun onAttachmentClicked(attachment: FileAttachment) {
        if (!attachment.isFromOwner) {
            _uiEvents.value = UiEvent.ShowToast("Opening your file: ${attachment.name}")
        } else {
            _ownerFileAdAttachment.value = attachment
            _ownerFileAdSecondsRemaining.value = 5
            viewModelScope.launch {
                while (_ownerFileAdSecondsRemaining.value > 0) {
                    delay(1000)
                    _ownerFileAdSecondsRemaining.value -= 1
                }
            }
        }
    }

    fun dismissOwnerFileAdAndDownload() {
        val att = _ownerFileAdAttachment.value
        _ownerFileAdAttachment.value = null
        if (att != null) {
            _uiEvents.value = UiEvent.ShowToast("Downloaded file: ${att.name}")
        }
    }

    fun setLanguage(langCode: String) {
        prefs.setLanguage(langCode)
        _currentLanguage.value = langCode
    }

    fun setThemeMode(mode: String) {
        prefs.setThemeMode(mode)
        _themeMode.value = mode
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> String.format(java.util.Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
        }
    }
}

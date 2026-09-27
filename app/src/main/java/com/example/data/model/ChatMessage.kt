package com.example.data.model

import android.graphics.Bitmap
import java.util.UUID

enum class MessageType {
    TEXT,
    FILE,
    VOICE,
    IMAGE
}

enum class AttachmentType {
    DOCUMENT,
    PDF,
    SPREADSHEET,
    AUDIO,
    IMAGE,
    ARCHIVE,
    VIDEO,
    OTHER
}

enum class UserRole {
    PUBLIC,
    EMPLOYEE,
    HOD // Head of Department
}

data class FileAttachment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: AttachmentType,
    val sizeBytes: Long,
    val sizeFormatted: String,
    val localUri: String? = null,
    val isFromOwner: Boolean = false,
    val downloadUrl: String? = null
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sessionId: String = "default_session",
    val senderId: String,
    val senderName: String,
    val isFromOwner: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: MessageType = MessageType.TEXT,
    val attachment: FileAttachment? = null,
    val isEncrypted: Boolean = true,
    val encryptionKeyId: String = "TYPNEX-AES256-E2EE",
    val translatedText: String? = null,
    val voiceDurationSeconds: Int = 0,
    val isDeletedForEveryone: Boolean = false,
    val deletedForUserIds: List<String> = emptyList()
)

data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val userId: String,
    val userName: String,
    val assignedEmployeeId: String? = null,
    val assignedEmployeeName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastMessageTime: Long = System.currentTimeMillis(),
    val lastPreview: String = "",
    val messageCount: Int = 0
)

data class EmployeeAccount(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val password: String,
    val name: String,
    val isActive: Boolean = true,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val assignedSessionIds: List<String> = emptyList()
) {
    val isTimedOut: Boolean
        get() = (System.currentTimeMillis() - lastActiveTimestamp) > 3600_000L // 1 hour inactivity
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole = UserRole.PUBLIC,
    val employeeUsername: String? = null,
    val isVerifiedVip: Boolean = false,
    val selectedLanguage: String = "hi",
    val joinedAt: Long = System.currentTimeMillis(),
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

data class DailyQuota(
    val dateKey: String, // e.g. 2026-09-26
    val filesSent: Int = 0,
    val chatsSent: Int = 0,
    val maxFiles: Int = 10,
    val maxChats: Int = 100
) {
    val isFilesLocked: Boolean get() = filesSent >= maxFiles
    val isChatsLocked: Boolean get() = chatsSent >= maxChats
    val remainingFiles: Int get() = (maxFiles - filesSent).coerceAtLeast(0)
    val remainingChats: Int get() = (maxChats - chatsSent).coerceAtLeast(0)
}

data class LanguageItem(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String
)

data class FilePreviewPayload(
    val fileName: String,
    val mimeType: String?,
    val sizeBytes: Long,
    val uri: String?,
    val type: AttachmentType,
    val previewBitmap: Bitmap? = null,
    val voiceDurationSeconds: Int = 0,
    val isVideoRejection: Boolean = false
)

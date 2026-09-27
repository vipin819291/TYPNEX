package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AttachmentType
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.FileAttachment
import com.example.data.model.MessageType

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String = "default_session",
    val senderId: String,
    val senderName: String,
    val isFromOwner: Boolean,
    val text: String,
    val timestamp: Long,
    val messageType: String,
    val attachmentName: String? = null,
    val attachmentType: String? = null,
    val attachmentSizeBytes: Long? = null,
    val attachmentSizeFormatted: String? = null,
    val attachmentUri: String? = null,
    val isEncrypted: Boolean = true,
    val encryptionKeyId: String = "TYPNEX-AES256-E2EE",
    val translatedText: String? = null,
    val voiceDurationSeconds: Int = 0,
    val isDeletedForEveryone: Boolean = false,
    val deletedForUsersCsv: String = ""
) {
    fun toChatMessage(): ChatMessage {
        val attachment = if (attachmentName != null && attachmentType != null) {
            FileAttachment(
                name = attachmentName,
                type = try { AttachmentType.valueOf(attachmentType) } catch (e: Exception) { AttachmentType.OTHER },
                sizeBytes = attachmentSizeBytes ?: 0L,
                sizeFormatted = attachmentSizeFormatted ?: "0 KB",
                localUri = attachmentUri,
                isFromOwner = isFromOwner
            )
        } else null

        val deletedIds = if (deletedForUsersCsv.isBlank()) emptyList() else deletedForUsersCsv.split(",")

        return ChatMessage(
            id = id,
            sessionId = sessionId,
            senderId = senderId,
            senderName = senderName,
            isFromOwner = isFromOwner,
            text = text,
            timestamp = timestamp,
            messageType = try { MessageType.valueOf(messageType) } catch (e: Exception) { MessageType.TEXT },
            attachment = attachment,
            isEncrypted = isEncrypted,
            encryptionKeyId = encryptionKeyId,
            translatedText = translatedText,
            voiceDurationSeconds = voiceDurationSeconds,
            isDeletedForEveryone = isDeletedForEveryone,
            deletedForUserIds = deletedIds
        )
    }

    companion object {
        fun fromChatMessage(message: ChatMessage): ChatMessageEntity {
            return ChatMessageEntity(
                id = message.id,
                sessionId = message.sessionId,
                senderId = message.senderId,
                senderName = message.senderName,
                isFromOwner = message.isFromOwner,
                text = message.text,
                timestamp = message.timestamp,
                messageType = message.messageType.name,
                attachmentName = message.attachment?.name,
                attachmentType = message.attachment?.type?.name,
                attachmentSizeBytes = message.attachment?.sizeBytes,
                attachmentSizeFormatted = message.attachment?.sizeFormatted,
                attachmentUri = message.attachment?.localUri,
                isEncrypted = message.isEncrypted,
                encryptionKeyId = message.encryptionKeyId,
                translatedText = message.translatedText,
                voiceDurationSeconds = message.voiceDurationSeconds,
                isDeletedForEveryone = message.isDeletedForEveryone,
                deletedForUsersCsv = message.deletedForUserIds.joinToString(",")
            )
        }
    }
}

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val userId: String,
    val userName: String,
    val assignedEmployeeId: String? = null,
    val assignedEmployeeName: String? = null,
    val createdAt: Long,
    val lastMessageTime: Long,
    val lastPreview: String = "",
    val messageCount: Int = 0
) {
    fun toChatSession(): ChatSession {
        return ChatSession(
            id = id,
            title = title,
            userId = userId,
            userName = userName,
            assignedEmployeeId = assignedEmployeeId,
            assignedEmployeeName = assignedEmployeeName,
            createdAt = createdAt,
            lastMessageTime = lastMessageTime,
            lastPreview = lastPreview,
            messageCount = messageCount
        )
    }

    companion object {
        fun fromChatSession(session: ChatSession): ChatSessionEntity {
            return ChatSessionEntity(
                id = session.id,
                title = session.title,
                userId = session.userId,
                userName = session.userName,
                assignedEmployeeId = session.assignedEmployeeId,
                assignedEmployeeName = session.assignedEmployeeName,
                createdAt = session.createdAt,
                lastMessageTime = session.lastMessageTime,
                lastPreview = session.lastPreview,
                messageCount = session.messageCount
            )
        }
    }
}

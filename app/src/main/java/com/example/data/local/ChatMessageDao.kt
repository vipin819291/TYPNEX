package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Update
    suspend fun updateMessage(message: ChatMessageEntity)

    @Query("UPDATE chat_messages SET isDeletedForEveryone = 1, text = :notice WHERE id = :messageId")
    suspend fun markDeletedForEveryone(messageId: String, notice: String = "This message was deleted by sender")

    @Query("UPDATE chat_messages SET deletedForUsersCsv = :deletedCsv WHERE id = :messageId")
    suspend fun updateDeletedForMe(messageId: String, deletedCsv: String)

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessagePermanently(messageId: String)

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: String)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    @Query("SELECT COUNT(*) FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun getMessageCount(sessionId: String): Int

    @Query("SELECT COUNT(*) FROM chat_messages")
    suspend fun getTotalMessageCount(): Int

    // SESSIONS
    @Query("SELECT * FROM chat_sessions ORDER BY lastMessageTime DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE userId = :userId ORDER BY lastMessageTime DESC")
    fun getSessionsForUser(userId: String): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE assignedEmployeeId = :employeeId ORDER BY lastMessageTime DESC")
    fun getSessionsForEmployee(employeeId: String): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): ChatSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity)

    @Query("UPDATE chat_sessions SET lastMessageTime = :time, lastPreview = :preview, messageCount = messageCount + 1 WHERE id = :sessionId")
    suspend fun updateSessionActivity(sessionId: String, time: Long, preview: String)

    @Query("UPDATE chat_sessions SET assignedEmployeeId = :employeeId, assignedEmployeeName = :employeeName WHERE id = :sessionId")
    suspend fun updateSessionAssignment(sessionId: String, employeeId: String?, employeeName: String?)

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)
}

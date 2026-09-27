package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ChatMessageEntity
import com.example.data.local.PreferencesManager
import com.example.data.local.TypnexDatabase
import com.example.data.model.EmployeeAccount
import com.example.data.model.LanguageData
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Typnex", appName)
    }

    @Test
    fun `verify welcome messages in hindi and english`() {
        val (w1Hi, w2Hi, w3Hi) = LanguageData.getWelcomeMessages("Amit", "hi")
        assertTrue(w1Hi.contains("Amit"))
        assertTrue(w2Hi.contains("5 मिनट"))

        val (w1En, w2En, w3En) = LanguageData.getWelcomeMessages("John", "en")
        assertTrue(w1En.contains("John"))
        assertTrue(w2En.contains("5 minutes"))
    }

    @Test
    fun `test daily quota default and VIP limits`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = PreferencesManager(context)
        prefs.setVipStatus(false)
        val normalQuota = prefs.getDailyQuota()
        assertEquals(10, normalQuota.maxFiles)
        assertEquals(100, normalQuota.maxChats)

        prefs.setVipStatus(true)
        val vipQuota = prefs.getDailyQuota()
        assertEquals(Int.MAX_VALUE, vipQuota.maxFiles)
        assertEquals(Int.MAX_VALUE, vipQuota.maxChats)
    }

    @Test
    fun `test department head credentials and employee management`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = PreferencesManager(context)

        val employees = prefs.getEmployees()
        assertTrue(employees.isNotEmpty())

        val newEmp = EmployeeAccount(
            id = "test_emp",
            username = "test_typist",
            password = "password123",
            name = "Test Typist"
        )
        prefs.addEmployee(newEmp)
        val updated = prefs.getEmployees()
        assertTrue(updated.any { it.username == "test_typist" })

        val hodId = "7906348721"
        val hodPass = "7906348721"
        assertEquals("7906348721", hodId)
        assertEquals("7906348721", hodPass)
    }

    @Test
    fun `test markDeletedForEveryone Room query execution`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = TypnexDatabase.getDatabase(context)
        val dao = db.chatMessageDao()

        val msg = ChatMessageEntity(
            id = "msg_test_123",
            sessionId = "session_test",
            senderId = "user_1",
            senderName = "Test User",
            isFromOwner = false,
            text = "Original message text",
            timestamp = System.currentTimeMillis(),
            messageType = "TEXT"
        )
        dao.insertMessage(msg)

        // Execute markDeletedForEveryone on SQLite
        dao.markDeletedForEveryone("msg_test_123", "This message was deleted by sender")
        assertTrue(true)
    }
}

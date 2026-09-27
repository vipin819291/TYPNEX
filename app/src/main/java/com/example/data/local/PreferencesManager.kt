package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.DailyQuota
import com.example.data.model.EmployeeAccount
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("typnex_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_EMPLOYEE_USERNAME = "employee_username"
        private const val KEY_IS_VIP = "is_vip"
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_QUOTA_DATE = "quota_date"
        private const val KEY_FILES_SENT = "files_sent"
        private const val KEY_CHATS_SENT = "chats_sent"
        private const val KEY_LAST_LONG_AD_TIME = "last_long_ad_time"
        private const val KEY_HAS_SEEN_WELCOME = "has_seen_welcome"
        private const val KEY_LIVE_TRANSLATION = "live_translation"
        private const val KEY_CURRENT_SESSION_ID = "current_session_id"
        private const val KEY_EMPLOYEES_JSON = "employees_json"
        private const val KEY_LAST_ACTIVITY_TIME = "last_activity_time"
    }

    init {
        // Initialize default employees if not present
        if (!prefs.contains(KEY_EMPLOYEES_JSON)) {
            val defaultEmployees = listOf(
                EmployeeAccount(
                    id = "emp_1",
                    username = "rahul_typist",
                    password = "emp123",
                    name = "Rahul Sharma",
                    isActive = true,
                    lastActiveTimestamp = System.currentTimeMillis()
                ),
                EmployeeAccount(
                    id = "emp_2",
                    username = "priya_typist",
                    password = "emp123",
                    name = "Priya Patel",
                    isActive = true,
                    lastActiveTimestamp = System.currentTimeMillis()
                )
            )
            saveEmployees(defaultEmployees)
        }
    }

    private fun getCurrentDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserProfile(): UserProfile? {
        if (!isLoggedIn()) return null
        val roleStr = prefs.getString(KEY_USER_ROLE, UserRole.PUBLIC.name) ?: UserRole.PUBLIC.name
        val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.PUBLIC }

        return UserProfile(
            id = prefs.getString(KEY_USER_ID, "user_1") ?: "user_1",
            name = prefs.getString(KEY_USER_NAME, "User") ?: "User",
            email = prefs.getString(KEY_USER_EMAIL, "user@example.com") ?: "user@example.com",
            role = role,
            employeeUsername = prefs.getString(KEY_EMPLOYEE_USERNAME, null),
            isVerifiedVip = prefs.getBoolean(KEY_IS_VIP, false),
            selectedLanguage = getLanguage(),
            lastActiveTimestamp = prefs.getLong(KEY_LAST_ACTIVITY_TIME, System.currentTimeMillis())
        )
    }

    fun saveUserProfile(profile: UserProfile) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_ID, profile.id)
            .putString(KEY_USER_NAME, profile.name)
            .putString(KEY_USER_EMAIL, profile.email)
            .putString(KEY_USER_ROLE, profile.role.name)
            .putString(KEY_EMPLOYEE_USERNAME, profile.employeeUsername)
            .putBoolean(KEY_IS_VIP, profile.isVerifiedVip)
            .putString(KEY_LANGUAGE, profile.selectedLanguage)
            .putLong(KEY_LAST_ACTIVITY_TIME, System.currentTimeMillis())
            .apply()
    }

    fun updateLastActivity() {
        prefs.edit().putLong(KEY_LAST_ACTIVITY_TIME, System.currentTimeMillis()).apply()
        // If logged in as employee, also update their employee account timestamp
        val user = getUserProfile()
        if (user?.role == UserRole.EMPLOYEE && user.employeeUsername != null) {
            updateEmployeeActivity(user.employeeUsername)
        }
    }

    fun getCurrentSessionId(): String {
        return prefs.getString(KEY_CURRENT_SESSION_ID, "session_main") ?: "session_main"
    }

    fun setCurrentSessionId(sessionId: String) {
        prefs.edit().putString(KEY_CURRENT_SESSION_ID, sessionId).apply()
    }

    fun setVipStatus(isVip: Boolean) {
        prefs.edit().putBoolean(KEY_IS_VIP, isVip).apply()
    }

    fun isVip(): Boolean = prefs.getBoolean(KEY_IS_VIP, false)

    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "hi") ?: "hi"

    fun setLanguage(langCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, langCode).apply()
    }

    fun getThemeMode(): String = prefs.getString(KEY_THEME_MODE, "LIGHT") ?: "LIGHT"

    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    }

    fun isLiveTranslationEnabled(): Boolean = prefs.getBoolean(KEY_LIVE_TRANSLATION, true)

    fun setLiveTranslation(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LIVE_TRANSLATION, enabled).apply()
    }

    fun hasSeenWelcome(): Boolean = prefs.getBoolean(KEY_HAS_SEEN_WELCOME, false)

    fun setSeenWelcome(seen: Boolean) {
        prefs.edit().putBoolean(KEY_HAS_SEEN_WELCOME, seen).apply()
    }

    // DAILY QUOTA
    fun getDailyQuota(): DailyQuota {
        val today = getCurrentDateKey()
        val storedDate = prefs.getString(KEY_QUOTA_DATE, "")

        if (storedDate != today) {
            prefs.edit()
                .putString(KEY_QUOTA_DATE, today)
                .putInt(KEY_FILES_SENT, 0)
                .putInt(KEY_CHATS_SENT, 0)
                .apply()
            return DailyQuota(dateKey = today, filesSent = 0, chatsSent = 0)
        }

        val files = prefs.getInt(KEY_FILES_SENT, 0)
        val chats = prefs.getInt(KEY_CHATS_SENT, 0)
        return DailyQuota(
            dateKey = today,
            filesSent = files,
            chatsSent = chats,
            maxFiles = if (isVip()) Int.MAX_VALUE else 10,
            maxChats = if (isVip()) Int.MAX_VALUE else 100
        )
    }

    fun incrementFilesSent(): DailyQuota {
        val quota = getDailyQuota()
        val newFiles = quota.filesSent + 1
        prefs.edit().putInt(KEY_FILES_SENT, newFiles).apply()
        return quota.copy(filesSent = newFiles)
    }

    fun incrementChatsSent(): DailyQuota {
        val quota = getDailyQuota()
        val newChats = quota.chatsSent + 1
        prefs.edit().putInt(KEY_CHATS_SENT, newChats).apply()
        return quota.copy(chatsSent = newChats)
    }

    fun getMillisUntilMidnight(): Long {
        val now = Calendar.getInstance()
        val midnight = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return (midnight.timeInMillis - now.timeInMillis).coerceAtLeast(0L)
    }

    fun getLastLongAdTimestamp(): Long = prefs.getLong(KEY_LAST_LONG_AD_TIME, 0L)

    fun recordLongAdShown() {
        prefs.edit().putLong(KEY_LAST_LONG_AD_TIME, System.currentTimeMillis()).apply()
    }

    fun isLongAdDue(): Boolean {
        val last = getLastLongAdTimestamp()
        val twelveHours = 12 * 60 * 60 * 1000L
        return (System.currentTimeMillis() - last) >= twelveHours
    }

    // EMPLOYEE MANAGEMENT (HOD CONTROL)
    fun getEmployees(): List<EmployeeAccount> {
        val jsonStr = prefs.getString(KEY_EMPLOYEES_JSON, "[]") ?: "[]"
        val list = mutableListOf<EmployeeAccount>()
        try {
            val array = JSONArray(jsonStr)
            val now = System.currentTimeMillis()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val lastActive = obj.optLong("lastActiveTimestamp", now)
                val isTimedOut = (now - lastActive) > 3600_000L // 1 hour inactivity
                val rawActive = obj.optBoolean("isActive", true)
                // If inactive for > 1 hour, auto logout / mark inactive
                val effectiveActive = if (isTimedOut) false else rawActive

                list.add(
                    EmployeeAccount(
                        id = obj.getString("id"),
                        username = obj.getString("username"),
                        password = obj.getString("password"),
                        name = obj.getString("name"),
                        isActive = effectiveActive,
                        lastActiveTimestamp = lastActive
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveEmployees(employees: List<EmployeeAccount>) {
        val array = JSONArray()
        employees.forEach { emp ->
            val obj = JSONObject()
            obj.put("id", emp.id)
            obj.put("username", emp.username)
            obj.put("password", emp.password)
            obj.put("name", emp.name)
            obj.put("isActive", emp.isActive)
            obj.put("lastActiveTimestamp", emp.lastActiveTimestamp)
            array.put(obj)
        }
        prefs.edit().putString(KEY_EMPLOYEES_JSON, array.toString()).apply()
    }

    fun addEmployee(emp: EmployeeAccount) {
        val list = getEmployees().toMutableList()
        list.removeAll { it.username.equals(emp.username, ignoreCase = true) }
        list.add(emp)
        saveEmployees(list)
    }

    fun removeEmployee(id: String) {
        val list = getEmployees().toMutableList()
        list.removeAll { it.id == id }
        saveEmployees(list)
    }

    fun toggleEmployeeActive(id: String, active: Boolean) {
        val list = getEmployees().map {
            if (it.id == id) it.copy(isActive = active, lastActiveTimestamp = System.currentTimeMillis()) else it
        }
        saveEmployees(list)
    }

    fun updateEmployeeActivity(username: String) {
        val list = getEmployees().map {
            if (it.username.equals(username, ignoreCase = true)) {
                it.copy(lastActiveTimestamp = System.currentTimeMillis(), isActive = true)
            } else it
        }
        saveEmployees(list)
    }

    fun logout() {
        val user = getUserProfile()
        if (user?.role == UserRole.EMPLOYEE && user.employeeUsername != null) {
            // Mark employee offline on logout
            val list = getEmployees().map {
                if (it.username.equals(user.employeeUsername, ignoreCase = true)) {
                    it.copy(isActive = false)
                } else it
            }
            saveEmployees(list)
        }

        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .putBoolean(KEY_HAS_SEEN_WELCOME, false)
            .apply()
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.model.UserRole
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HodDashboardScreen
import com.example.ui.theme.TypnexTheme
import com.example.viewmodel.TypnexViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TypnexViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val systemDark = isSystemInDarkTheme()

            // In light theme: normal UI with liquid glass
            // In dark theme: UI turns black while maintaining liquid glass effect
            val isDark = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> systemDark
            }

            TypnexTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (isDark) Color.Black else Color(0xFFF8FAFC)
                ) {
                    TypnexApp(
                        viewModel = viewModel,
                        isDark = isDark,
                        onToggleTheme = {
                            val nextMode = if (isDark) "LIGHT" else "DARK"
                            viewModel.setThemeMode(nextMode)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TypnexApp(
    viewModel: TypnexViewModel,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    var isShowingHodDashboard by remember { mutableStateOf(true) }

    if (currentUser == null) {
        AuthScreen(
            currentLanguageCode = currentLanguage,
            isDark = isDark,
            onSelectLanguage = { code -> viewModel.setLanguage(code) },
            onEmailAuth = { name, email, isNewAccount ->
                viewModel.loginWithEmail(name, email, isNewAccount)
            },
            onGoogleSignIn = {
                viewModel.loginWithGoogle()
            },
            onHodLogin = { loginId, pass ->
                val ok = viewModel.loginAsHod(loginId, pass)
                if (ok) isShowingHodDashboard = true
            },
            onEmployeeLogin = { username, pass ->
                viewModel.loginAsEmployee(username, pass)
            }
        )
    } else {
        if (currentUser?.role == UserRole.HOD && isShowingHodDashboard) {
            HodDashboardScreen(
                viewModel = viewModel,
                isDark = isDark,
                onOpenSessionChat = { session ->
                    viewModel.switchSession(session)
                    isShowingHodDashboard = false
                },
                onLogout = { viewModel.logout() }
            )
        } else {
            ChatScreen(
                viewModel = viewModel,
                isDark = isDark,
                onToggleTheme = onToggleTheme,
                onOpenHodPortal = if (currentUser?.role == UserRole.HOD) {
                    { isShowingHodDashboard = true }
                } else null
            )
        }
    }
}

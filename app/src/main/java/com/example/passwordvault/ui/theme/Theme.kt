package com.example.passwordvault.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 暗黑科技风配色方案
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00D4FF),      // 科技蓝
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0066FF),
    onPrimaryContainer = Color.White,
    
    secondary = Color(0xFF9D4EDD),    // 科技紫
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF7B2CBF),
    onSecondaryContainer = Color.White,
    
    tertiary = Color(0xFF00FF88),     // 科技绿
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF00CC6A),
    onTertiaryContainer = Color.Black,
    
    background = Color(0xFF0A0A0F),   // 深空黑背景
    onBackground = Color(0xFFFFFFFF), // 白色文字
    
    surface = Color(0xFF121218),      // 表面深灰
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1A1A24), // 表面变体
    onSurfaceVariant = Color(0xFFB0B0C0),
    
    outline = Color(0xFF303040),      // 边框
    outlineVariant = Color(0xFF404050),
    
    error = Color(0xFFFF3D71),        // 错误红色
    onError = Color.White,
    errorContainer = Color(0xFFCC2F5A),
    onErrorContainer = Color.White,
    
    inversePrimary = Color(0xFF00D4FF),
    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = Color(0xFF1A1A24),
    
    scrim = Color(0xCC000000),        // 遮罩
    surfaceTint = Color(0xFF00D4FF),
    
    // 小米13动态颜色支持
    primaryFixed = Color(0xFF00D4FF),
    onPrimaryFixed = Color.White,
    primaryFixedDim = Color(0xFF00AACC),
    onPrimaryFixedVariant = Color.White,
    
    secondaryFixed = Color(0xFF9D4EDD),
    onSecondaryFixed = Color.White,
    secondaryFixedDim = Color(0xFF7B2CBF),
    onSecondaryFixedVariant = Color.White
)

// 亮色主题（备用，但主要使用暗黑主题）
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0066CC),
    secondary = Color(0xFF6A1B9A),
    tertiary = Color(0xFF00A859),
    background = Color(0xFFF5F5F7),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A)
)

@Composable
fun PasswordVaultTheme(
    darkTheme: Boolean = true, // 强制使用暗黑主题
    content: @Composable () -> Unit
) {
    // 总是使用暗黑主题，符合科技风设计
    val colorScheme = DarkColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
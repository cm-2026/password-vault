package com.example.passwordvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.passwordvault.ui.theme.PasswordVaultTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 针对小米13优化：请求高刷新率
        window.attributes = window.attributes.apply {
            // 尝试请求高刷新率模式
            try {
                val displayModeField = android.view.WindowManager.LayoutParams::class.java
                    .getDeclaredField("preferredDisplayModeId")
                displayModeField.isAccessible = true
                // 120Hz显示模式ID（可能因设备而异）
                displayModeField.set(this, 2) // 2通常对应120Hz
            } catch (e: Exception) {
                // 忽略错误，使用默认刷新率
            }
        }
        
        setContent {
            PasswordVaultTheme {
                // 暗黑科技风背景
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 显示预览界面（开发阶段）
                    PasswordVaultPreview()
                    
                    // 正式版本使用：PasswordVaultApp()
                }
            }
        }
    }
}
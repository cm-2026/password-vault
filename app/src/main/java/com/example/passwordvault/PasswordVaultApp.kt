package com.example.passwordvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.passwordvault.ui.theme.PasswordVaultTheme

@Composable
fun PasswordVaultApp() {
    PasswordVaultTheme {
        // 应用主界面
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // 这里将显示登录界面或主界面
            // 暂时显示一个占位界面
            TechBackground {
                // 应用内容
            }
        }
    }
}

@Composable
fun TechBackground(content: @Composable () -> Unit) {
    // 科技风背景实现
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordVaultAppPreview() {
    PasswordVaultTheme {
        PasswordVaultApp()
    }
}
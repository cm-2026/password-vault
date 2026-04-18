package com.example.passwordvault.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.passwordvault.ui.components.SettingsCard
import com.example.passwordvault.ui.components.TechBackground
import com.example.passwordvault.ui.theme.TechDarkTheme
import kotlinx.coroutines.launch

/**
 * 设置界面
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    
    TechBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 安全设置
            SecuritySettingsSection(
                autoLockTimeout = uiState.autoLockTimeout,
                onAutoLockChange = { timeout ->
                    scope.launch {
                        viewModel.updateAutoLockTimeout(timeout)
                    }
                },
                isFingerprintEnabled = uiState.isFingerprintEnabled,
                onFingerprintToggle = { enabled ->
                    scope.launch {
                        viewModel.toggleFingerprint(enabled)
                    }
                },
                hasPin = uiState.hasPin,
                onPinChange = {
                    // 打开PIN码设置界面
                }
            )
            
            // 备份与恢复
            BackupRestoreSection(
                onBackup = {
                    scope.launch {
                        viewModel.createBackup()
                    }
                },
                onRestore = {
                    // 打开文件选择器
                },
                lastBackupTime = uiState.lastBackupTime,
                backupCount = uiState.backupCount
            )
            
            // 外观设置
            AppearanceSection(
                themeMode = uiState.themeMode,
                onThemeChange = { theme ->
                    scope.launch {
                        viewModel.updateTheme(theme)
                    }
                }
            )
            
            // 数据管理
            DataManagementSection(
                passwordCount = uiState.passwordCount,
                categoryCount = uiState.categoryCount,
                onClearData = {
                    scope.launch {
                        viewModel.clearAllData()
                    }
                },
                onExportData = {
                    scope.launch {
                        viewModel.exportData()
                    }
                }
            )
            
            // 关于
            AboutSection(
                appVersion = uiState.appVersion,
                onPrivacyPolicy = {
                    // 打开隐私政策
                },
                onTermsOfService = {
                    // 打开服务条款
                },
                onRateApp = {
                    // 打开应用商店
                },
                onFeedback = {
                    // 打开反馈界面
                }
            )
        }
    }
}

/**
 * 安全设置部分
 */
@Composable
private fun SecuritySettingsSection(
    autoLockTimeout: Int,
    onAutoLockChange: (Int) -> Unit,
    isFingerprintEnabled: Boolean,
    onFingerprintToggle: (Boolean) -> Unit,
    hasPin: Boolean,
    onPinChange: () -> Unit
) {
    Column {
        Text(
            text = "安全设置",
            style = MaterialTheme.typography.titleLarge,
            color = TechDarkTheme.TextPrimary,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
        )
        
        // 自动锁定
        SettingsCard(
            title = "自动锁定",
            description = "应用无操作后自动锁定的时间",
            icon = "⏰",
            onClick = { /* 打开时间选择器 */ }
        ) {
            Text(
                text = when (autoLockTimeout) {
                    0 -> "立即"
                    1 -> "1分钟"
                    5 -> "5分钟"
                    10 -> "10分钟"
                    30 -> "30分钟"
                    else -> "从不"
                },
                color = TechDarkTheme.TextSecondary
            )
        }
        
        // 指纹认证
        SettingsCard(
            title = "指纹认证",
            description = "使用指纹快速登录",
            icon = "🖐️",
            onClick = { onFingerprintToggle(!isFingerprintEnabled) }
        ) {
            Switch(
                checked = isFingerprintEnabled,
                onCheckedChange = onFingerprintToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TechDarkTheme.TechBlue,
                    checkedTrackColor = TechDarkTheme.TechBlue.copy(alpha = 0.5f)
                )
            )
        }
        
        // PIN码管理
        SettingsCard(
            title = "PIN码管理",
            description = if (hasPin) "更改或禁用PIN码" else "设置PIN码",
            icon = "🔢",
            onClick = onPinChange
        )
    }
}

/**
 * 备份与恢复部分
 */
@Composable
private fun BackupRestoreSection(
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    lastBackupTime: Long?,
    backupCount: Int
) {
    Column {
        Text(
            text = "备份与恢复",
            style = MaterialTheme.typography.titleLarge,
            color = TechDarkTheme.TextPrimary,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
        )
        
        // 创建备份
        SettingsCard(
            title = "创建备份",
            description = "加密备份所有密码数据",
            icon = "💾",
            onClick = onBackup
        )
        
        // 恢复备份
        SettingsCard(
            title = "恢复备份",
            description = "从备份文件恢复数据",
            icon = "🔄",
            onClick = onRestore
        )
        
        // 备份信息
        SettingsCard(
            title = "备份信息",
            description = "查看和管理备份文件",
            icon = "📊",
            onClick = { /* 打开备份管理界面 */ }
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$backupCount 个备份",
                    color = TechDarkTheme.TextSecondary
                )
                lastBackupTime?.let {
                    Text(
                        text = formatTimeAgo(it),
                        color = TechDarkTheme.TextDisabled,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/**
 * 外观部分
 */
@Composable
private fun AppearanceSection(
    themeMode: String,
    onThemeChange: (String) -> Unit
) {
    Column {
        Text(
            text = "外观",
            style = MaterialTheme.typography.titleLarge,
            color = TechDarkTheme.TextPrimary,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
        )
        
        // 主题模式
        SettingsCard(
            title = "主题模式",
            description = "选择应用主题",
            icon = "🎨",
            onClick = { /* 打开主题选择器 */ }
        ) {
            Text(
                text = when (themeMode) {
                    "dark" -> "暗黑"
                    "light" -> "明亮"
                    else -> "跟随系统"
                },
                color = TechDarkTheme.TextSecondary
            )
        }
        
        // 科技风效果
        SettingsCard(
            title = "科技风效果",
            description = "启用/禁用科技风动画",
            icon = "✨",
            onClick = { /* 切换效果 */ }
        ) {
            Switch(
                checked = true,
                onCheckedChange = { /* 切换效果 */ },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TechDarkTheme.TechBlue,
                    checkedTrackColor = TechDarkTheme.TechBlue.copy(alpha = 0.5f)
                )
            )
        }
    }
}

/**
 * 数据管理部分
 */
@Composable
private fun DataManagementSection(
    passwordCount: Int,
    categoryCount: Int,
    onClearData: () -> Unit,
    onExportData: () -> Unit
) {
    Column {
        Text(
            text = "数据管理",
            style = MaterialTheme.typography.titleLarge,
            color = TechDarkTheme.TextPrimary,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
        )
        
        // 数据统计
        SettingsCard(
            title = "数据统计",
            description = "查看存储的数据量",
            icon = "📈",
            onClick = { /* 打开统计界面 */ }
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$passwordCount 个密码",
                    color = TechDarkTheme.TextSecondary
                )
                Text(
                    text = "$categoryCount 个分类",
                    color = TechDarkTheme.TextDisabled,
                    fontSize = 12.sp
                )
            }
        }
        
        // 导出数据
        SettingsCard(
            title = "导出数据",
            description = "导出为通用格式",
            icon = "📤",
            onClick = onExportData
        )
        
        // 清除数据
        SettingsCard(
            title = "清除所有数据",
            description = "危险操作，无法恢复",
            icon = "🗑️",
            onClick = onClearData
        )
    }
}

/**
 * 关于部分
 */
@Composable
private fun AboutSection(
    appVersion: String,
    onPrivacyPolicy: () -> Unit,
    onTermsOfService: () -> Unit,
    onRateApp: () -> Unit,
    onFeedback: () -> Unit
) {
    Column {
        Text(
            text = "关于",
            style = MaterialTheme.typography.titleLarge,
            color = TechDarkTheme.TextPrimary,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
        )
        
        // 应用信息
        SettingsCard(
            title = "应用信息",
            description = "版本和更新",
            icon = "ℹ️",
            onClick = { /* 打开应用信息 */ }
        ) {
            Text(
                text = appVersion,
                color = TechDarkTheme.TextSecondary
            )
        }
        
        // 隐私政策
        SettingsCard(
            title = "隐私政策",
            description = "了解我们如何保护您的数据",
            icon = "🔒",
            onClick = onPrivacyPolicy
        )
        
        // 服务条款
        SettingsCard(
            title = "服务条款",
            description = "使用条款和条件",
            icon = "📄",
            onClick = onTermsOfService
        )
        
        // 评价应用
        SettingsCard(
            title = "评价应用",
            description = "在应用商店给我们评分",
            icon = "⭐",
            onClick = onRateApp
        )
        
        // 反馈
        SettingsCard(
            title = "反馈",
            description = "报告问题或提出建议",
            icon = "💬",
            onClick = onFeedback
        )
        
        // 开发者信息
        SettingsCard(
            title = "开发者信息",
            description = "关于开发团队",
            icon = "👨‍💻",
            onClick = { /* 打开开发者信息 */ }
        )
    }
}

/**
 * 格式化时间显示
 */
private fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "刚刚" // 1分钟内
        diff < 3600000 -> "${diff / 60000}分钟前" // 1小时内
        diff < 86400000 -> "${diff / 3600000}小时前" // 1天内
        diff < 604800000 -> "${diff / 86400000}天前" // 1周内
        else -> "${diff / 604800000}周前"
    }
}

// 导入必要的Compose组件
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
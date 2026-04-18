package com.example.passwordvault

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passwordvault.ui.components.*
import com.example.passwordvault.ui.theme.PasswordVaultTheme
import com.example.passwordvault.ui.theme.TechDarkTheme

/**
 * 应用预览界面
 * 展示密码保险箱应用的主要UI组件
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PasswordVaultPreview() {
    PasswordVaultTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = TechDarkTheme.BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 应用标题
                Text(
                    text = "🔐 密码保险箱预览",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechDarkTheme.TechBlue,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                )
                
                Text(
                    text = "暗黑科技风密码管理应用",
                    fontSize = 14.sp,
                    color = TechDarkTheme.TextSecondary,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                // 登录界面预览
                LoginPreviewSection()
                
                // 密码卡片预览
                PasswordCardPreviewSection()
                
                // 组件库预览
                ComponentsPreviewSection()
                
                // 设置界面预览
                SettingsPreviewSection()
                
                // 构建信息
                BuildInfoSection()
            }
        }
    }
}

/**
 * 登录界面预览
 */
@Composable
private fun LoginPreviewSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "登录界面",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TechDarkTheme.TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        TechCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TechDarkTheme.SurfaceMedium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 指纹图标
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = TechDarkTheme.TechBlue.copy(alpha = 0.1f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🖐️",
                        fontSize = 36.sp
                    )
                }
                
                Text(
                    text = "指纹登录",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TechDarkTheme.TextPrimary
                )
                
                Text(
                    text = "请验证指纹以继续",
                    fontSize = 14.sp,
                    color = TechDarkTheme.TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                TechButton(
                    text = "指纹认证",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
                
                TextButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "使用PIN码登录",
                        color = TechDarkTheme.TechCyan
                    )
                }
            }
        }
    }
}

/**
 * 密码卡片预览
 */
@Composable
private fun PasswordCardPreviewSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "密码卡片",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TechDarkTheme.TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        PasswordCard(
            title = "Google账户",
            username = "user@gmail.com",
            categoryName = "社交",
            categoryColor = TechDarkTheme.CategorySocial,
            lastUpdated = "今天 14:30",
            isFavorite = true,
            securityLevel = "🔒",
            onClick = {},
            onFavoriteClick = {}
        )
        
        PasswordCard(
            title = "支付宝",
            username = "138****8888",
            categoryName = "金融",
            categoryColor = TechDarkTheme.CategoryFinance,
            lastUpdated = "昨天 09:15",
            isFavorite = false,
            securityLevel = "🛡️",
            onClick = {},
            onFavoriteClick = {}
        )
        
        PasswordCard(
            title = "公司邮箱",
            username = "zhangsan@company.com",
            categoryName = "工作",
            categoryColor = TechDarkTheme.CategoryWork,
            lastUpdated = "2026-04-17",
            isFavorite = true,
            securityLevel = "🚨",
            onClick = {},
            onFavoriteClick = {}
        )
    }
}

/**
 * 组件库预览
 */
@Composable
private fun ComponentsPreviewSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "UI组件库",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TechDarkTheme.TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 按钮预览
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TechButton(
                text = "主要按钮",
                onClick = {},
                modifier = Modifier.weight(1f)
            )
            
            TechButton(
                text = "次要按钮",
                onClick = {},
                modifier = Modifier.weight(1f),
                isPrimary = false
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 输入框预览
        TechTextField(
            value = "示例文本",
            onValueChange = {},
            label = "应用名称",
            placeholder = "输入应用名称"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        PasswordTextField(
            value = "password123",
            onValueChange = {},
            label = "密码",
            placeholder = "输入密码"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 分类标签预览
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryTag(name = "社交", color = TechDarkTheme.CategorySocial)
            CategoryTag(name = "金融", color = TechDarkTheme.CategoryFinance)
            CategoryTag(name = "工作", color = TechDarkTheme.CategoryWork)
            CategoryTag(name = "娱乐", color = TechDarkTheme.CategoryEntertainment)
        }
    }
}

/**
 * 设置界面预览
 */
@Composable
private fun SettingsPreviewSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "设置选项",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TechDarkTheme.TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        SettingsCard(
            title = "指纹认证",
            description = "使用指纹快速登录",
            icon = "🖐️",
            onClick = {}
        ) {
            Switch(
                checked = true,
                onCheckedChange = {},
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TechDarkTheme.TechBlue,
                    checkedTrackColor = TechDarkTheme.TechBlue.copy(alpha = 0.5f)
                )
            )
        }
        
        SettingsCard(
            title = "自动备份",
            description = "每周自动备份数据",
            icon = "💾",
            onClick = {}
        ) {
            Text(
                text = "已启用",
                color = TechDarkTheme.TechGreen
            )
        }
        
        SettingsCard(
            title = "暗黑模式",
            description = "启用暗黑科技主题",
            icon = "🌙",
            onClick = {}
        ) {
            Text(
                text = "已启用",
                color = TechDarkTheme.TechBlue
            )
        }
    }
}

/**
 * 构建信息
 */
@Composable
private fun BuildInfoSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(
            color = TechDarkTheme.BorderMedium,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        )
        
        Text(
            text = "📱 应用信息",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TechDarkTheme.TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            InfoRow(label = "应用名称", value = "密码保险箱")
            InfoRow(label = "版本", value = "1.0.0 (1)")
            InfoRow(label = "目标设备", value = "Android 13+ (小米13优化)")
            InfoRow(label = "数据存储", value = "本地加密存储")
            InfoRow(label = "认证方式", value = "指纹 + PIN码")
            InfoRow(label = "UI风格", value = "暗黑科技风")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "✅ 预览构建完成",
            fontSize = 14.sp,
            color = TechDarkTheme.TechGreen,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "可以在Android Studio中查看实时预览",
            fontSize = 12.sp,
            color = TechDarkTheme.TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/**
 * 信息行组件
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TechDarkTheme.TextSecondary
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            color = TechDarkTheme.TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 单个组件预览
 */
@Preview(showBackground = true)
@Composable
fun LoginButtonPreview() {
    PasswordVaultTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TechButton(
                text = "指纹登录",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PasswordCard(
                title = "预览卡片",
                username = "preview@example.com",
                categoryName = "预览",
                categoryColor = TechDarkTheme.TechBlue,
                lastUpdated = "刚刚",
                isFavorite = true,
                securityLevel = "🔒",
                onClick = {},
                onFavoriteClick = {}
            )
        }
    }
}

// 导入必要的Compose组件
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
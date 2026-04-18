package com.example.passwordvault.ui.password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.passwordvault.data.model.PasswordEntry
import com.example.passwordvault.ui.components.*
import com.example.passwordvault.ui.theme.TechDarkTheme

/**
 * 添加/编辑密码界面
 */
@Composable
fun AddEditPasswordScreen(
    passwordId: Long? = null,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AddEditPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(passwordId) {
        if (passwordId != null) {
            viewModel.loadPassword(passwordId)
        }
    }
    
    TechBackground {
        Scaffold(
            topBar = {
                AddEditPasswordTopBar(
                    isEditing = passwordId != null,
                    onSave = { viewModel.savePassword() },
                    onCancel = onCancel,
                    isSaving = uiState.isSaving
                )
            }
        ) { paddingValues ->
            AddEditPasswordContent(
                uiState = uiState,
                onTitleChange = { viewModel.updateTitle(it) },
                onUsernameChange = { viewModel.updateUsername(it) },
                onPasswordChange = { viewModel.updatePassword(it) },
                onCategoryChange = { viewModel.updateCategory(it) },
                onUrlChange = { viewModel.updateUrl(it) },
                onNotesChange = { viewModel.updateNotes(it) },
                onSecurityLevelChange = { viewModel.updateSecurityLevel(it) },
                onGeneratePassword = { viewModel.generatePassword() },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
    
    // 监听保存完成
    LaunchedEffect(uiState.isSaveComplete) {
        if (uiState.isSaveComplete) {
            onSave()
        }
    }
}

/**
 * 添加/编辑密码顶部栏
 */
@Composable
private fun AddEditPasswordTopBar(
    isEditing: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isSaving: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = if (isEditing) "编辑密码" else "添加密码",
                color = TechDarkTheme.TextPrimary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = TechDarkTheme.SurfaceDark
        ),
        navigationIcon = {
            IconButton(onClick = onCancel) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = TechDarkTheme.TextPrimary
                )
            }
        },
        actions = {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = TechDarkTheme.TechBlue
                )
            } else {
                TextButton(onClick = onSave) {
                    Text(
                        text = "保存",
                        color = TechDarkTheme.TechBlue
                    )
                }
            }
        }
    )
}

/**
 * 添加/编辑密码内容
 */
@Composable
private fun AddEditPasswordContent(
    uiState: AddEditPasswordUiState,
    onTitleChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onCategoryChange: (Long) -> Unit,
    onUrlChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSecurityLevelChange: (PasswordEntry.SecurityLevel) -> Unit,
    onGeneratePassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        TechTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = "应用/网站名称",
            placeholder = "例如：Google、微信",
            isError = uiState.titleError != null,
            errorMessage = uiState.titleError
        )
        
        // 账号
        TechTextField(
            value = uiState.username,
            onValueChange = onUsernameChange,
            label = "账号",
            placeholder = "邮箱/手机号/用户名",
            isError = uiState.usernameError != null,
            errorMessage = uiState.usernameError
        )
        
        // 密码
        PasswordSection(
            password = uiState.password,
            onPasswordChange = onPasswordChange,
            onGeneratePassword = onGeneratePassword,
            passwordError = uiState.passwordError
        )
        
        // 分类
        CategorySection(
            selectedCategoryId = uiState.categoryId,
            onCategoryChange = onCategoryChange,
            categories = uiState.categories
        )
        
        // 网址
        TechTextField(
            value = uiState.url,
            onValueChange = onUrlChange,
            label = "网址（可选）",
            placeholder = "https://example.com"
        )
        
        // 备注
        TechTextField(
            value = uiState.notes,
            onValueChange = onNotesChange,
            label = "备注（可选）",
            placeholder = "额外的说明信息",
            singleLine = false,
            maxLines = 4
        )
        
        // 安全级别
        SecurityLevelSection(
            selectedLevel = uiState.securityLevel,
            onLevelChange = onSecurityLevelChange
        )
        
        // 错误消息
        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = TechDarkTheme.Error,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * 密码部分
 */
@Composable
private fun PasswordSection(
    password: String,
    onPasswordChange: (String) -> Unit,
    onGeneratePassword: () -> Unit,
    passwordError: String?
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "密码",
                fontSize = 14.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                color = if (passwordError != null) TechDarkTheme.Error else TechDarkTheme.TextSecondary
            )
            
            TextButton(onClick = onGeneratePassword) {
                Text(
                    text = "生成密码",
                    color = TechDarkTheme.TechBlue
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        PasswordTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "输入密码",
            isError = passwordError != null,
            errorMessage = passwordError
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 密码强度指示器
        PasswordStrengthIndicator(password = password)
    }
}

/**
 * 分类部分
 */
@Composable
private fun CategorySection(
    selectedCategoryId: Long?,
    onCategoryChange: (Long) -> Unit,
    categories: List<com.example.passwordvault.data.model.Category>
) {
    if (categories.isNotEmpty()) {
        CategorySelector(
            selectedCategoryId = selectedCategoryId,
            categories = categories,
            onCategorySelected = onCategoryChange,
            label = "分类"
        )
    }
}

/**
 * 安全级别部分
 */
@Composable
private fun SecurityLevelSection(
    selectedLevel: PasswordEntry.SecurityLevel,
    onLevelChange: (PasswordEntry.SecurityLevel) -> Unit
) {
    Column {
        Text(
            text = "安全级别",
            fontSize = 14.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            color = TechDarkTheme.TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PasswordEntry.SecurityLevel.values().forEach { level ->
                SecurityLevelChip(
                    level = level,
                    isSelected = level == selectedLevel,
                    onClick = { onLevelChange(level) }
                )
            }
        }
    }
}

/**
 * 安全级别芯片
 */
@Composable
private fun SecurityLevelChip(
    level: PasswordEntry.SecurityLevel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (text, color) = when (level) {
        PasswordEntry.SecurityLevel.LOW -> Pair("低", TechDarkTheme.TextSecondary)
        PasswordEntry.SecurityLevel.NORMAL -> Pair("正常", TechDarkTheme.TechBlue)
        PasswordEntry.SecurityLevel.HIGH -> Pair("高", TechDarkTheme.Warning)
        PasswordEntry.SecurityLevel.CRITICAL -> Pair("关键", TechDarkTheme.Error)
    }
    
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(text = text)
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.2f),
            selectedLabelColor = color,
            containerColor = TechDarkTheme.SurfaceMedium,
            labelColor = TechDarkTheme.TextSecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = if (isSelected) color else TechDarkTheme.BorderMedium,
            borderWidth = 1.dp
        )
    )
}

// 导入必要的Compose组件
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
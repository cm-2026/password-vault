package com.example.passwordvault.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passwordvault.ui.theme.TechDarkTheme

/**
 * 科技风输入框组件
 */
@Composable
fun TechTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var isFocused by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        // 标签
        label?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isError) TechDarkTheme.Error else TechDarkTheme.TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // 输入框容器
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = if (enabled) TechDarkTheme.SurfaceMedium else TechDarkTheme.SurfaceDark,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = when {
                        isError -> TechDarkTheme.Error
                        isFocused -> TechDarkTheme.TechBlue
                        else -> TechDarkTheme.BorderMedium
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 前导图标
                leadingIcon?.invoke()
                
                // 文本输入
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    enabled = enabled,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = if (enabled) TechDarkTheme.TextPrimary else TechDarkTheme.TextDisabled
                    ),
                    cursorBrush = SolidColor(TechDarkTheme.TechBlue),
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    decorationBox = { innerTextField ->
                        // 占位符
                        if (value.isEmpty() && placeholder != null) {
                            Text(
                                text = placeholder,
                                fontSize = 16.sp,
                                color = TechDarkTheme.TextHint,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerTextField()
                    }
                )
                
                // 尾部图标
                trailingIcon?.invoke()
            }
        }
        
        // 错误消息
        errorMessage?.let {
            Text(
                text = it,
                fontSize = 12.sp,
                color = TechDarkTheme.Error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

/**
 * 密码输入框（带显示/隐藏切换）
 */
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    TechTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        isError = isError,
        errorMessage = errorMessage,
        enabled = enabled,
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            TechIconButton(
                onClick = { isPasswordVisible = !isPasswordVisible },
                icon = {
                    Text(
                        text = if (isPasswordVisible) "👁️" else "👁️‍🗨️",
                        fontSize = 20.sp
                    )
                },
                tint = TechDarkTheme.TextSecondary
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        )
    )
}

/**
 * 搜索输入框
 */
@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索",
    onSearch: (() -> Unit)? = null
) {
    TechTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = {
            Text(
                text = "🔍",
                fontSize = 20.sp,
                color = TechDarkTheme.TextSecondary
            )
        },
        trailingIcon = if (value.isNotEmpty()) {
            {
                TechIconButton(
                    onClick = { onValueChange("") },
                    icon = {
                        Text(
                            text = "✕",
                            fontSize = 18.sp,
                            color = TechDarkTheme.TextSecondary
                        )
                    }
                )
            }
        } else null,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch?.invoke() }
        )
    )
}

/**
 * 分类选择器
 */
@Composable
fun CategorySelector(
    selectedCategoryId: Long?,
    categories: List<com.example.passwordvault.data.model.Category>,
    onCategorySelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = "分类"
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        // 标签
        label?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TechDarkTheme.TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // 选择器按钮
        TechCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { expanded = true },
            backgroundColor = TechDarkTheme.SurfaceMedium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 选中的分类
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    selectedCategoryId?.let { categoryId ->
                        val category = categories.find { it.id == categoryId }
                        category?.let {
                            // 分类颜色点
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(it.color))
                            )
                            
                            // 分类名称
                            Text(
                                text = it.name,
                                fontSize = 16.sp,
                                color = TechDarkTheme.TextPrimary
                            )
                        }
                    } ?: run {
                        Text(
                            text = "选择分类",
                            fontSize = 16.sp,
                            color = TechDarkTheme.TextHint
                        )
                    }
                }
                
                // 下拉箭头
                Text(
                    text = "⌄",
                    fontSize = 20.sp,
                    color = TechDarkTheme.TextSecondary
                )
            }
        }
        
        // 下拉菜单
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(TechDarkTheme.SurfaceDark)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 分类图标
                            Text(
                                text = category.icon,
                                fontSize = 18.sp
                            )
                            
                            // 分类名称
                            Text(
                                text = category.name,
                                fontSize = 16.sp,
                                color = TechDarkTheme.TextPrimary
                            )
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // 分类颜色点
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color(category.color))
                            )
                        }
                    },
                    onClick = {
                        onCategorySelected(category.id)
                        expanded = false
                    },
                    modifier = Modifier.background(TechDarkTheme.SurfaceMedium)
                )
            }
        }
    }
}

/**
 * 密码强度指示器
 */
@Composable
fun PasswordStrengthIndicator(
    password: String,
    modifier: Modifier = Modifier
) {
    val strength = calculatePasswordStrength(password)
    
    Column(modifier = modifier) {
        // 强度标签
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "密码强度",
                fontSize = 14.sp,
                color = TechDarkTheme.TextSecondary
            )
            
            Text(
                text = strength.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = strength.color
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 强度条
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(TechDarkTheme.SurfaceMedium)
        ) {
            // 强度填充
            Box(
                modifier = Modifier
                    .fillMaxWidth(strength.percentage)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                strength.color,
                                strength.color.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
        }
    }
}

/**
 * 密码强度计算结果
 */
private data class PasswordStrength(
    val level: Int, // 0-4
    val label: String,
    val color: Color,
    val percentage: Float
)

/**
 * 计算密码强度
 */
private fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) {
        return PasswordStrength(0, "未输入", TechDarkTheme.TextDisabled, 0f)
    }
    
    var score = 0
    
    // 长度检查
    if (password.length >= 8) score++
    if (password.length >= 12) score++
    
    // 字符类型检查
    val hasLowercase = password.any { it.isLowerCase() }
    val hasUppercase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }
    
    if (hasLowercase) score++
    if (hasUppercase) score++
    if (hasDigit) score++
    if (hasSpecial) score++
    
    // 限制最大分数
    score = score.coerceAtMost(5)
    
    return when (score) {
        0, 1 -> PasswordStrength(1, "非常弱", TechDarkTheme.Error, 0.2f)
        2 -> PasswordStrength(2, "弱", Color(0xFFFF6B00), 0.4f)
        3 -> PasswordStrength(3, "一般", Color(0xFFFFAA00), 0.6f)
        4 -> PasswordStrength(4, "良好", TechDarkTheme.TechGreen, 0.8f)
        else -> PasswordStrength(5, "非常强", TechDarkTheme.TechBlue, 1f)
    }
}

// 导入必要的Compose组件
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.text.input.VisualTransformation
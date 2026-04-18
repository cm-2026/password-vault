package com.example.passwordvault.ui.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.passwordvault.ui.components.TechButton
import com.example.passwordvault.ui.components.TechCard
import com.example.passwordvault.ui.theme.TechDarkTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 登录界面
 * 支持指纹和PIN码两种认证方式
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSetupPin: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    
    // 科技风背景
    TechBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 应用Logo和标题
            LoginHeader()
            
            Spacer(modifier = Modifier.height(48.dp))
            
            when (uiState) {
                is LoginUiState.Initial -> {
                    // 初始状态，显示指纹认证选项
                    FingerprintAuthSection(
                        onFingerprintClick = {
                            scope.launch {
                                viewModel.authenticateWithFingerprint()
                            }
                        },
                        onUsePinClick = {
                            viewModel.switchToPinAuth()
                        },
                        isFingerprintAvailable = viewModel.isFingerprintAvailable()
                    )
                }
                
                is LoginUiState.PinAuth -> {
                    // PIN码认证
                    PinAuthSection(
                        onPinEntered = { pin ->
                            scope.launch {
                                viewModel.authenticateWithPin(pin)
                            }
                        },
                        onBackToFingerprint = {
                            viewModel.switchToFingerprintAuth()
                        },
                        errorMessage = (uiState as? LoginUiState.PinAuth)?.errorMessage,
                        isLocked = (uiState as? LoginUiState.PinAuth)?.isLocked == true,
                        lockedSeconds = (uiState as? LoginUiState.PinAuth)?.lockedSeconds ?: 0
                    )
                }
                
                is LoginUiState.SetupPin -> {
                    // 设置PIN码
                    SetupPinSection(
                        onPinSetup = { pin ->
                            scope.launch {
                                viewModel.setupPin(pin)
                            }
                        },
                        onCancel = {
                            viewModel.cancelPinSetup()
                        }
                    )
                }
                
                is LoginUiState.Authenticating -> {
                    // 认证中
                    AuthenticatingSection()
                }
                
                is LoginUiState.Success -> {
                    // 认证成功
                    SuccessSection {
                        onLoginSuccess()
                    }
                }
                
                is LoginUiState.Error -> {
                    // 认证错误
                    ErrorSection(
                        errorMessage = (uiState as LoginUiState.Error).message,
                        onRetry = {
                            viewModel.retry()
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 底部信息
            Text(
                text = "所有数据本地加密存储\n确保您的隐私安全",
                fontSize = 12.sp,
                color = TechDarkTheme.TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
    
    // 监听认证成功
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            delay(1000) // 显示成功动画1秒
            onLoginSuccess()
        }
    }
}

/**
 * 登录头部（Logo和标题）
 */
@Composable
private fun LoginHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 科技感Logo
        TechLogo()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 应用标题
        Text(
            text = "密码保险箱",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TechDarkTheme.TechBlue,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 副标题
        Text(
            text = "安全存储您的所有密码",
            fontSize = 14.sp,
            color = TechDarkTheme.TextSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * 科技感Logo动画
 */
@Composable
private fun TechLogo() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        TechDarkTheme.TechBlue.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // 外圈
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(TechDarkTheme.SurfaceDark)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            TechDarkTheme.TechBlue,
                            TechDarkTheme.TechCyan,
                            TechDarkTheme.TechBlue
                        )
                    ),
                    shape = CircleShape
                )
                .rotate(rotation)
        )
        
        // 内圈
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(TechDarkTheme.SurfaceMedium)
                .scale(scale)
                .border(
                    width = 1.dp,
                    color = TechDarkTheme.TechCyan.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔒",
                fontSize = 32.sp
            )
        }
    }
}

/**
 * 指纹认证部分
 */
@Composable
private fun FingerprintAuthSection(
    onFingerprintClick: () -> Unit,
    onUsePinClick: () -> Unit,
    isFingerprintAvailable: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 指纹图标
        FingerprintIcon()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 提示文字
        Text(
            text = if (isFingerprintAvailable) {
                "请验证指纹以继续"
            } else {
                "指纹识别不可用"
            },
            fontSize = 18.sp,
            color = TechDarkTheme.TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 指纹认证按钮
        TechButton(
            text = "指纹认证",
            onClick = onFingerprintClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = isFingerprintAvailable,
            isLoading = false
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 使用PIN码选项
        TextButton(
            onClick = onUsePinClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "使用PIN码登录",
                color = TechDarkTheme.TechCyan
            )
        }
    }
}

/**
 * 指纹图标动画
 */
@Composable
private fun FingerprintIcon() {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(TechDarkTheme.TechBlue.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🖐️",
            fontSize = 48.sp,
            modifier = Modifier.scale(pulse)
        )
    }
}

/**
 * PIN码认证部分
 */
@Composable
private fun PinAuthSection(
    onPinEntered: (String) -> Unit,
    onBackToFingerprint: () -> Unit,
    errorMessage: String?,
    isLocked: Boolean,
    lockedSeconds: Int
) {
    var pin by remember { mutableStateOf("") }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题
        Text(
            text = "PIN码认证",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = TechDarkTheme.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "请输入6位数字PIN码",
            fontSize = 14.sp,
            color = TechDarkTheme.TextSecondary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // PIN码输入显示
        PinDisplay(pin = pin)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 错误消息
        errorMessage?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                color = TechDarkTheme.Error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 锁定消息
        if (isLocked) {
            Text(
                text = "已锁定，请等待${lockedSeconds}秒",
                fontSize = 14.sp,
                color = TechDarkTheme.Warning,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 数字键盘
        PinKeypad(
            onDigitClick = { digit ->
                if (pin.length < 6) {
                    pin += digit
                    if (pin.length == 6) {
                        onPinEntered(pin)
                    }
                }
            },
            onBackspaceClick = {
                if (pin.isNotEmpty()) {
                    pin = pin.dropLast(1)
                }
            },
            enabled = !isLocked
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 返回指纹认证
        TextButton(
            onClick = onBackToFingerprint,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "返回指纹认证",
                color = TechDarkTheme.TechCyan
            )
        }
    }
}

/**
 * PIN码显示
 */
@Composable
private fun PinDisplay(pin: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (index < pin.length) {
                            TechDarkTheme.TechBlue
                        } else {
                            TechDarkTheme.SurfaceMedium
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = TechDarkTheme.TechBlue.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * 数字键盘
 */
@Composable
private fun PinKeypad(
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    enabled: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 第一行：1 2 3
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            listOf("1", "2", "3").forEach { digit ->
                PinKey(
                    digit = digit,
                    onClick = { onDigitClick(digit) },
                    enabled = enabled
                )
            }
        }
        
        // 第二行：4 5 6
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            listOf("4", "5", "6").forEach { digit ->
                PinKey(
                    digit = digit,
                    onClick = { onDigitClick(digit) },
                    enabled = enabled
                )
            }
        }
        
        // 第三行：7 8 9
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            listOf("7", "8", "9").forEach { digit ->
                PinKey(
                    digit = digit,
                    onClick = { onDigitClick(digit) },
                    enabled = enabled
                )
            }
        }
        
        // 第四行：0 和 退格
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PinKey(
                digit = "0",
                onClick = { onDigitClick("0") },
                enabled = enabled
            )
            
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackspaceClick,
                    enabled = enabled
                ) {
                    Text(
                        text = "⌫",
                        fontSize = 24.sp,
                        color = if (enabled) TechDarkTheme.TextPrimary else TechDarkTheme.TextDisabled
                    )
                }
            }
        }
    }
}

/**
 * 数字键
 */
@Composable
private fun PinKey(
    digit: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(
                if (enabled) TechDarkTheme.SurfaceMedium else TechDarkTheme.SurfaceDark
            )
            .clickable(enabled = enabled) { onClick() }
            .border(
                width = 1.dp,
                color = TechDarkTheme.TechBlue.copy(alpha = if (enabled) 0.3f else 0.1f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = if (enabled) TechDarkTheme.TextPrimary else TechDarkTheme.TextDisabled
        )
    }
}

/**
 * 设置PIN码部分
 */
@Composable
private fun SetupPinSection(
    onPinSetup: (String) -> Unit,
    onCancel: () -> Unit
) {
    // 实现类似PIN码认证的界面，但用于设置新PIN码
    // 为了简洁，这里省略详细实现
    Column {
        Text(
            text = "设置PIN码",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = TechDarkTheme.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        TechButton(
            text = "设置PIN码",
            onClick = { onPinSetup("123456") }, // 示例
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TechButton(
            text = "取消",
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            isPrimary = false
        )
    }
}

/**
 * 认证中部分
 */
@Composable
private fun AuthenticatingSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 加载动画
        CircularProgressIndicator(
            color = TechDarkTheme.TechBlue,
            strokeWidth = 3.dp,
            modifier = Modifier.size(60.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "认证中...",
            fontSize = 18.sp,
            color = TechDarkTheme.TextPrimary
        )
    }
}

/**
 * 成功部分
 */
@Composable
private fun SuccessSection(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 成功图标
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(TechDarkTheme.Success.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                fontSize = 40.sp,
                color = TechDarkTheme.Success
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "认证成功",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = TechDarkTheme.Success
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "正在进入应用...",
            fontSize = 14.sp,
            color = TechDarkTheme.TextSecondary
        )
    }
}

/**
 * 错误部分
 */
@Composable
private fun ErrorSection(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 错误图标
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(TechDarkTheme.Error.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✗",
                fontSize = 40.sp,
                color = TechDarkTheme.Error
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "认证失败",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = TechDarkTheme.Error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = errorMessage,
            fontSize = 14.sp,
            color = TechDarkTheme.TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TechButton(
            text = "重试",
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 科技风背景
 */
@Composable
private fun TechBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TechDarkTheme.BackgroundDark)
    ) {
        // 网格背景
        // 这里可以添加科技感的网格或粒子效果
        content()
    }
}

// 导入必要的Compose组件
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
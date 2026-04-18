package com.example.passwordvault.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passwordvault.ui.theme.TechDarkTheme

/**
 * 科技风按钮组件
 */
@Composable
fun TechButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    isLoading: Boolean = false,
    gradientColors: List<Color> = if (isPrimary) listOf(
        TechDarkTheme.TechBlue,
        TechDarkTheme.TechCyan
    ) else listOf(
        TechDarkTheme.SurfaceMedium,
        TechDarkTheme.SurfaceLight
    )
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        border = if (enabled) BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    TechDarkTheme.TechCyan.copy(alpha = 0.8f),
                    TechDarkTheme.TechBlue.copy(alpha = 0.8f)
                )
            )
        ) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = if (isPrimary) Color.White else TechDarkTheme.TextPrimary,
            disabledContainerColor = TechDarkTheme.SurfaceDark,
            disabledContentColor = TechDarkTheme.TextDisabled
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // 渐变背景
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (enabled) gradientColors else listOf(
                            TechDarkTheme.SurfaceDark,
                            TechDarkTheme.SurfaceMedium
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
        )
        
        if (isLoading) {
            // 加载动画
            TechLoadingIndicator(size = 20.dp)
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
        }
    }
}

/**
 * 圆形科技按钮（用于FAB）
 */
@Composable
fun TechFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(56.dp)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TechDarkTheme.TechBlue,
            contentColor = Color.White,
            disabledContainerColor = TechDarkTheme.SurfaceDark
        ),
        border = BorderStroke(
            width = 1.dp,
            color = TechDarkTheme.TechCyan.copy(alpha = 0.5f)
        ),
        enabled = enabled
    ) {
        icon()
    }
}

/**
 * 图标按钮
 */
@Composable
fun TechIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    enabled: Boolean = true,
    tint: Color = TechDarkTheme.TechBlue
) {
    androidx.compose.material3.IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        androidx.compose.material3.Icon(
            painter = androidx.compose.ui.res.painterResource(id = androidx.compose.ui.res.painterResource(id = 0)),
            contentDescription = null,
            tint = if (enabled) tint else TechDarkTheme.TextDisabled
        )
    }
}

/**
 * 文本按钮
 */
@Composable
fun TechTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = TechDarkTheme.TechBlue
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Text(
            text = text,
            color = if (enabled) color else TechDarkTheme.TextDisabled,
            fontSize = 14.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
        )
    }
}

/**
 * 加载指示器
 */
@Composable
fun TechLoadingIndicator(
    size: androidx.compose.ui.unit.Dp = 24.dp,
    color: Color = TechDarkTheme.TechBlue
) {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = Modifier.size(size),
        color = color,
        strokeWidth = 2.dp
    )
}

// 导入必要的Compose组件
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
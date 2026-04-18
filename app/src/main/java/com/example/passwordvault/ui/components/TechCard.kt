package com.example.passwordvault.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passwordvault.ui.theme.TechDarkTheme

/**
 * 科技风卡片组件
 */
@Composable
fun TechCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: androidx.compose.ui.unit.Dp = 4.dp,
    borderColor: Color = TechDarkTheme.TechBlue.copy(alpha = 0.3f),
    backgroundColor: Color = TechDarkTheme.SurfaceDark,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(16.dp),
                spotColor = TechDarkTheme.TechBlue.copy(alpha = 0.2f)
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = TechDarkTheme.TextPrimary
        ),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

/**
 * 密码条目卡片
 */
@Composable
fun PasswordCard(
    title: String,
    username: String,
    categoryName: String,
    categoryColor: Color,
    lastUpdated: String,
    isFavorite: Boolean = false,
    securityLevel: String = "🔒",
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    TechCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick,
        borderColor = categoryColor.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 安全级别图标
                    Text(
                        text = securityLevel,
                        fontSize = 18.sp
                    )
                    
                    // 标题
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TechDarkTheme.TextPrimary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                
                // 收藏按钮
                TechIconButton(
                    onClick = onFavoriteClick,
                    icon = {
                        val icon = if (isFavorite) "❤️" else "🤍"
                        Text(
                            text = icon,
                            fontSize = 20.sp
                        )
                    },
                    tint = if (isFavorite) Color(0xFFFF3D71) else TechDarkTheme.TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 账号信息
            Text(
                text = username,
                fontSize = 14.sp,
                color = TechDarkTheme.TextSecondary,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 底部信息行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 分类标签
                CategoryTag(
                    name = categoryName,
                    color = categoryColor
                )
                
                // 更新时间
                Text(
                    text = lastUpdated,
                    fontSize = 12.sp,
                    color = TechDarkTheme.TextDisabled
                )
            }
        }
    }
}

/**
 * 分类标签组件
 */
@Composable
fun CategoryTag(
    name: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

/**
 * 设置项卡片
 */
@Composable
fun SettingsCard(
    title: String,
    description: String? = null,
    icon: String? = null,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null
) {
    TechCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        onClick = onClick,
        backgroundColor = TechDarkTheme.SurfaceMedium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 图标
                icon?.let {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TechDarkTheme.TechBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it,
                            fontSize = 20.sp
                        )
                    }
                }
                
                // 文本内容
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TechDarkTheme.TextPrimary
                    )
                    
                    description?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = TechDarkTheme.TextSecondary
                        )
                    }
                }
            }
            
            // 尾部内容
            trailing?.invoke() ?: Text(
                text = "›",
                fontSize = 20.sp,
                color = TechDarkTheme.TextSecondary
            )
        }
    }
}

/**
 * 空状态卡片
 */
@Composable
fun EmptyStateCard(
    title: String,
    description: String,
    icon: String = "🔒",
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    TechCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        backgroundColor = TechDarkTheme.SurfaceMedium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 图标
            Text(
                text = icon,
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 标题
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TechDarkTheme.TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 描述
            Text(
                text = description,
                fontSize = 14.sp,
                color = TechDarkTheme.TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // 操作按钮
            actionText?.let {
                onActionClick?.let { onClick ->
                    TechButton(
                        text = actionText,
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// 导入必要的Compose组件
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextOverflow
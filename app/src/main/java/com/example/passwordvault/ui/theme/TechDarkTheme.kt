package com.example.passwordvault.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 暗黑科技风主题颜色常量
 */
object TechDarkTheme {
    // 基础色
    val BackgroundDark = Color(0xFF0A0A0F)     // 深空黑
    val SurfaceDark = Color(0xFF121218)        // 表面深灰
    val SurfaceMedium = Color(0xFF1A1A24)      // 中等表面
    val SurfaceLight = Color(0xFF242430)       // 浅色表面
    
    // 科技色
    val TechBlue = Color(0xFF00D4FF)           // 科技蓝（主色）
    val TechCyan = Color(0xFF00FFE0)           // 青蓝
    val TechPurple = Color(0xFF9D4EDD)         // 科技紫
    val TechGreen = Color(0xFF00FF88)          // 科技绿
    val TechPink = Color(0xFFFF2D95)           // 科技粉
    
    // 功能色
    val Success = Color(0xFF00FF88)            // 成功（科技绿）
    val Warning = Color(0xFFFFAA00)            // 警告
    val Error = Color(0xFFFF3D71)              // 错误
    val Info = Color(0xFF00D4FF)               // 信息（科技蓝）
    
    // 文字色
    val TextPrimary = Color(0xFFFFFFFF)        // 主要文字
    val TextSecondary = Color(0xFFB0B0C0)      // 次要文字
    val TextDisabled = Color(0xFF606070)       // 禁用文字
    val TextHint = Color(0xFF808090)           // 提示文字
    
    // 边框和分隔线
    val BorderLight = Color(0xFF303040)        // 浅色边框
    val BorderMedium = Color(0xFF404050)       // 中等边框
    val BorderDark = Color(0xFF505060)         // 深色边框
    val Divider = Color(0xFF303040)            // 分隔线
    
    // 阴影和覆盖
    val ShadowDark = Color(0x80000000)         // 深色阴影
    val OverlayDark = Color(0xCC000000)        // 深色覆盖
    val OverlayLight = Color(0x33FFFFFF)       // 浅色覆盖
    
    // 分类颜色
    val CategorySocial = Color(0xFF00D4FF)     // 社交 - 科技蓝
    val CategoryFinance = Color(0xFF00FF88)    // 金融 - 科技绿
    val CategoryWork = Color(0xFF9D4EDD)       // 工作 - 科技紫
    val CategoryEntertainment = Color(0xFFFF2D95) // 娱乐 - 科技粉
    val CategoryShopping = Color(0xFFFFAA00)   // 购物 - 警告色
    val CategoryEducation = Color(0xFF00FFE0)  // 教育 - 青蓝
    val CategoryOther = Color(0xFFB0B0C0)      // 其他 - 次要文字色
    
    // 渐变
    val GradientBlue = listOf(Color(0xFF0066FF), Color(0xFF00D4FF))
    val GradientPurple = listOf(Color(0xFF9D4EDD), Color(0xFFFF2D95))
    val GradientGreen = listOf(Color(0xFF00FF88), Color(0xFF00FFE0))
    
    /**
     * 根据分类ID获取颜色
     */
    fun getCategoryColor(categoryId: Long): Color {
        return when (categoryId % 7) {
            0L -> CategorySocial
            1L -> CategoryFinance
            2L -> CategoryWork
            3L -> CategoryEntertainment
            4L -> CategoryShopping
            5L -> CategoryEducation
            else -> CategoryOther
        }
    }
    
    /**
     * 获取所有分类颜色列表
     */
    fun getCategoryColors(): List<Color> {
        return listOf(
            CategorySocial,
            CategoryFinance,
            CategoryWork,
            CategoryEntertainment,
            CategoryShopping,
            CategoryEducation,
            CategoryOther
        )
    }
    
    /**
     * 获取密码强度颜色
     */
    fun getStrengthColor(level: Int): Color {
        return when (level) {
            0 -> Error
            1 -> Color(0xFFFF6B00)
            2 -> Warning
            3 -> TechGreen
            4 -> TechBlue
            else -> TechPurple
        }
    }
}
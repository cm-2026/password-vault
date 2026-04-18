package com.example.passwordvault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * 分类实体
 * 用于对密码条目进行分类管理
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 唯一标识符
    val uuid: String = UUID.randomUUID().toString(),
    
    // 分类信息
    val name: String,           // 分类名称
    val color: Int,             // 分类颜色
    val icon: String,          // 分类图标
    
    // 排序和显示
    val position: Int = 0,      // 显示位置
    val isDefault: Boolean = false, // 是否为默认分类
    val isHidden: Boolean = false,  // 是否隐藏
    
    // 统计信息
    val itemCount: Int = 0,     // 包含的条目数量
    
    // 时间戳
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * 获取默认分类列表
         */
        fun getDefaultCategories(): List<Category> {
            return listOf(
                Category(
                    id = 1,
                    name = "社交",
                    color = 0xFF00D4FF.toInt(),  // 科技蓝
                    icon = "👥",
                    isDefault = true,
                    position = 0
                ),
                Category(
                    id = 2,
                    name = "金融",
                    color = 0xFF00FF88.toInt(),  // 科技绿
                    icon = "💰",
                    isDefault = true,
                    position = 1
                ),
                Category(
                    id = 3,
                    name = "工作",
                    color = 0xFF9D4EDD.toInt(),  // 科技紫
                    icon = "💼",
                    isDefault = true,
                    position = 2
                ),
                Category(
                    id = 4,
                    name = "娱乐",
                    color = 0xFFFF2D95.toInt(),  // 科技粉
                    icon = "🎮",
                    isDefault = true,
                    position = 3
                ),
                Category(
                    id = 5,
                    name = "购物",
                    color = 0xFFFFAA00.toInt(),  // 警告色
                    icon = "🛒",
                    isDefault = true,
                    position = 4
                ),
                Category(
                    id = 6,
                    name = "教育",
                    color = 0xFF00FFE0.toInt(),  // 青蓝
                    icon = "📚",
                    isDefault = true,
                    position = 5
                ),
                Category(
                    id = 7,
                    name = "其他",
                    color = 0xFFB0B0C0.toInt(),  // 灰色
                    icon = "📁",
                    isDefault = true,
                    position = 6
                )
            )
        }
        
        /**
         * 根据名称获取默认分类
         */
        fun getDefaultCategoryByName(name: String): Category? {
            return getDefaultCategories().find { it.name == name }
        }
        
        /**
         * 获取分类颜色列表
         */
        fun getCategoryColors(): List<Int> {
            return listOf(
                0xFF00D4FF.toInt(),  // 科技蓝
                0xFF00FF88.toInt(),  // 科技绿
                0xFF9D4EDD.toInt(),  // 科技紫
                0xFFFF2D95.toInt(),  // 科技粉
                0xFFFFAA00.toInt(),  // 橙色
                0xFF00FFE0.toInt(),  // 青蓝
                0xFFFF6B00.toInt(),  // 深橙
                0xFF4CAF50.toInt(),  // 绿色
                0xFF2196F3.toInt(),  // 蓝色
                0xFF9C27B0.toInt()   // 紫色
            )
        }
        
        /**
         * 获取分类图标列表
         */
        fun getCategoryIcons(): List<String> {
            return listOf(
                "👥", "💰", "💼", "🎮", "🛒", "📚", "📁",
                "🏠", "✈️", "🍔", "☕", "🎵", "🎬", "🏥",
                "🚗", "📱", "💻", "🔑", "🛡️", "🌟", "❤️"
            )
        }
    }
    
    /**
     * 复制并更新修改时间
     */
    fun copyWithUpdate(
        name: String = this.name,
        color: Int = this.color,
        icon: String = this.icon,
        position: Int = this.position,
        isHidden: Boolean = this.isHidden
    ): Category {
        return this.copy(
            name = name,
            color = color,
            icon = icon,
            position = position,
            isHidden = isHidden,
            updatedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * 更新条目数量
     */
    fun updateItemCount(count: Int): Category {
        return this.copy(itemCount = count)
    }
    
    /**
     * 获取颜色对应的Compose Color
     */
    fun getComposeColor(): androidx.compose.ui.graphics.Color {
        return androidx.compose.ui.graphics.Color(color)
    }
}
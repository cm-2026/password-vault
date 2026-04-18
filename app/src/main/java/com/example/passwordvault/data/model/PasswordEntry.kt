package com.example.passwordvault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/**
 * 密码条目实体
 * 表示一个存储的账号密码记录
 */
@Entity(tableName = "password_entries")
data class PasswordEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 唯一标识符，用于备份恢复
    val uuid: String = UUID.randomUUID().toString(),
    
    // 基本信息
    val title: String,          // 应用/网站名称
    val username: String,       // 账号/邮箱/手机号
    val password: String,       // 加密后的密码
    val categoryId: Long,       // 分类ID
    
    // 额外信息
    val url: String? = null,    // 网址（可选）
    val notes: String? = null,  // 备注（可选）
    
    // 元数据
    val isFavorite: Boolean = false,    // 是否收藏
    val lastUsed: Long? = null,         // 最后使用时间
    val usageCount: Int = 0,            // 使用次数
    
    // 时间戳
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    
    // 安全相关
    val requiresBiometric: Boolean = false, // 是否需要生物识别才能查看
    val securityLevel: SecurityLevel = SecurityLevel.NORMAL // 安全级别
) {
    /**
     * 安全级别枚举
     */
    enum class SecurityLevel {
        LOW,        // 低 - 普通密码
        NORMAL,     // 正常 - 默认级别
        HIGH,       // 高 - 重要账号
        CRITICAL    // 关键 - 金融等敏感账号
    }
    
    /**
     * 获取分类颜色（根据分类ID）
     */
    fun getCategoryColor(): Int {
        return when (categoryId % 7) {
            0L -> 0xFF00D4FF.toInt()  // 科技蓝
            1L -> 0xFF00FF88.toInt()  // 科技绿
            2L -> 0xFF9D4EDD.toInt()  // 科技紫
            3L -> 0xFFFF2D95.toInt()  // 科技粉
            4L -> 0xFFFFAA00.toInt()  // 警告色
            5L -> 0xFF00FFE0.toInt()  // 青蓝
            else -> 0xFFB0B0C0.toInt() // 灰色
        }
    }
    
    /**
     * 获取安全级别图标
     */
    fun getSecurityIcon(): String {
        return when (securityLevel) {
            SecurityLevel.LOW -> "🔓"
            SecurityLevel.NORMAL -> "🔒"
            SecurityLevel.HIGH -> "🛡️"
            SecurityLevel.CRITICAL -> "🚨"
        }
    }
    
    /**
     * 获取格式化时间
     */
    fun getFormattedTime(): String {
        val date = Date(updatedAt)
        return android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", date).toString()
    }
    
    /**
     * 复制并更新修改时间
     */
    fun copyWithUpdate(
        title: String = this.title,
        username: String = this.username,
        password: String = this.password,
        categoryId: Long = this.categoryId,
        url: String? = this.url,
        notes: String? = this.notes,
        isFavorite: Boolean = this.isFavorite,
        securityLevel: SecurityLevel = this.securityLevel
    ): PasswordEntry {
        return this.copy(
            title = title,
            username = username,
            password = password,
            categoryId = categoryId,
            url = url,
            notes = notes,
            isFavorite = isFavorite,
            securityLevel = securityLevel,
            updatedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * 标记为已使用
     */
    fun markAsUsed(): PasswordEntry {
        return this.copy(
            lastUsed = System.currentTimeMillis(),
            usageCount = this.usageCount + 1
        )
    }
}